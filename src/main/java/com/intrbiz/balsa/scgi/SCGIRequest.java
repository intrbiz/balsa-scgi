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

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.parameter.ListParameter;
import com.intrbiz.balsa.parameter.Parameter;
import com.intrbiz.balsa.parameter.StringParameter;


/**
 * A SCGI request
 * 
 * It pulls information out of the SCGI headers.
 * 
 * It allows access to the input and output streams, but provides no processing of them
 * 
 */
public class SCGIRequest
{
    private Logger logger = Logger.getLogger(SCGIRequest.class);
    
    private int contentLength = 0;

    private String contentType;

    private String version;

    private String serverSoftware;

    private String serverName;

    private String serverAddress;

    private int serverPort;

    private String serverProtocol;

    private String remoteAddress;

    private int remotePort;

    private String requestMethod;

    private String requestUri;

    private String pathInfo;

    private String queryString;

    private String scriptName;

    private String scriptFileName;

    private String documentRoot;

    private Map<String, String> headers = new TreeMap<String, String>();

    private Map<String, String> scgiVariables = new TreeMap<String, String>();

    private Map<String, Parameter> parameters = new TreeMap<String, Parameter>();

    private Map<String, String> cookies = new TreeMap<String, String>();

    private InputStream input;

    private Object body;
    
    private long processingStart;
    
    private long processingEnd;

    public SCGIRequest()
    {
        super();
    }

    public void variable(String name, String value)
    {
        if (this.logger.isTraceEnabled()) this.logger.trace("SCGI Variable: " + name + " => " + value);
        if (name.startsWith("HTTP"))
        {
            this.headers.put(name.substring(5), value);
        }
        else if ("CONTENT_LENGTH".equals(name))
        {
            this.contentLength = Integer.parseInt(value);
        }
        else if ("CONTENT_TYPE".equals(name))
        {
            this.contentType = value;
        }
        else if ("SCGI".equals(name))
        {
            this.version = value;
        }
        else if ("SERVER_SOFTWARE".equals(name))
        {
            this.serverSoftware = value;
        }
        else if ("SERVER_NAME".equals(name))
        {
            this.serverName = value;
        }
        else if ("SERVER_ADDR".equals(name))
        {
            this.serverAddress = value;
        }
        else if ("SERVER_PORT".equals(name))
        {
            this.serverPort = Integer.parseInt(value);
        }
        else if ("SERVER_PROTOCOL".equals(name))
        {
            this.serverProtocol = value;
        }
        else if ("REMOTE_ADDR".equals(name))
        {
            this.remoteAddress = value;
        }
        else if ("REMOTE_PORT".equals(name))
        {
            this.remotePort = Integer.parseInt(value);
        }
        else if ("REQUEST_METHOD".equals(name))
        {
            this.requestMethod = value;
        }
        else if ("REQUEST_URI".equals(name))
        {
            this.requestUri = value;
        }
        else if ("PATH_INFO".equals(name))
        {
            this.pathInfo = value;
        }
        else if ("QUERY_STRING".equals(name))
        {
            this.queryString = value;
        }
        else if ("SCRIPT_NAME".equals(name))
        {
            this.scriptName = value;
        }
        else if ("SCRIPT_FILENAME".equals(name))
        {
            this.scriptFileName = value;
        }
        else if ("DOCUMENT_ROOT".equals(name))
        {
            this.documentRoot = value;
        }
        else
        {
            this.scgiVariables.put(name, value);
        }
    }

    public void stream(InputStream input)
    {
        this.input = input;
    }

    public void activate()
    {
    }

    public void deactivate()
    {
        // clear all state!
        this.input = null;
        this.headers.clear();
        this.scgiVariables.clear();
        this.parameters.clear();
        this.contentLength = 0;
        this.contentType = null;
        this.version = null;
        this.serverSoftware = null;
        this.serverName = null;
        this.serverAddress = null;
        this.serverPort = 0;
        this.serverProtocol = null;
        this.remoteAddress = null;
        this.remotePort = 0;
        this.requestMethod = null;
        this.requestUri = null;
        this.pathInfo = null;
        this.queryString = null;
        this.scriptName = null;
        this.scriptFileName = null;
        this.documentRoot = null;
    }

    /*
     * Accessors
     */

    public InputStream getInput()
    {
        return input;
    }

    public int getContentLength()
    {
        return contentLength;
    }

    public String getContentType()
    {
        return contentType;
    }

    public String getVersion()
    {
        return version;
    }

    public String getServerSoftware()
    {
        return serverSoftware;
    }

    public String getServerName()
    {
        return serverName;
    }

    public String getServerAddress()
    {
        return serverAddress;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public String getServerProtocol()
    {
        return serverProtocol;
    }

    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    public int getRemotePort()
    {
        return remotePort;
    }

    public String getRequestMethod()
    {
        return requestMethod;
    }

    public String getRequestUri()
    {
        return requestUri;
    }

    public String getPathInfo()
    {
        return pathInfo;
    }

    public String getQueryString()
    {
        return queryString;
    }

    public String getScriptName()
    {
        return scriptName;
    }

    public String getScriptFileName()
    {
        return scriptFileName;
    }

    public String getDocumentRoot()
    {
        return documentRoot;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public String getHeader(String name)
    {
        return this.headers.get(name.toUpperCase());
    }

    public Set<String> getHeaderNames()
    {
        return this.headers.keySet();
    }

    public Map<String, String> getVariables()
    {
        return scgiVariables;
    }

    public String getVariable(String name)
    {
        return this.scgiVariables.get(name.toUpperCase());
    }

    public Set<String> getVariableNames()
    {
        return this.scgiVariables.keySet();
    }

    public Map<String, Parameter> getParameters()
    {
        return parameters;
    }

    public Parameter getParameter(String name)
    {
        return this.parameters.get(name);
    }

    public void addParameter(Parameter parameter)
    {
        this.parameters.put(parameter.getName(), parameter);
    }

    public Set<String> getParameterNames()
    {
        return this.parameters.keySet();
    }

    public Collection<Parameter> getParameterValues()
    {
        return this.parameters.values();
    }

    public boolean containsParameter(String name)
    {
        return this.parameters.containsKey(name);
    }

    public String cookie(String name)
    {
        return this.cookies.get(name);
    }

    public void cookie(String name, String value)
    {
        this.cookies.put(name, value);
    }

    public Object getBody()
    {
        return body;
    }

    public void setBody(Object body)
    {
        this.body = body;
    }

    public String dumpRequest()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getRequestMethod()).append(" ").append(this.getPathInfo()).append("\r\n\r\n");
        //
        sb.append("CONTENT_LENGTH: ").append(this.contentLength).append("\r\n");
        sb.append("CONTENT_TYPE: ").append(this.contentType).append("\r\n");
        sb.append("SCGI: ").append(this.version).append("\r\n");
        sb.append("SERVER_SOFTWARE: ").append(this.serverSoftware).append("\r\n");
        sb.append("SERVER_NAME: ").append(this.serverName).append("\r\n");
        sb.append("SERVER_ADDR: ").append(this.serverAddress).append("\r\n");
        sb.append("SERVER_PORT: ").append(this.serverPort).append("\r\n");
        sb.append("SERVER_PROTOCOL: ").append(this.serverProtocol).append("\r\n");
        sb.append("REMOTE_ADDR: ").append(this.remoteAddress).append("\r\n");
        sb.append("REMOTE_PORT: ").append(this.remotePort).append("\r\n");
        sb.append("REQUEST_METHOD: ").append(this.requestMethod).append("\r\n");
        sb.append("REQUEST_URI: ").append(this.requestUri).append("\r\n");
        sb.append("PATH_INFO: ").append(this.pathInfo).append("\r\n");
        sb.append("QUERY_STRING: ").append(this.queryString).append("\r\n");
        sb.append("SCRIPT_NAME: ").append(this.scriptName).append("\r\n");
        sb.append("SCRIPT_FILENAME: ").append(this.scriptFileName).append("\r\n");
        sb.append("DOCUMENT_ROOT: ").append(this.documentRoot).append("\r\n");
        //
        sb.append("\r\nVariables:\r\n");
        for (Entry<String, String> var : this.getVariables().entrySet())
        {
            sb.append("\t").append(var.getKey()).append(" => ").append(var.getValue()).append("\r\n");
        }
        sb.append("\r\nHeaders:\r\n");
        for (Entry<String, String> hd : this.getHeaders().entrySet())
        {
            sb.append("\t").append(hd.getKey()).append(" => ").append(hd.getValue()).append("\r\n");
        }
        sb.append("\r\nParameters:\r\n");
        for (Parameter p : this.getParameterValues())
        {
            if (p instanceof StringParameter)
            {
                sb.append("\t").append(p.getName()).append(" => ").append(p.getStringValue()).append("\r\n");
            }
            else if (p instanceof ListParameter)
            {
                for (Parameter v : p.getListValue())
                {
                    if (v instanceof StringParameter)
                    {
                        sb.append("\t").append(p.getName()).append(" => ").append(v.getStringValue()).append("\r\n");
                    }
                }
            }
        }
        return sb.toString();
    }
    
    // Timing

    public final long getProcessingStart()
    {
        return processingStart;
    }

    public final void setProcessingStart(long processingStart)
    {
        this.processingStart = processingStart;
    }

    public final long getProcessingEnd()
    {
        return processingEnd;
    }

    public final void setProcessingEnd(long processingEnd)
    {
        this.processingEnd = processingEnd;
    }
}
