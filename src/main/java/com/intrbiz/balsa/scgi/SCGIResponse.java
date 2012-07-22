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

import com.intrbiz.balsa.util.HTMLWriter;


public class SCGIResponse
{
    // Thu, 01 Jan 1970 00:00:00 GMT
    public static final SimpleDateFormat HEADER_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    /**
     * Common charsets
     */
    public static final class Charsets
    {
        public static final Charset UTF8 = Charset.forName("UTF-8");

        public static final Charset SCGI = Charset.forName("ISO-8859-1");
    }

    /**
     * Common content types
     */
    public static final class ContentTypes
    {
        public static final String TEXT_PLAIN = "text/plain";

        public static final String TEXT_HTML = "text/html";

        public static final String TEXT_CSS = "text/css";

        public static final String TEXT_JAVASCRIPT = "text/javascript";

        public static final String APPLICATION_JSON = "application/json";
    }

    /**
     * The response status
     */
    public static final class Status
    {
        /**
         * The response ok
         */
        public static final Status OK = new Status(200, "Ok");

        /**
         * The response is a redirect
         */
        public static final Status REDIRECT = new Status(302, "Hey, look over there");

        /**
         * The response is a redirect (permanent)
         */
        public static final Status REDIRECT_PERMANENT = new Status(301, "Hey, look over there");

        /**
         * The response is a not found error
         */
        public static final Status NOT_FOUND = new Status(404, "Opps, lost that one");

        /**
         * The response is a internal server error
         */
        public static final Status INTERNAL_ERROR = new Status(500, "Opps");

        /**
         * The status code
         */
        public final int code;

        /**
         * The status message
         */
        public final String message;

        public Status(int code, String message)
        {
            this.code = code;
            this.message = message;
        }

        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + code;
            return result;
        }

        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Status other = (Status) obj;
            if (code != other.code) return false;
            return true;
        }
    }

    public static final class CacheControl
    {
        public static final String NO_CACHE = "no-cache, no-store, max-age=0, must-revalidate";
    }

    public static final class Expires
    {
        public static final String EXPIRED = "Thu, 01 Jan 1970 00:00:00 GMT";
    }

    private OutputStream output;

    private Status status = Status.OK;

    private Charset charset = Charsets.UTF8;

    private String contentType = ContentTypes.TEXT_HTML;

    private String cacheControl = CacheControl.NO_CACHE;

    private String expires = Expires.EXPIRED;

    private List<String> headers = new LinkedList<String>();

    private boolean sentHeaders = false;

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
        this.status = Status.OK;
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
        this.status = Status.OK;
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
    }

    public Status getStatus()
    {
        return status;
    }

    public void status(Status status)
    {
        this.status = status;
    }

    public void ok()
    {
        this.status(Status.OK);
    }

    public void notFound()
    {
        this.status(Status.NOT_FOUND);
    }

    public void error()
    {
        this.status(Status.INTERNAL_ERROR);
    }

    public void redirect(boolean permanent)
    {
        if (permanent)
            this.status(Status.REDIRECT_PERMANENT);
        else
            this.status(Status.REDIRECT);
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
        // flush the headers
        this.sendHeaders();
    }

    public List<String> getHeaders()
    {
        return headers;
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
            headerWriter.write(String.valueOf(this.getStatus().code));
            headerWriter.write(" ");
            headerWriter.write(this.getStatus().message);
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
        return output;
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
