package com.intrbiz.balsa.util;

import java.io.IOException;

public interface BalsaWriter
{   
    void write(int c) throws IOException;
    
    void write(char[] cbuf) throws IOException;

    void write(char[] cbuf, int off, int len) throws IOException;

    void write(String str) throws IOException;
    
    BalsaWriter append(CharSequence csq) throws IOException;
    
    BalsaWriter append(CharSequence csq, int start, int end) throws IOException;
    
    BalsaWriter append(char c) throws IOException;
    
    //

    BalsaWriter writeEncoded(String str) throws IOException;

    BalsaWriter indent();

    BalsaWriter unindent();

    int getIndentLevel();

    BalsaWriter padding() throws IOException;

    BalsaWriter put(String str) throws IOException;

    BalsaWriter putLn(String str) throws IOException;

    BalsaWriter putPad(String str) throws IOException;

    BalsaWriter putPadLn(String str) throws IOException;

    BalsaWriter putEnc(String str) throws IOException;

    BalsaWriter putEncLn(String str) throws IOException;

    BalsaWriter putEncPadLn(String str) throws IOException;

    BalsaWriter ln() throws IOException;

    BalsaWriter endTag(String name) throws IOException;

    BalsaWriter startTag(String name) throws IOException;

    BalsaWriter openStartTag(String name) throws IOException;

    BalsaWriter attribute(String name, String value) throws IOException;

    BalsaWriter closeStartTag() throws IOException;

    BalsaWriter endTagPad(String name) throws IOException;

    BalsaWriter startTagPad(String name) throws IOException;

    BalsaWriter openStartTagPad(String name) throws IOException;

    BalsaWriter endTagPadLn(String name) throws IOException;

    BalsaWriter startTagPadLn(String name) throws IOException;

    BalsaWriter endTagLn(String name) throws IOException;

    BalsaWriter startTagLn(String name) throws IOException;

    BalsaWriter closeStartTagLn() throws IOException;

    BalsaWriter comment(String comment) throws IOException;
    
    //
    
    void flush() throws IOException;

    void close() throws IOException;
}
