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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.intrbiz.balsa.http.HTTP.CacheControl;
import com.intrbiz.balsa.http.HTTP.Charsets;
import com.intrbiz.balsa.http.HTTP.ContentTypes;
import com.intrbiz.balsa.http.HTTP.Expires;
import com.intrbiz.balsa.http.HTTP.HTTPStatus;
import com.intrbiz.balsa.util.CookieBuilder;
import com.intrbiz.balsa.util.HTMLWriter;
import com.intrbiz.balsa.util.SocketOutputStream;

public class SCGIResponse
{
    // Thu, 01 Jan 1970 00:00:00 GMT
    public final SimpleDateFormat HEADER_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    private OutputStream output;

    private HTTPStatus status = HTTPStatus.OK;

    private Charset charset = Charsets.UTF8;

    private String contentType = ContentTypes.TEXT_HTML;

    private String cacheControl = CacheControl.NO_CACHE;

    private String expires = Expires.EXPIRED;

    private List<String> headers = new LinkedList<String>();

    private boolean sentHeaders = false;
    
    private SocketOutputStream bodyOutput = null;

    private Writer writer = null;

    private HTMLWriter htmlWriter = null;

    public SCGIResponse()
    {
        super();
    }

    public void activate()
    {
    }

    public void deactivate()
    {
        this.output = null;
        this.writer = null;
        this.htmlWriter = null;
        this.status = HTTPStatus.OK;
        this.charset = Charsets.UTF8;
        this.contentType = ContentTypes.TEXT_HTML;
        this.sentHeaders = false;
        this.cacheControl = CacheControl.NO_CACHE;
        this.expires = Expires.EXPIRED;
        this.headers.clear();
    }

    public void abortOnError(Throwable t)
    {
        if (this.isHeadersSent()) throw new IllegalStateException("An error occurred after data has been flushed to the web server, error handling cannot happen.", t);
        // Reset the response
        // Note: Do not reset the output stream!
        this.writer = null;
        this.htmlWriter = null;
        this.status = HTTPStatus.OK;
        this.charset = Charsets.UTF8;
        this.contentType = ContentTypes.TEXT_HTML;
        this.sentHeaders = false;
        this.cacheControl = CacheControl.NO_CACHE;
        this.expires = Expires.EXPIRED;
        this.headers.clear();
    }

    public void stream(OutputStream output)
    {
        this.output = output;
        this.bodyOutput = new SocketOutputStream(this.output);
    }

    public HTTPStatus getStatus()
    {
        return status;
    }

    public void status(HTTPStatus status)
    {
        this.status = status;
    }

    public void ok()
    {
        this.status(HTTPStatus.OK);
    }

    public void notFound()
    {
        this.status(HTTPStatus.NotFound);
    }

    public void error()
    {
        this.status(HTTPStatus.InternalServerError);
    }

    public void redirect(boolean permanent)
    {
        if (permanent)
            this.status(HTTPStatus.MovedPermanently);
        else
            this.status(HTTPStatus.Found);
    }

    public Charset getCharset()
    {
        return charset;
    }

    public void charset(Charset charset)
    {
        this.charset = charset;
    }

    public final String getContentType()
    {
        return contentType;
    }

    public void contentType(String contentType)
    {
        this.contentType = contentType;
    }

    public void plain()
    {
        this.contentType(ContentTypes.TEXT_PLAIN);
    }

    public void html()
    {
        this.contentType(ContentTypes.TEXT_HTML);
    }

    public void javascript()
    {
        this.contentType(ContentTypes.TEXT_JAVASCRIPT);
    }

    public void json()
    {
        this.contentType(ContentTypes.APPLICATION_JSON);
    }

    public void css()
    {
        this.contentType(ContentTypes.TEXT_CSS);
    }

    public String getCacheControl()
    {
        return this.cacheControl;
    }

    public void cacheControl(String value)
    {
        this.cacheControl = value;
    }

    public String getExpires()
    {
        return this.expires;
    }

    public void expires(Date value)
    {
        this.expires = HEADER_DATE_FORMAT.format(value);
    }

    public void expires(String value)
    {
        this.expires = value;
    }

    public void header(String name, Date value)
    {
        this.header(name, HEADER_DATE_FORMAT.format(value));
    }

    public void header(String name, String value)
    {
        this.headers.add(name + ": " + value);
    }

    public void redirect(String location, boolean permanent) throws IOException
    {
        this.redirect(permanent);
        this.header("Location", location);
        // TODO should we flush here (it'll get done when the processing is finished)?
        // flush the headers
        // this.sendHeaders();
    }

    public List<String> getHeaders()
    {
        return headers;
    }
    
    public CookieBuilder<SCGIResponse> setCookie()
    {
        return new CookieBuilder<SCGIResponse>()
        {
            @Override
            public SCGIResponse set()
            {
                header("Set-Cookie", this.build());
                return SCGIResponse.this;
            }
        };
    }
    
    public CookieBuilder<SCGIResponse> cookie()
    {
        return this.setCookie();
    }

    public void sendHeaders() throws IOException
    {
        if (!this.sentHeaders)
        {
            this.sentHeaders = true;
            // create the writer
            Writer headerWriter = new BufferedWriter(new OutputStreamWriter(this.output, Charsets.SCGI), 1024);
            // the status
            headerWriter.write("Status: ");
            headerWriter.write(String.valueOf(this.getStatus().getCode()));
            headerWriter.write(" ");
            headerWriter.write(this.getStatus().getMessage());
            // the content type
            headerWriter.write("\r\nContent-Type: ");
            headerWriter.write(this.getContentType());
            headerWriter.write("; charset=");
            headerWriter.write(this.getCharset().name().toLowerCase());
            // cache control
            if (this.getCacheControl() != null)
            {
                headerWriter.write("\r\nCacheControl: ");
                headerWriter.write(this.getCacheControl());
            }
            // expires
            if (this.getExpires() != null)
            {
                headerWriter.write("\r\nExpires: ");
                headerWriter.write(this.getExpires());
            }
            // write headers
            for (String header : this.getHeaders())
            {
                headerWriter.write("\r\n");
                headerWriter.write(header);
            }
            // write end of headers
            headerWriter.write("\r\n\r\n");
            // flush
            headerWriter.flush();
        }
    }

    public OutputStream getOutput() throws IOException
    {
        this.sendHeaders();
        return this.bodyOutput;
    }

    public Writer getWriter() throws IOException
    {
        if (this.writer == null) this.writer = new BufferedWriter(new OutputStreamWriter(this.getOutput(), this.getCharset()), 8192);
        return writer;
    }

    public HTMLWriter getHtmlWriter() throws IOException
    {
        if (this.htmlWriter == null) this.htmlWriter = new HTMLWriter(this.getWriter());
        return this.htmlWriter;
    }

    public void flush() throws IOException
    {
        this.sendHeaders();
        //
        if (this.sentHeaders)
        {
            if (this.htmlWriter != null)
            {
                this.htmlWriter.flush();
            }
            else if (this.writer != null)
            {
                this.writer.flush();
            }
            else
            {
                this.output.flush();
            }
        }
    }

    public boolean isHeadersSent()
    {
        return this.sentHeaders;
    }
}
