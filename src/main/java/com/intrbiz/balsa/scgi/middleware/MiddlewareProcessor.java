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

import com.intrbiz.balsa.scgi.SCGIMiddleware;
import com.intrbiz.balsa.scgi.SCGIProcessor;
import com.intrbiz.balsa.scgi.SCGIRequest;
import com.intrbiz.balsa.scgi.SCGIResponse;

/**
 * Adapt Middleware to a processor, forming a processing chain
 */
public class MiddlewareProcessor implements SCGIProcessor
{
    private final SCGIMiddleware middleware;

    private final SCGIProcessor processor;

    public MiddlewareProcessor(SCGIMiddleware middleware, SCGIProcessor processor)
    {
        super();
        this.middleware = middleware;
        this.processor = processor;
    }

    public void process(SCGIRequest request, SCGIResponse response) throws Throwable
    {
        // Call before, abort if false
        if (!this.middleware.before(request, response)) return;
        try
        {
            this.processor.process(request, response);
        }
        finally
        {
            // Always call after
            this.middleware.after(request, response);
        }
    }

}
