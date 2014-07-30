package com.intrbiz.balsa.util;

import java.util.Map;
import java.util.Set;

/**
 * A set of cookies of a request, etc
 */
public interface CookieSet
{
    /**
     * Get the value of a cookie
     * @param name the cookie name
     * @return the cookie value
     */
    String cookie(String name);

    /**
     * Add a cookie
     * @param name the cookie name
     * @param value the cookie value
     */
    void cookie(String name, String value);
    
    /**
     * Get a map of all cookies
     * @return the Map of name to value of cookies
     */
    Map<String, String> cookies();
    
    /**
     * Get the set of all cookie names
     * @return a Set of cookie names
     */
    Set<String> cookieNames();
    
    /**
     * Remove a cookie
     * @param name the cookie name
     */
    void removeCookie(String name);
}