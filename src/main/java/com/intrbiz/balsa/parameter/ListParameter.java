/*
 * Balsa SCGI
 * Copyright (c) 2012, Chris Ellis
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */

package com.intrbiz.balsa.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list of parameters
 */
public final class ListParameter extends AbstractParameter
{
    private final ArrayList<Parameter> value = new ArrayList<Parameter>();

    public ListParameter(String name)
    {
        super(name);
    }
    
    public ListParameter(String name, Parameter... values)
    {
        super(name);
        if (values != null)
        {
            for (Parameter val : values)
            {
                this.value.add(val);
            }
        }
    }
    
    public String getStringValue()
    {
        return null;
    }

    public List<Parameter> getListValue()
    {
        return this.value;
    }
    
    public Parameter getListValue(int index)
    {
        for (Parameter param : this.value)
        {
            if (param.getIndex() == index)
                return param;
        }
        return null;
    }
    
    public List<String> getStringListValue()
    {
        ArrayList<String> ret = new ArrayList<String>();
        for (Parameter param : this.value)
        {
            if (param instanceof StringParameter)
            {
                ret.add(param.getStringValue());
            }
        }
        return ret;
    }
    
    public String getStringListValue(int index)
    {
        Parameter param = this.getListValue(index);
        if (param instanceof StringParameter)
        {
            return param.getStringValue();
        }
        return null;
    }
    
    public int getLength()
    {
        return this.value.size();
    }

    public Object getValue()
    {
        return this.value;
    }
    
    public void addValue(Parameter parameter)
    {
        this.value.add(parameter);
    }
    
    public void sort()
    {
        Collections.sort(this.value);
    }
}
