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

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.intrbiz.balsa.scgi.SCGIListener;
import com.intrbiz.balsa.scgi.SCGIProcessor;
import com.intrbiz.balsa.scgi.SCGIRequest;
import com.intrbiz.balsa.scgi.SCGIResponse;
import com.intrbiz.balsa.scgi.middleware.CookieMiddleware;
import com.intrbiz.balsa.scgi.middleware.LoggingMiddleware;
import com.intrbiz.balsa.scgi.middleware.MiddlewareProcessor;
import com.intrbiz.balsa.scgi.middleware.QueryStringMiddleware;



public class SCGIExample
{
    public static void main(String[] args) throws Exception
    {
        /* Log to console */
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        /* Construct a listener, on port 8080 with 20 workers */
        int port = 8090;
        int threads = 20;
        SCGIListener listener = new SCGIListener(port, threads);

        /* The processor */
        SCGIProcessor processor = new SCGIProcessor() {
            public void process(SCGIRequest request, SCGIResponse response) throws IOException
            {
                response.ok();                              // 200 OK
                response.plain();                           // text/plain
                response.getWriter().write("Hello World");  // response body
                response.flush();                           // always flush the response
            }
        };

        /* The processing chain, using middleware to parse the query string, etc.
         * The chain is: log -> cookies -> query string -> processor */
        SCGIProcessor chain = processor;
        chain = new MiddlewareProcessor(new QueryStringMiddleware(), chain);  // parse query string
        chain = new MiddlewareProcessor(new CookieMiddleware(), chain);       // parse cookies
        chain = new MiddlewareProcessor(new LoggingMiddleware(), chain);      // log the request

        /* Tell the listener to use the processing chain */
        listener.setProcessor(chain);

        /* Start the listener */
        listener.start();
    }
}
