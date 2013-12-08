package com.intrbiz.balsa.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.intrbiz.balsa.parameter.ListParameter;
import com.intrbiz.balsa.parameter.Parameter;
import com.intrbiz.balsa.parameter.StringParameter;

public class QueryStringParser
{
    /**
     * Parse the given query string
     * @param query
     * @param request
     * returns void
     */
    public final static void parseQueryString(String query, ParameterSet request)
    {
        if (!Util.isEmpty(query))
        {
            int spos = 0, pos = 0;
            while ((pos = query.indexOf("&", spos)) != -1)
            {
                parseParameter(query.substring(spos, pos), request);
                spos = pos + 1;
            }
            parseParameter(query.substring(spos), request);
        }
    }

    /**
     * Parse the given parameter
     * @param parameter
     * @param request
     * returns void
     */
    public final static void parseParameter(String parameter, ParameterSet request)
    {
        int pos = parameter.indexOf("=");
        if (pos != -1)
        {
            try
            {
                // TODO: Charset
                String name = URLDecoder.decode(parameter.substring(0, pos), "UTF-8");
                String value = URLDecoder.decode(parameter.substring(pos + 1), "UTF-8");
                Parameter jparam = new StringParameter(name, value);
                // add
                if (request.containsParameter(name))
                {
                    Parameter fparam = request.getParameter(name);
                    if (fparam instanceof ListParameter)
                    {
                        ((ListParameter) fparam).addValue(jparam);
                    }
                    else
                    {
                        request.addParameter(new ListParameter(name, fparam, jparam));
                    }
                }
                else
                {
                    request.addParameter(jparam);
                }
            }
            catch (UnsupportedEncodingException e)
            {
            }
        }
    }
    
    public static interface ParameterSet
    {
        Map<String, Parameter> getParameters();

        Parameter getParameter(String name);

        void addParameter(Parameter parameter);

        Set<String> getParameterNames();

        Collection<Parameter> getParameterValues();

        boolean containsParameter(String name);
    }
}
