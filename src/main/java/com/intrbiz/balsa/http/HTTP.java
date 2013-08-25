package com.intrbiz.balsa.http;

import java.nio.charset.Charset;

import com.intrbiz.balsa.scgi.SCGIClient;

public class HTTP
{
    /**
     * Common charsets
     */
    public static class Charsets
    {
        public static final Charset UTF8 = Charset.forName("UTF-8");

        public static final Charset SCGI = Charset.forName("ISO-8859-1");
    }

    /**
     * Common content types
     */
    public static class ContentTypes
    {
        public static final String TEXT_PLAIN = "text/plain";

        public static final String TEXT_HTML = "text/html";

        public static final String TEXT_CSS = "text/css";

        public static final String TEXT_JAVASCRIPT = "text/javascript";

        public static final String APPLICATION_JSON = "application/json";

        public static final String APPLICATION_XML = "application/xml";
        
        public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    }

    /**
     * The response status
     */
    public static enum HTTPStatus {
        /* 1xx */
        Continue(100, "Continue"), 
        SwitchingProtocols(101, "Switching Protocols"),
        /* 2xx */
        OK(200, "OK"), 
        Created(201, "Created"), 
        Accepted(202, "Accepted"), 
        NonAuthoritativeInformation(203, "Non-Authoritative Information"), 
        NoContent(204, "No Content"), 
        ResetContent(205, "Reset Content"), 
        PartialContent(206, ""),
        /* 3xx */
        MultipleChoices(300, "Multiple Choices"), 
        MovedPermanently(301, "Moved Permanently"), 
        Found(302, "Found"), 
        SeeOther(303, "See Other"), 
        NotModified(304, "Not Modified"), 
        UseProxy(305, "Use Proxy"), 
        TemporaryRedirect(307, "Temporary Redirect"),
        /* 4xx */
        BadRequest(400, "Bad Request"), 
        Unauthorized(401, "Unauthorized"), 
        PaymentRequired(402, "Payment Required"), 
        Forbidden(403, "Forbidden"), 
        NotFound(404, "Not Found"), 
        MethodNotAllowed(405, "Method Not Allowed"), 
        NotAcceptable(406, "Not Acceptable"), 
        ProxyAuthenticationRequired(407, "Proxy Authentication Required"), 
        RequestTimeout(408, "Request Timeout"), 
        Conflict(409, "Conflict"), Gone(410, "Gone"), 
        LengthRequired(411, "Length Required"), 
        PreconditionFailed(412, "Precondition Failed"), 
        RequestEntityTooLarge(413, "Request Entity Too Large"), 
        RequestURITooLong(414, "Request-URI Too Long"), 
        UnsupportedMediaType(415, "Unsupported Media Type"), 
        RequestedRangeNotSatisfiable(416, "Requested Range Not Satisfiable"), 
        ExpectationFailed(417, "Expectation Failed"),
        /* 5xx */
        InternalServerError(500, "Internal Server Error"), 
        NotImplemented(501, "Not Implemented"), 
        BadGateway(502, "Bad Gateway"), 
        ServiceUnavailable(503, "Service Unavailable"), 
        GatewayTimeout(504, "Gateway Timeout"), 
        HTTPVersionNotSupported(505, "HTTP Version Not Supported");

        private final int code;

        private final String message;

        private HTTPStatus(int code, String message)
        {
            this.code = code;
            this.message = message;
        }

        public int getCode()
        {
            return this.code;
        }

        public String getMessage()
        {
            return this.message;
        }
        
        public String toString()
        {
            return this.code + " " + this.message;
        }
        
        public static HTTPStatus valueOf(int code)
        {
            for (HTTPStatus stat : HTTPStatus.values())
            {
                if (stat.getCode() == code) return stat;
            }
            throw new IllegalArgumentException("No such HTTP status: " + code);
        }
    }

    public static class CacheControl
    {
        public static final String NO_CACHE = "no-cache, no-store, max-age=0, must-revalidate";
    }

    public static class Expires
    {
        public static final String EXPIRED = "Thu, 01 Jan 1970 00:00:00 GMT";
    }

    public static class SCGI
    {
        public static final String CONTENT_LENGTH = "CONTENT_LENGTH";

        public static final String CONTENT_TYPE = "CONTENT_TYPE";

        public static final String SCGI = "SCGI";

        public static final String SERVER_SOFTWARE = "SERVER_SOFTWARE";

        public static final String SERVER_NAME = "SERVER_NAME";

        public static final String SERVER_ADDR = "SERVER_ADDR";

        public static final String SERVER_PORT = "SERVER_PORT";

        public static final String SERVER_PROTOCOL = "SERVER_PROTOCOL";

        public static final String REMOTE_ADDR = "REMOTE_ADDR";

        public static final String REMOTE_PORT = "REMOTE_PORT";

        public static final String REQUEST_METHOD = "REQUEST_METHOD";

        public static final String REQUEST_SCHEME = "REQUEST_SCHEME";

        public static final String REQUEST_URI = "REQUEST_URI";

        public static final String PATH_INFO = "PATH_INFO";

        public static final String QUERY_STRING = "QUERY_STRING";

        public static final String SCRIPT_NAME = "SCRIPT_NAME";

        public static final String SCRIPT_FILENAME = "SCRIPT_FILENAME";

        public static final String DOCUMENT_ROOT = "DOCUMENT_ROOT";

        /**
         * Syntactic sugar to create a SCGI client
         */
        public static final SCGIClient client(String host, int port)
        {
            return new SCGIClient(host, port);
        }
    }
}
