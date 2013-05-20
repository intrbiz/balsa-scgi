/*
 * Balsa SCGI Copyright (c) 2012, Chris Ellis All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intrbiz.balsa.scgi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;

/**
 * A worker of the SCGI Listener.
 * 
 * The SCGI worker will parse the socket, populating a SCGIRequest.
 * 
 * The request is then passed to a SCGIProcessor for processing.
 * 
 */
public class SCGIWorker implements Runnable
{
    private static final Charset SCGI_CHARSET = Charset.forName("ISO-8859-1");

    private final SCGIListener listener;

    private final BlockingQueue<Socket> runQueue;

    private final Thread thread;

    private Logger logger = Logger.getLogger(this.getClass());

    private final Counter requests;

    private final Timer requestDuration;

    private final Timer requestHeaderParseDuration;
    
    private final Timer requestProcessDuration;

    public SCGIWorker(SCGIListener listener, BlockingQueue<Socket> runQueue, ThreadFactory workerFactory, Timer requestDuration, Timer requestHeaderParseDuration, Timer requestProcessDuration)
    {
        super();
        this.listener = listener;
        this.runQueue = runQueue;
        this.thread = workerFactory.newThread(this);
        // create our metrics
        this.requests = Metrics.newCounter(SCGIWorker.class, "requests", this.thread.getName());
        this.requestDuration = requestDuration;
        this.requestHeaderParseDuration = requestHeaderParseDuration;
        this.requestProcessDuration = requestProcessDuration;
    }

    protected SCGIProcessor getProcessor()
    {
        return this.listener.getProcessor();
    }

    public void run()
    {
        // Request and Response
        SCGIRequest request = new SCGIRequest();
        SCGIResponse response = new SCGIResponse();
        // The client
        Socket client;
        // Enter the run loop
        while (this.listener.isRun())
        {
            try
            {
                client = this.runQueue.take();
                this.requests.inc();
                final TimerContext tCtx = this.requestDuration.time();
                try
                {
                    try
                    {
                        request.activate();
                        response.activate();
                        try
                        {
                            final TimerContext hpCtx = this.requestHeaderParseDuration.time();
                            try
                            {
                                InputStream input = new BufferedInputStream(client.getInputStream(), 1024);
                                OutputStream output = new BufferedOutputStream(client.getOutputStream(), 8192);
                                byte[] headers = this.readHeaders(input);
                                this.parseHeaders(headers, request);
                                this.readBodySeparator(input);
                                // set the streams
                                request.stream(input);
                                response.stream(output);
                            }
                            finally
                            {
                                hpCtx.stop();
                            }
                            // process
                            final TimerContext rpCtx = this.requestProcessDuration.time();
                            try
                            {
                                this.getProcessor().process(request, response);
                            }
                            finally
                            {
                                rpCtx.stop();
                            }
                        }
                        finally
                        {
                            // close
                            request.deactivate();
                            response.deactivate();
                            client.close();
                        }
                    }
                    catch (Throwable t)
                    {
                        // A throwable should not reach here - Fatal!
                        if (t instanceof OutOfMemoryError) logger.fatal("OUT OF MEMORY ERROR!");
                        logger.fatal("Terminated request due to uncaught throwable while processing request!", t);
                    }
                }
                finally
                {
                    tCtx.stop();
                }
            }
            catch (InterruptedException e)
            {
                /* expected */
            }
        }
    }

    public void start()
    {
        this.thread.start();
    }

    public void await()
    {
        try
        {
            this.thread.join();
        }
        catch (InterruptedException e)
        {
        }
    }

    protected void parseHeaders(byte[] headers, SCGIRequest request)
    {
        int start = 0;
        String name = null;
        for (int i = 0; i < headers.length; i++)
        {
            if (headers[i] == 0)
            {
                if (name == null)
                {
                    name = new String(headers, start, i - start, SCGI_CHARSET);
                }
                else
                {
                    request.variable(name, new String(headers, start, i - start, SCGI_CHARSET));
                    name = null;
                }
                start = i + 1;
            }
        }
    }

    protected void readBodySeparator(InputStream input) throws IOException
    {
        int r = input.read();
        if (r == -1) throw new IOException("Unexpected EOF while reading body separator");
        if (r != ',') throw new IOException("SCGI body separator not found where it should be, got #" + r + " instead");
    }

    protected byte[] readHeaders(InputStream input) throws IOException
    {
        int headerLength = this.readHeaderLength(input);
        // Get a buffer for the headers
        byte[] buffer = new byte[headerLength];
        int readLength = 0;
        // read in the headers
        while (readLength < headerLength)
        {
            int r = input.read(buffer, readLength, headerLength);
            if (r == -1) throw new IOException("Unexpected EOF while reading headers");
            readLength += r;
        }
        return buffer;
    }

    protected int readHeaderLength(InputStream input) throws IOException
    {
        int r;
        int length = 0;
        while ((r = input.read()) != -1)
        {
            if (r == ':') return length;
            if (r < '0' && '9' > r) throw new IOException("Unexpected character #" + r + " in headers length");
            length = length * 10 + (r - '0');
        }
        throw new IOException("Unexpected EOF while reading header length");
    }
}
