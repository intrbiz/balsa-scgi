package com.intrbiz.balsa.util;

import java.io.IOException;
import java.io.OutputStream;

public class SocketOutputStream extends OutputStream
{
    private final OutputStream output;
    
    public SocketOutputStream(OutputStream output)
    {
        super();
        this.output = output;
    }

    @Override
    public void write(int b) throws IOException
    {
        this.output.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        this.output.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        this.output.write(b, off, len);
    }

    @Override
    public void flush() throws IOException
    {
        this.output.flush();
    }

    @Override
    public void close() throws IOException
    {
        // Do not close the stream, but call flush
        this.flush();
    }
    
}
