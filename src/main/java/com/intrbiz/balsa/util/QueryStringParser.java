package com.intrbiz.balsa.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import com.intrbiz.balsa.parameter.ListParameter;
import com.intrbiz.balsa.parameter.Parameter;
import com.intrbiz.balsa.parameter.StringParameter;

/**
 * Parse HTTP query strings
 */
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
                int index = -1;
                boolean forceList = false;
                boolean indexedList = false;
                // handle array parameters in the form of name[index]
                // parameters ending in []
                if (name.endsWith("]"))
                {
                    int sepIdx = name.lastIndexOf('[');
                    if (sepIdx > 0)
                    {
                        try
                        {
                            String strIndex = name.substring(sepIdx + 1, name.length() - 1);
                            if (! Util.isEmpty(strIndex))
                            {
                                index = Integer.parseInt(strIndex);
                                indexedList = true;
                            }
                            name = name.substring(0, sepIdx);
                            forceList = true;
                        }
                        catch (NumberFormatException e)
                        {
                            Logger.getLogger(QueryStringParser.class).debug("Failed to decode array parameter index, of '" + name + "'");
                        }
                    }
                }
                // create the parameter
                Parameter jparam = new StringParameter(name, index, value);
                // add
                if (request.containsParameter(name))
                {
                    Parameter fparam = request.getParameter(name);
                    if (fparam instanceof ListParameter)
                    {
                        ((ListParameter) fparam).addValue(jparam);
                        if (indexedList) ((ListParameter) fparam).sort();
                    }
                    else
                    {
                        ListParameter lparam = new ListParameter(name, fparam, jparam);
                        if (indexedList) lparam.sort();
                        request.addParameter(lparam);
                    }
                }
                else
                {
                    if (forceList)
                    {
                        // force a list
                        request.addParameter(new ListParameter(name, jparam));
                    }
                    else
                    {
                        request.addParameter(jparam);
                    }
                }
            }
            catch (UnsupportedEncodingException e)
            {
            }
        }
    }
}
