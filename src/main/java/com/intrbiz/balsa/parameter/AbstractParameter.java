/*
 * Balsa SCGI Copyright (c) 2012, Chris Ellis All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intrbiz.balsa.parameter;

import java.io.InputStream;
import java.util.List;

public abstract class AbstractParameter implements Parameter
{
    private final String name;

    private final int index;

    public AbstractParameter(String name, int index)
    {
        this.name = name;
        this.index = index;
    }

    public AbstractParameter(String name)
    {
        this(name, -1);
    }

    public String getName()
    {
        return name;
    }

    public int getIndex()
    {
        return this.index;
    }

    public Object getValue()
    {
        return null;
    }

    public String getStringValue()
    {
        return null;
    }

    public List<Parameter> getListValue()
    {
        return null;
    }

    public Parameter getListValue(int index)
    {
        return null;
    }

    public List<String> getStringListValue()
    {
        return null;
    }

    public String getStringListValue(int index)
    {
        return null;
    }

    public int getLength()
    {
        return 0;
    }

    public byte[] getBytesValue()
    {
        return null;
    }

    public InputStream getInputStream()
    {
        return null;
    }

    public void close()
    {
    }

    public String toString()
    {
        return this.getName() + "[" + this.getIndex() + "]" + " => " + this.getValue();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AbstractParameter other = (AbstractParameter) obj;
        if (index != other.index) return false;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }

    @Override
    public int compareTo(Parameter o)
    {
        if (this.name.equals(o.getName())) { return Integer.compare(this.index, o.getIndex()); }
        return this.name.compareTo(o.getName());
    }
}
