package com.intrbiz.balsa.scgi.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Matcher;
import org.junit.Test;

import com.intrbiz.balsa.parameter.ListParameter;
import com.intrbiz.balsa.parameter.Parameter;
import com.intrbiz.balsa.parameter.StringParameter;
import com.intrbiz.balsa.util.QueryStringParser;
import com.intrbiz.balsa.util.QueryStringParser.ParameterSet;

public class TestQueryStringParser
{
    public static final String SINGLE_PARAMETER = "name=test";
    
    public static final String LIST_NO_INDEX_PARAMETER = "list[]=test";
    
    public static final String LIST_INDEXED_PARAMETER = "list[1]=test";
    
    public static final String SIMPLE_QUERY_STRING = "name=test&summary=Test&number=12345";
    
    public static final String LIST_QUERY_STRING = "name=test&summary=Test&list=1&list=2&list=3";
    
    public static final String LIST_NO_INDEX_QUERY_STRING = "name=test&summary=Test&list[]=1&list[]=2&list[]=3";
    
    public static final String LIST_INDEXED_QUERY_STRING = "name=test&summary=Test&list[0]=1&list[1]=2&list[2]=3";

    @Test
    public void testSingleParameter()
    {
        DummyParameterSet params = new DummyParameterSet();
        QueryStringParser.parseParameter(SINGLE_PARAMETER, params);
        assertThat(params.getParameterNames(), hasItem("name"));
        assertThat(params.getParameterNames().size(), is(equalTo(1)));
        assertThat(params.getParameter("name"), is(notNullValue()));
        assertThat(params.getParameter("name"), is(StringParameter.class));
        assertThat(params.getParameter("name").getStringValue(), is(equalTo("test")));
        assertThat(params.getParameter("name").getStringListValue(), hasItem("test"));
        assertThat(params.getParameter("name").getIndex(), is(equalTo(-1)));
    }
    
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testListNoIndexParameter()
    {
        DummyParameterSet params = new DummyParameterSet();
        QueryStringParser.parseParameter(LIST_NO_INDEX_PARAMETER, params);
        assertThat(params.getParameterNames(), hasItem("list"));
        assertThat(params.getParameterNames().size(), is(equalTo(1)));
        assertThat(params.getParameter("list"), is(notNullValue()));
        assertThat(params.getParameter("list"), is(ListParameter.class));
        assertThat(params.getParameter("list").getStringValue(), is(nullValue()));
        assertThat(params.getParameter("list").getStringListValue(), hasItem("test"));
        assertThat(params.getParameter("list").getListValue(), is(notNullValue()));
        assertThat(params.getParameter("list").getListValue().size(), is(equalTo(1)));
        assertThat(params.getParameter("list").getListValue(), (Matcher) hasItem(new StringParameter("list", -1, "test")));
    }
    
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testListIndexedParameter()
    {
        DummyParameterSet params = new DummyParameterSet();
        QueryStringParser.parseParameter(LIST_INDEXED_PARAMETER, params);
        assertThat(params.getParameterNames(), hasItem("list"));
        assertThat(params.getParameterNames().size(), is(equalTo(1)));
        assertThat(params.getParameter("list"), is(notNullValue()));
        assertThat(params.getParameter("list"), is(ListParameter.class));
        assertThat(params.getParameter("list").getStringValue(), is(nullValue()));
        assertThat(params.getParameter("list").getStringListValue(), hasItem("test"));
        assertThat(params.getParameter("list").getListValue(),  is(notNullValue()));
        assertThat(params.getParameter("list").getListValue().size(),  is(equalTo(1)));
        assertThat(params.getParameter("list").getListValue(), (Matcher) hasItem(new StringParameter("list", 1, "test")));
    }
    
    @Test
    public void testSimpleQueryString()
    {
        DummyParameterSet params = new DummyParameterSet();
        QueryStringParser.parseQueryString(SIMPLE_QUERY_STRING, params);
        assertThat(params.getParameterNames(), hasItem("name"));
        assertThat(params.getParameterNames(), hasItem("summary"));
        assertThat(params.getParameterNames(), hasItem("number"));
        assertThat(params.getParameterNames().size(), is(equalTo(3)));
        assertThat(params.getParameter("name"), is(notNullValue()));
        assertThat(params.getParameter("name"), is(StringParameter.class));
        assertThat(params.getParameter("summary"), is(notNullValue()));
        assertThat(params.getParameter("summary"), is(StringParameter.class));
        assertThat(params.getParameter("number"), is(notNullValue()));
        assertThat(params.getParameter("number"), is(StringParameter.class));
        assertThat(params.getParameter("name").getStringValue(), is(equalTo("test")));
        assertThat(params.getParameter("name").getStringListValue(), hasItem("test"));
        assertThat(params.getParameter("summary").getStringValue(), is(equalTo("Test")));
        assertThat(params.getParameter("summary").getStringListValue(), hasItem("Test"));
        assertThat(params.getParameter("number").getStringValue(), is(equalTo("12345")));
        assertThat(params.getParameter("number").getStringListValue(), hasItem("12345"));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testListQueryString()
    {
        DummyParameterSet params = new DummyParameterSet();
        QueryStringParser.parseQueryString(LIST_QUERY_STRING, params);
        assertThat(params.getParameterNames(), hasItem("name"));
        assertThat(params.getParameterNames(), hasItem("summary"));
        assertThat(params.getParameterNames(), hasItem("list"));
        assertThat(params.getParameterNames().size(), is(equalTo(3)));
        assertThat(params.getParameter("name"), is(notNullValue()));
        assertThat(params.getParameter("name"), is(StringParameter.class));
        assertThat(params.getParameter("summary"), is(notNullValue()));
        assertThat(params.getParameter("summary"), is(StringParameter.class));
        assertThat(params.getParameter("list"), is(notNullValue()));
        assertThat(params.getParameter("list"), is(ListParameter.class));
        assertThat(params.getParameter("name").getStringValue(), is(equalTo("test")));
        assertThat(params.getParameter("name").getStringListValue(), hasItem("test"));
        assertThat(params.getParameter("summary").getStringValue(), is(equalTo("Test")));
        assertThat(params.getParameter("summary").getStringListValue(), hasItem("Test"));
        assertThat(params.getParameter("list").getStringValue(), is(nullValue()));
        assertThat(params.getParameter("list").getListValue().size(),  is(equalTo(3)));
        assertThat(params.getParameter("list").getStringListValue(), allOf(hasItem("1"), hasItem("2"), hasItem("3")));
        assertThat(params.getParameter("list").getStringListValue(), allOf(hasItem("1"), hasItem("2"), hasItem("3")));
        assertThat(params.getParameter("list").getListValue(), (Matcher) allOf(hasItem(new StringParameter("list", -1, "1")), hasItem(new StringParameter("list", -1, "2")), hasItem(new StringParameter("list", -1, "3"))));
    }
    
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testListNoIndexQueryString()
    {
        DummyParameterSet params = new DummyParameterSet();
        QueryStringParser.parseQueryString(LIST_NO_INDEX_QUERY_STRING, params);
        assertThat(params.getParameterNames(), hasItem("name"));
        assertThat(params.getParameterNames(), hasItem("summary"));
        assertThat(params.getParameterNames(), hasItem("list"));
        assertThat(params.getParameterNames().size(), is(equalTo(3)));
        assertThat(params.getParameter("name"), is(notNullValue()));
        assertThat(params.getParameter("name"), is(StringParameter.class));
        assertThat(params.getParameter("summary"), is(notNullValue()));
        assertThat(params.getParameter("summary"), is(StringParameter.class));
        assertThat(params.getParameter("list"), is(notNullValue()));
        assertThat(params.getParameter("list"), is(ListParameter.class));
        assertThat(params.getParameter("name").getStringValue(), is(equalTo("test")));
        assertThat(params.getParameter("name").getStringListValue(), hasItem("test"));
        assertThat(params.getParameter("summary").getStringValue(), is(equalTo("Test")));
        assertThat(params.getParameter("summary").getStringListValue(), hasItem("Test"));
        assertThat(params.getParameter("list").getStringValue(), is(nullValue()));
        assertThat(params.getParameter("list").getListValue().size(),  is(equalTo(3)));
        assertThat(params.getParameter("list").getStringListValue(), allOf(hasItem("1"), hasItem("2"), hasItem("3")));
        assertThat(params.getParameter("list").getStringListValue(), allOf(hasItem("1"), hasItem("2"), hasItem("3")));
        assertThat(params.getParameter("list").getListValue(), (Matcher) allOf(hasItem(new StringParameter("list", -1, "1")), hasItem(new StringParameter("list", -1, "2")), hasItem(new StringParameter("list", -1, "3"))));
    }
    
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testListIndexedQueryString()
    {
        DummyParameterSet params = new DummyParameterSet();
        QueryStringParser.parseQueryString(LIST_INDEXED_QUERY_STRING, params);
        assertThat(params.getParameterNames(), hasItem("name"));
        assertThat(params.getParameterNames(), hasItem("summary"));
        assertThat(params.getParameterNames(), hasItem("list"));
        assertThat(params.getParameterNames().size(), is(equalTo(3)));
        assertThat(params.getParameter("name"), is(notNullValue()));
        assertThat(params.getParameter("name"), is(StringParameter.class));
        assertThat(params.getParameter("summary"), is(notNullValue()));
        assertThat(params.getParameter("summary"), is(StringParameter.class));
        assertThat(params.getParameter("list"), is(notNullValue()));
        assertThat(params.getParameter("list"), is(ListParameter.class));
        assertThat(params.getParameter("name").getStringValue(), is(equalTo("test")));
        assertThat(params.getParameter("name").getStringListValue(), hasItem("test"));
        assertThat(params.getParameter("summary").getStringValue(), is(equalTo("Test")));
        assertThat(params.getParameter("summary").getStringListValue(), hasItem("Test"));
        assertThat(params.getParameter("list").getStringValue(), is(nullValue()));
        assertThat(params.getParameter("list").getListValue().size(),  is(equalTo(3)));
        assertThat(params.getParameter("list").getStringListValue(), allOf(hasItem("1"), hasItem("2"), hasItem("3")));
        assertThat(params.getParameter("list").getStringListValue(), allOf(hasItem("1"), hasItem("2"), hasItem("3")));
        assertThat(params.getParameter("list").getListValue(), (Matcher) allOf(hasItem(new StringParameter("list", 0, "1")), hasItem(new StringParameter("list", 1, "2")), hasItem(new StringParameter("list", 2, "3"))));
    }
    
    private static class DummyParameterSet implements ParameterSet
    {
        private Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        
        public Map<String, Parameter> getParameters()
        {
            return this.parameters;
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
    }
}
