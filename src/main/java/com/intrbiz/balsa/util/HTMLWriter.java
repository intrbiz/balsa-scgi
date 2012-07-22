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

package com.intrbiz.balsa.util;

import java.io.IOException;
import java.io.Writer;

/**
 * HTML util writer
 */
public class HTMLWriter extends Writer
{
    private Writer writer;
    
    private int indentLevel = 0;
    
    public HTMLWriter(Writer writer)
    {
        super();
        this.writer = writer;
    }

    public void write(int c) throws IOException
    {
        writer.write(c);
    }

    public void write(char[] cbuf) throws IOException
    {
        writer.write(cbuf);
    }

    public void write(char[] cbuf, int off, int len) throws IOException
    {
        writer.write(cbuf, off, len);
    }

    public void write(String str) throws IOException
    {
        writer.write(str);
    }
    
    public void writeEncoded(String str) throws IOException
    {
        htmlEncode(str, this);
    }

    public void write(String str, int off, int len) throws IOException
    {
        writer.write(str, off, len);
    }

    public Writer append(CharSequence csq) throws IOException
    {
        return writer.append(csq);
    }

    public Writer append(CharSequence csq, int start, int end) throws IOException
    {
        return writer.append(csq, start, end);
    }

    public Writer append(char c) throws IOException
    {
        return writer.append(c);
    }

    public void flush() throws IOException
    {
        writer.flush();
    }

    public void close() throws IOException
    {
        writer.close();
    }
    
    public HTMLWriter indent()
    {
        this.indentLevel++;
        return this;
    }
    
    public HTMLWriter unindent()
    {
        this.indentLevel--;
        if (this.indentLevel < 0) this.indentLevel = 0;
        return this;
    }
    
    public int getIndentLevel()
    {
        return this.indentLevel;
    }
    
    public HTMLWriter padding() throws IOException
    {
        for (int i = 0; i < this.getIndentLevel(); i++)
        {
            this.write("\t");
        }
        return this;
    }
    
    public HTMLWriter put(String str) throws IOException
    {
        this.write(str);
        return this;
    }
    
    public HTMLWriter putLn(String str) throws IOException
    {
        this.write(str);
        this.write("\r\n");
        return this;
    }
    
    public HTMLWriter putPad(String str) throws IOException
    {
        this.padding();
        this.write(str);
        return this;
    }
    
    public HTMLWriter putPadLn(String str) throws IOException
    {
        this.padding();
        this.write(str);
        this.write("\r\n");
        return this;
    }
    
    public HTMLWriter putEnc(String str) throws IOException
    {
        this.writeEncoded(str);
        return this;
    }
    
    public HTMLWriter putEncLn(String str) throws IOException
    {
        this.writeEncoded(str);
        this.write("\r\n");
        return this;
    }
    
    public HTMLWriter putEncPadLn(String str) throws IOException
    {
        this.padding();
        this.writeEncoded(str);
        this.write("\r\n");
        return this;
    }
    
    public HTMLWriter ln() throws IOException
    {
        this.write("\r\n");
        return this;
    }
    
    public HTMLWriter endTag(String name) throws IOException
    {
        this.write("</");
        this.write(name);
        this.write(">");
        return this;
    }
    
    public HTMLWriter startTag(String name) throws IOException
    {
        this.write("<");
        this.write(name);
        this.write(">");
        return this;
    }
    
    public HTMLWriter openStartTag(String name) throws IOException
    {
        this.write("<");
        this.write(name);
        return this;
    }
    
    public HTMLWriter attribute(String name, String value) throws IOException
    {
        this.write(" ");
        this.write(name);
        this.write("=\"");
        this.writeEncoded(value);
        this.write("\"");
        return this;
    }
    
    public HTMLWriter closeStartTag() throws IOException
    {
        this.write(">");
        return this;
    }
    
    public HTMLWriter endTagPad(String name) throws IOException
    {
        this.unindent();
        this.padding();
        this.write("</");
        this.write(name);
        this.write(">");
        return this;
    }
    
    public HTMLWriter startTagPad(String name) throws IOException
    {
        this.padding();
        this.indent();
        this.write("<");
        this.write(name);
        this.write(">");
        return this;
    }
    
    public HTMLWriter openStartTagPad(String name) throws IOException
    {
        this.padding();
        this.indent();
        this.write("<");
        this.write(name);
        return this;
    }
    
    public HTMLWriter endTagPadLn(String name) throws IOException
    {
        this.unindent();
        this.padding();
        this.write("</");
        this.write(name);
        this.write(">\r\n");
        return this;
    }
    
    public HTMLWriter startTagPadLn(String name) throws IOException
    {
        this.padding();
        this.indent();
        this.write("<");
        this.write(name);
        this.write(">\r\n");
        return this;
    }
    
    public HTMLWriter endTagLn(String name) throws IOException
    {
        this.write("</");
        this.write(name);
        this.write(">\r\n");
        return this;
    }
    
    public HTMLWriter startTagLn(String name) throws IOException
    {
        this.write("<");
        this.write(name);
        this.write(">\r\n");
        return this;
    }
    
    public HTMLWriter closeStartTagLn() throws IOException
    {
        this.write(">\r\n");
        return this;
    }
    
    public HTMLWriter comment(String comment) throws IOException
    {
        this.write("<!-- ");
        this.write(comment);
        this.write(" -->");
        return this;
    }
    
    public final static void htmlEncode(String in, Writer writer) throws IOException
    {
        if (in != null)
        {
            for (char c : in.toCharArray())
            {
                if (32 <= c && c <= 126 && c != '<' && c != '>' && c != '\'' && c != '&' && c != '"')
                {
                    writer.write(c);
                }
                else if (c == '\r' || c == '\n' || c == '\t')
                {
                    writer.write(c);
                }
                else
                {
                    switch (c)
                    {
                        case '&':
                            writer.write("&amp;");
                            break;
                        case '"':
                            writer.write("&quot;");
                            break;
                        case '<':
                            writer.write("&lt;");
                            break;
                        case '>':
                            writer.write("&gt;");
                            break;
                        default:
                            writer.write("&#x");
                            writer.write(Integer.toHexString(c));
                            writer.write(";");
                    }
                }
            }
        }
    }
}
