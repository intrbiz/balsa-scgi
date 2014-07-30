package com.intrbiz.balsa.util;


public class CookiesParser
{
    /**
     * Parse the given cookies
     * @param cookies
     * @param request
     * returns void
     */
    public final static void parseCookies(String cookies, CookieSet request)
    {
        if (!Util.isEmpty(cookies))
        {
            int spos = 0, pos = 0;
            while ((pos = cookies.indexOf("; ", spos)) != -1)
            {
                parseCookie(cookies.substring(spos, pos), request);
                spos = pos + 2;
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
    public final static void parseCookie(String cookie, CookieSet request)
    {
        int pos = cookie.indexOf("=");
        if (pos != -1) request.cookie(cookie.substring(0, pos), cookie.substring(pos + 1));
    }
}
