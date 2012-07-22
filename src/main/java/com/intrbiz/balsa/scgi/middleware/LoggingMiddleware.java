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

package com.intrbiz.balsa.scgi.middleware;

import static com.intrbiz.balsa.util.Util.isEmpty;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.scgi.SCGIRequest;
import com.intrbiz.balsa.scgi.SCGIResponse;

/**
 * Log the request
 */
public class LoggingMiddleware extends AbstractMiddleware
{
    private Logger logger = Logger.getLogger(LoggingMiddleware.class);

    @Override
    public boolean before(SCGIRequest request, SCGIResponse response) throws IOException
    {
        request.setProcessingStart(System.nanoTime());
        return true;
    }

    @Override
    public void after(SCGIRequest request, SCGIResponse response) throws IOException
    {
        request.setProcessingEnd(System.nanoTime());
        // Log to trace
        if (logger.isDebugEnabled())
        {
            logger.debug("Processing request: " + request.getPathInfo() + (isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString()) + " " + request.getRequestMethod() + " in " + ((request.getProcessingEnd() - request.getProcessingStart())/1000) + "us");
        }
    }

}
