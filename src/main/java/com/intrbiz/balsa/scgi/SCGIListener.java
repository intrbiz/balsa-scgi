/*
 * Balsa SCGI
 * Copyright (c) 2012, Chris Ellis
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */

package com.intrbiz.balsa.scgi;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.intrbiz.balsa.SCGIException;
import com.intrbiz.gerald.source.IntelligenceSource;
import com.intrbiz.gerald.witchcraft.Witchcraft;

/**
 * A simple 'pre-forked' SCGI server
 * 
 * Note: This is based on the Balsa SCGI implementation, modified to be more standalone.
 * 
 */
public class SCGIListener implements Runnable
{
    public static final int DEFAULT_PORT = 8090;

    public static final int DEFAULT_POOL_SIZE = 16;

    private int port = DEFAULT_PORT;

    private int poolSize = DEFAULT_POOL_SIZE;

    protected SCGIProcessor processor;

    private volatile boolean run = false;

    private ServerSocket server;
    
    private Thread listenerThread;

    private ThreadFactory workerFactory;

    private SCGIWorker[] workers;

    private BlockingQueue<Socket> runQueue;

    private Logger logger = Logger.getLogger(SCGIListener.class);
    
    private final Counter accepts;
    
    private final Timer requestDuration;
    
    private final Timer requestHeaderParseDuration;
    
    private final Timer requestProcessDuration;

    public SCGIListener()
    {
        super();
        // setup the metrics
        IntelligenceSource source = Witchcraft.get().source("com.intrbiz.balsa");
        this.accepts                    = source.getRegistry().counter(Witchcraft.name(SCGIListener.class, "accepts"));
        this.requestDuration            = source.getRegistry().timer(  Witchcraft.name(SCGIListener.class, "request-duration"));
        this.requestHeaderParseDuration = source.getRegistry().timer(  Witchcraft.name(SCGIListener.class, "request-header-parse-duration"));
        this.requestProcessDuration     = source.getRegistry().timer(  Witchcraft.name(SCGIListener.class, "request-process-duration"));
    }

    public SCGIListener(int port)
    {
        this();
        this.port = port;
    }

    public SCGIListener(int port, int poolSize)
    {
        this();
        this.port = port;
        this.poolSize = poolSize;
    }
    
    public SCGIListener(int port, int poolSize, SCGIProcessor processor)
    {
        this();
        this.port = port;
        this.poolSize = poolSize;
        this.processor = processor;
    }

    public int getPort()
    {
        return port;
    }

    /**
     * The port to listen on
     * @param port
     * returns void
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    public int getPoolSize()
    {
        return poolSize;
    }

    /**
     * The number of worker threads
     * @param poolSize
     * returns void
     */
    public void setPoolSize(int poolSize)
    {
        this.poolSize = poolSize;
    }

    public SCGIProcessor getProcessor()
    {
        return processor;
    }

    /**
     * The processor to handler the requests
     * @param processor
     * returns void
     */
    public void setProcessor(SCGIProcessor processor)
    {
        this.processor = processor;
    }

    boolean isRun()
    {
        return this.run;
    }

    /**
     * Start the SCGI Listener
     * 
     * This will create a bind a socket, create a thread and start listening for requests.
     * 
     * @throws SCGIException
     * returns void
     */
    public void start() throws SCGIException
    {
        try
        {
            this.listenerThread = new Thread(new ThreadGroup("SCGI"), this, "SCGIListener");
            // factory
            this.workerFactory = new ThreadFactory() {
                
                private int count = 0;
                private ThreadGroup group = new ThreadGroup("SCGI");
                
                public Thread newThread(Runnable r)
                {
                    return new Thread(this.group, r, "SCGIWorker-" + (++count));
                }
            };
            // runqueue
            this.runQueue = new LinkedBlockingQueue<Socket>(this.getPoolSize() * 2);
            // prefork
            this.run = true;
            this.workers = new SCGIWorker[this.getPoolSize()];
            for (int i = 0; i < this.workers.length; i++)
            {
                this.workers[i] = new SCGIWorker(this, this.runQueue, this.workerFactory, this.requestDuration, this.requestHeaderParseDuration, this.requestProcessDuration);
                this.workers[i].start();
            }
            // listen
            this.server = new ServerSocket(this.getPort());
            this.server.setSoTimeout(20000);
            // start the listener
            this.listenerThread.start();
        }
        catch (BindException e)
        {
            throw new SCGIException("Failed to start SCGI Listener, could not bind to socket", e);
        }
        catch (IOException e)
        {
            throw new SCGIException("Failed to start SCGI Listener", e);
        }
    }

    public void run()
    {
        while (this.run)
        {
            try
            {
                // Place the socket onto the run queue
                Socket client = this.server.accept();
                this.accepts.inc();
                this.runQueue.offer(client);
            }
            catch (SocketTimeoutException e)
            {
                /* expected */
            }
            catch (IOException e)
            {
                logger.fatal("Error during listener run loop", e);
            }
        }
    }

    public void shutdown()
    {
        this.stop();
        if (this.workers != null)
        {
            for (SCGIWorker worker : this.workers)
            {
                worker.await();
            }
        }
    }

    public void stop()
    {
        if (this.server != null)
        {
            try
            {
                this.run = false;
                this.runQueue.notifyAll();
                this.server.close();
            }
            catch (Exception e)
            {
            }
        }
    }
}
