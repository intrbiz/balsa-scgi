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

package com.intrbiz.balsa.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.intrbiz.balsa.parameter.ListParameter;
import com.intrbiz.balsa.parameter.Parameter;
import com.intrbiz.balsa.parameter.StringParameter;
import com.intrbiz.balsa.scgi.SCGIRequest;

/**
 * Basic utility functions
 */
public class Util
{
    /**
     * Parse the given query string
     * @param query
     * @param request
     * returns void
     */
    public final static void parseQueryString(String query, SCGIRequest request)
    {
        if (!isEmpty(query))
        {
            int spos = 0, pos = 0;
            while ((pos = query.indexOf("&", spos)) != -1)
            {
                parseParameter(query.substring(spos, pos), request);
                spos = pos + 1;
            }
            parseParameter(query.substring(spos), request);
        }
    }

    /**
     * Parse the given parameter
     * @param parameter
     * @param request
     * returns void
     */
    public final static void parseParameter(String parameter, SCGIRequest request)
    {
        int pos = parameter.indexOf("=");
        if (pos != -1)
        {
            try
            {
                // TODO: Charset
                String name = URLDecoder.decode(parameter.substring(0, pos), "UTF-8");
                String value = URLDecoder.decode(parameter.substring(pos + 1), "UTF-8");
                Parameter jparam = new StringParameter(name, value);
                // add
                if (request.containsParameter(name))
                {
                    Parameter fparam = request.getParameter(name);
                    if (fparam instanceof ListParameter)
                    {
                        ((ListParameter) fparam).addValue(jparam);
                    }
                    else
                    {
                        request.addParameter(new ListParameter(name, fparam, jparam));
                    }
                }
                else
                {
                    request.addParameter(jparam);
                }
            }
            catch (UnsupportedEncodingException e)
            {
            }
        }
    }

    /**
     * Parse the given cookies
     * @param cookies
     * @param request
     * returns void
     */
    public final static void parseCookies(String cookies, SCGIRequest request)
    {
        if (!isEmpty(cookies))
        {
            int spos = 0, pos = 0;
            while ((pos = cookies.indexOf("; ", spos)) != -1)
            {
                parseCookie(cookies.substring(spos, pos), request);
                spos = pos + 1;
            }
            parseCookie(cookies.substring(spos), request);
        }
    }

    /**
     * Parse a cookie
     * @param cookie
     * @param request
     * returns void
     */
    public final static void parseCookie(String cookie, SCGIRequest request)
    {
        int pos = cookie.indexOf("=");
        if (pos != -1) request.cookie(cookie.substring(0, pos), cookie.substring(pos + 1));
    }
    
    /**
     * Is the given string null or empty?
     * @param s
     * @return
     * returns boolean
     */
    public final static boolean isEmpty(String s)
    {
        return (s == null || s.length() <= 0);
    }
}
