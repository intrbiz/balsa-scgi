package com.intrbiz.balsa.util;

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
}