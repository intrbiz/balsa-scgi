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
import java.util.List;



/**
 * A simple parameter
 */
public final class StringParameter extends AbstractParameter
{
    private final String value;

    public StringParameter(String name, String value)
    {
        super(name);
        this.value = value;
    }
    
    public StringParameter(String name, int index, String value)
    {
        super(name, index);
        this.value = value;
    }

    public String getStringValue()
    {
        return this.value;
    }

    public Object getValue()
    {
        return this.value;
    }
    
    public List<Parameter> getListValue()
    {
        List<Parameter> ret = new ArrayList<Parameter>();
        ret.add(this);
        return ret;
    }
    
    public List<String> getStringListValue()
    {
        List<String> ret = new ArrayList<String>();
        ret.add(this.value);
        return ret;
    }
}
