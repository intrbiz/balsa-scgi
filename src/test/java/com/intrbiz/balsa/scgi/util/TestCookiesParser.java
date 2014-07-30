package com.intrbiz.balsa.scgi.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.intrbiz.balsa.util.CookiesParser;
import com.intrbiz.balsa.util.CookiesParser.CookieSet;

public class TestCookiesParser
{
    public static final String SINGLE_COOKIE = "BalsaSession=ba15a97d805c51d434abc8e0755786164b51f4256d74e62c428bc";
    
    public static final String MANY_COOKIES = "BalsaSession=ba15a97d805c51d434abc8e0755786164b51f4256d74e62c428bc; bergamot.username=chris.ellis";

    @Test
    public void testSingleCookie()
    {
        DummyCookieSet jar = new DummyCookieSet();
        CookiesParser.parseCookies(SINGLE_COOKIE, jar);
        assertThat(jar.cookie("BalsaSession"), is(notNullValue()));
        assertThat(jar.cookie("BalsaSession"), is(equalTo("ba15a97d805c51d434abc8e0755786164b51f4256d74e62c428bc")));
    }
    
    @Test
    public void testManyCookies()
    {
        DummyCookieSet jar = new DummyCookieSet();
        CookiesParser.parseCookies(MANY_COOKIES, jar);
        assertThat(jar.cookie("BalsaSession"), is(notNullValue()));
        assertThat(jar.cookie("BalsaSession"), is(equalTo("ba15a97d805c51d434abc8e0755786164b51f4256d74e62c428bc")));
        assertThat(jar.cookie("bergamot.username"), is(notNullValue()));
        assertThat(jar.cookie("bergamot.username"), is(equalTo("chris.ellis")));
    }

    public static class DummyCookieSet implements CookieSet
    {
        private Map<String, String> cookies = new HashMap<String, String>();
        
        @Override
        public String cookie(String name)
        {
            return this.cookies.get(name);
        }

        @Override
        public void cookie(String name, String value)
        {
            this.cookies.put(name, value);
        }
    }
}
