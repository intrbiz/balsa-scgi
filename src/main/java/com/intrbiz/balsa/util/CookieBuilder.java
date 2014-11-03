package com.intrbiz.balsa.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A fluent interface to build a cookie
 */
public abstract class CookieBuilder<T>
{
    // Thu, 01 Jan 1970 00:00:00 GMT
    public final SimpleDateFormat COOKIE_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    
    private String name;
    
    private String value;
    
    private String domain;
    
    private String path;
    
    private boolean httpOnly = false;
    
    private boolean secure = false;
    
    private Date expires;
    
    public CookieBuilder()
    {
        super();
    }
    
    /**
     * The name of this cookie
     */
    public CookieBuilder<T> name(String name)
    {
        this.name = name;
        return this;
    }
    
    /**
     * The value of this cookie
     */
    public CookieBuilder<T> value(String value)
    {
        this.value = value;
        return this;
    }
    
    /**
     * The domain this cookie is valid for
     */
    public CookieBuilder<T> domain(String domain)
    {
        this.domain = domain;
        return this;
    }
    
    /**
     * The path this cookie is valid for
     */
    public CookieBuilder<T> path(String path)
    {
        this.path = path;
        return this;
    }
    
    /**
     * Mark this cookie as HTTP only (not available via JS etc)
     */
    public CookieBuilder<T> httpOnly()
    {
        this.httpOnly = true;
        return this;
    }
    
    /**
     * Mark this cookie as HTTP only (not available via JS etc)
     * @param httpOnly set the flag if true
     */
    public CookieBuilder<T> httpOnly(boolean httpOnly)
    {
        this.httpOnly = httpOnly;
        return this;
    }
    
    /**
     * Mark this cookie as only available over a secure connection (HTTPS)
     */
    public CookieBuilder<T> secure()
    {
        this.secure = true;
        return this;
    }
    
    /**
     * Mark this cookie as only available over a secure connection (HTTPS)
     * @param secure set the flag if true
     */
    public CookieBuilder<T> secure(boolean secure)
    {
        this.secure = secure;
        return this;
    }
    
    /**
     * Set the expiry date of the cookie
     */
    public CookieBuilder<T> expires(Date expires)
    {
        this.expires = expires;
        return this;
    }
    
    /**
     * The cookie expires after the given number of milliseconds
     * @param interval the number of milliseconds to expire after
     */
    public CookieBuilder<T> expiresAfter(long interval)
    {
        // compute the expires
        this.expires = new Date(System.currentTimeMillis() + interval);
        return this;
    }
    
    /**
     * The cookie expires after the given number of milliseconds
     * @param interval the interval (of the given units) to expire after
     * @param unit the TimeUnit of the interval
     */
    public CookieBuilder<T> expiresAfter(long interval, TimeUnit unit)
    {
        // compute the expires
        this.expires = new Date(System.currentTimeMillis() + unit.toMillis(interval));
        return this;
    }
    
    /**
     * Build the cookie content.  You probably want to use set()
     * @return the cookie text
     */
    public String build()
    {
        StringBuilder sb = new StringBuilder();
        // name=value
        sb.append(this.name);
        sb.append("=");
        sb.append(this.value);
        // flags
        if (! Util.isEmpty(this.domain))
        {
            sb.append("; Domain=");
            sb.append(this.domain);
        }
        if (! Util.isEmpty(this.path))
        {
            sb.append("; Path=");
            sb.append(this.path);
        }
        if (this.httpOnly)
        {
            sb.append("; HttpOnly");
        }
        if (this.secure)
        {
            sb.append("; Secure");
        }
        if (this.expires != null)
        {
            sb.append("; Expires=");
            sb.append(COOKIE_DATE_FORMAT.format(this.expires));
        }
        sb.append(";");
        return sb.toString();
    }
    
    /**
     * Set the cookie
     */
    public abstract T set();
}
