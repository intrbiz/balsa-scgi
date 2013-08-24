package com.intrbiz.balsa.scgi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intrbiz.balsa.http.HTTP;
import com.intrbiz.balsa.http.HTTP.HTTPStatus;
import com.intrbiz.balsa.http.HTTP.SCGI;
import com.intrbiz.balsa.util.Util;

/**
 * <p>
 * A simple SCGI Client
 * </p>
 * <p>
 * For Example:
 * </p>
 * <code><pre> 
 *      try(SCGIClientResponse res = SCGI.client("localhost", 8099).http().server("localhost").get("/").executeRequest())
 *      {
 *          System.out.println(res.getStatus());
 *          for (Entry<String, String> header : res.getHeaders().entrySet())
 *          {
 *              System.out.println(header.getKey() + " => " + header.getValue());
 *          }
 *          System.out.println(res.getContentAsString());
 *      }
 *  </pre></code>
 */
public class SCGIClient
{
    private String host;

    private int port;

    private List<SCGIVar> vars = new LinkedList<SCGIVar>();

    private ContentEntity content = null;

    public SCGIClient(String host, int port)
    {
        super();
        this.host = host;
        this.port = port;
        this.reset();
    }

    public SCGIClient reset()
    {
        this.vars.clear();
        this.defaultVars();
        this.defaultHttpHeaders();
        return this;
    }

    protected void defaultVars()
    {
        this.setVar(HTTP.SCGI.CONTENT_LENGTH, "0");
        this.setVar(HTTP.SCGI.CONTENT_TYPE, "");
        this.setVar(HTTP.SCGI.SCGI, "1");
        this.setVar(HTTP.SCGI.SERVER_SOFTWARE, "Balsa SCGI Client");
        this.setVar(HTTP.SCGI.SERVER_NAME, "localhost");
        this.setVar(HTTP.SCGI.SERVER_ADDR, "");
        this.setVar(HTTP.SCGI.SERVER_PORT, "80");
        this.setVar(HTTP.SCGI.SERVER_PROTOCOL, "HTTP/1.1");
        this.setVar(HTTP.SCGI.REMOTE_ADDR, "127.0.0.1");
        this.setVar(HTTP.SCGI.REMOTE_PORT, "37184");
        this.setVar(HTTP.SCGI.REQUEST_METHOD, "GET");
        this.setVar(HTTP.SCGI.REQUEST_SCHEME, "http");
        this.setVar(HTTP.SCGI.REQUEST_URI, "/");
        this.setVar(HTTP.SCGI.PATH_INFO, "/");
        this.setVar(HTTP.SCGI.QUERY_STRING, "");
        this.setVar(HTTP.SCGI.SCRIPT_NAME, "");
        this.setVar(HTTP.SCGI.SCRIPT_FILENAME, "");
        this.setVar(HTTP.SCGI.DOCUMENT_ROOT, "/var/www/");
    }

    protected void defaultHttpHeaders()
    {
        this.setHttpHeader("HOST", "localhost");
        this.setHttpHeader("USER_AGENT", "Mozilla/5.0 (Linux x86_64; rv:23.0) Gecko/20100101 Firefox/23.0");
        this.setHttpHeader("ACCEPT", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        this.setHttpHeader("ACCEPT_LANGUAGE", "en-gb,en;q=0.5");
        this.setHttpHeader("ACCEPT_ENCODING", "gzip, deflate");
        this.setHttpHeader("CONNECTION", "keep-alive");
    }

    public SCGIClient setVar(String name, String value)
    {
        this.setVar(new SCGIVar(name, value));
        return this;
    }

    public SCGIClient setVar(SCGIVar var)
    {
        SCGIVar evar = this.getVar(var.name);
        if (evar == null)
            this.vars.add(var);
        else
            evar.value = var.value;
        return this;
    }

    public List<SCGIVar> getVars()
    {
        return this.vars;
    }

    public SCGIVar getVar(String name)
    {
        for (SCGIVar var : this.getVars())
        {
            if (name.equals(var.name)) return var;
        }
        return null;
    }

    public ContentEntity getContent()
    {
        return content;
    }

    public SCGIClient setContent(ContentEntity content)
    {
        this.content = content;
        return this;
    }

    public SCGIClient get(String uri)
    {
        this.setVar(HTTP.SCGI.REQUEST_METHOD, "GET");
        this.setVar(HTTP.SCGI.REQUEST_URI, uri);
        this.setVar(HTTP.SCGI.PATH_INFO, uri);
        return this;
    }

    public QueryStringBuilder queryString()
    {
        return new QueryStringBuilder(this);
    }

    public SCGIClient setHttpHeader(String name, String value)
    {
        this.setVar("HTTP_" + name.toUpperCase(), value);
        return this;
    }

    public SCGIClient server(String serverName)
    {
        this.setVar(SCGI.SERVER_NAME, serverName);
        this.setHttpHeader("HOST", serverName);
        return this;
    }

    public SCGIClient http()
    {
        this.setVar(SCGI.REQUEST_SCHEME, "http");
        this.setVar(SCGI.SERVER_PORT, "80");
        return this;
    }

    public SCGIClient https()
    {
        this.setVar(SCGI.REQUEST_SCHEME, "https");
        this.setVar(SCGI.SERVER_PORT, "443");
        return this;
    }

    public SCGIClientResponse executeRequest() throws IOException
    {
        this.finaliseHeaders();
        Socket sock = new Socket(this.host, this.port);
        OutputStream out = new BufferedOutputStream(sock.getOutputStream());
        // send the vars
        this.writeNetString(this.assembleHeaders(), out);
        // send the content
        out.flush();
        // return the response
        return new SCGIClientResponse(sock);
    }

    private void finaliseHeaders()
    {
        // add the content length
        int length = this.content == null ? 0 : this.content.getLength();
        this.setVar("CONTENT_LENGTH", String.valueOf(length));
    }

    private byte[] assembleHeaders() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (SCGIVar var : this.getVars())
        {
            baos.write(var.name.getBytes(HTTP.Charsets.SCGI));
            baos.write(new byte[] { 0 });
            baos.write(var.value.getBytes(HTTP.Charsets.SCGI));
            baos.write(new byte[] { 0 });
        }
        return baos.toByteArray();
    }

    private void writeNetString(byte[] string, OutputStream to) throws IOException
    {
        byte[] length = String.valueOf(string.length).getBytes(HTTP.Charsets.SCGI);
        to.write(length);
        to.write(new byte[] { ':' });
        to.write(string);
        to.write(new byte[] { ',' });
    }

    public static class SCGIVar
    {
        public final String name;

        public String value;

        public SCGIVar(String name, String value)
        {
            this.name = name;
            this.value = value;
        }

    }

    public static interface ContentEntity
    {
        public int getLength();

        public void writeTo(OutputStream out) throws IOException;
    }

    public static class QueryStringBuilder
    {
        private final SCGIClient client;

        private boolean ns = false;

        private StringBuilder queryString = new StringBuilder();

        private QueryStringBuilder(SCGIClient client)
        {
            this.client = client;
        }

        public QueryStringBuilder param(String name)
        {
            return this.param(name, "");
        }

        public QueryStringBuilder param(String name, String value)
        {
            // append a param
            if (ns) this.queryString.append("&");
            try
            {
                this.queryString.append(URLEncoder.encode(name, HTTP.Charsets.UTF8.name()));
                this.queryString.append("=");
                this.queryString.append(URLEncoder.encode(name, HTTP.Charsets.UTF8.name()));
            }
            catch (UnsupportedEncodingException e)
            {
            }
            ns = true;
            return this;
        }

        public SCGIClient complete()
        {
            this.client.setVar(HTTP.SCGI.QUERY_STRING, this.toString());
            return this.client;
        }

        public String toString()
        {
            return this.queryString.toString();
        }
    }

    public static class SCGIClientResponse implements Closeable
    {
        private static final Pattern STATUS_LINE = Pattern.compile("\\AStatus: ([0-9]+) (.+)\\z");
        
        private final Socket socket;

        private HTTPStatus status;

        private String message;

        private Map<String, String> headers = new TreeMap<String, String>();

        private InputStream inputStream;

        public SCGIClientResponse(Socket socket) throws IOException
        {
            super();
            this.socket = socket;
            this.inputStream = new BufferedInputStream(this.socket.getInputStream());
            this.parse();
        }
        
        private String readLine() throws IOException
        {
            // not efficient but acceptable for our purposes
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int c;
            while ((c = this.inputStream.read()) != -1)
            {
                if (c == '\r')
                {
                    int n = this.inputStream.read();
                    if (n == -1)
                    {
                        break;
                    }
                    else if (n == '\n')
                    {
                        break;
                    }
                    else
                    {
                        buffer.write(c);
                    }
                }
                else
                {
                    buffer.write(c);
                }
            }
            return new String(buffer.toByteArray(), HTTP.Charsets.SCGI);
        }

        private void parse() throws IOException
        {
            // read the status
            String statusLine = this.readLine();
            if (Util.isEmpty(statusLine)) throw new IOException("Malformed SCGI response: the response must start with a status line.");
            Matcher statusMatch = STATUS_LINE.matcher(statusLine);
            if (! statusMatch.matches()) throw new IOException("Malformed SCGI response: the status line is invalid");
            this.status = HTTPStatus.valueOf(Integer.parseInt(statusMatch.group(1)));
            this.message = statusMatch.group(2);
            // read headers
            String header;
            while (! Util.isEmpty(header = this.readLine()))
            {
                int sep = header.indexOf(": ");
                if (sep != -1)
                {
                    this.setHeader(header.substring(0, sep), header.substring(sep + 1));
                }
            }
        }

        public HTTPStatus getStatus()
        {
            return this.status;
        }

        public String getStatusMessage()
        {
            return this.message;
        }

        public void setHeader(String name, String value)
        {
            this.headers.put(name, value);
        }

        public Map<String, String> getHeaders()
        {
            return this.headers;
        }

        public String getHeader(String name)
        {
            return this.headers.get(name);
        }

        public InputStream getContent()
        {
            return this.inputStream;
        }
        
        public byte[] getContentAsBytes() throws IOException
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int r;
            while ((r = this.inputStream.read(b)) != -1)
            {
                baos.write(b, 0, r);
            }
            return baos.toByteArray();
        }
        
        public String getContentAsString() throws IOException
        {
            return new String(this.getContentAsBytes(), HTTP.Charsets.UTF8);
        }
        
        public void close() throws IOException
        {
            this.socket.close();
        }
    }
}
