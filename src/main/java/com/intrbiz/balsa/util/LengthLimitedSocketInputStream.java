package com.intrbiz.balsa.util;

import java.io.IOException;
import java.io.InputStream;

public final class LengthLimitedSocketInputStream extends InputStream
{
    private final int length;
    
    private final InputStream input;
    
    private int read = 0;
    
    public LengthLimitedSocketInputStream(int length, InputStream input)
    {
        super();
        this.input = input;
        this.length = length;
    }

    @Override
    public int read() throws IOException
    {
        if (this.read >= this.length) return -1;
        int b = this.input.read();
        this.read++;
        return b;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        if (this.read >= this.length) return -1;
        int read = this.input.read(b, off, len);
        this.read += read;
        return read;
    }

    @Override
    public int available() throws IOException
    {
        return this.length - this.read;
    }

    @Override
    public void close() throws IOException
    {
        // Do not close the underlying input stream
    }
}
