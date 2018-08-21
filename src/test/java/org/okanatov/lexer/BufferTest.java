package org.okanatov.lexer;

import org.junit.Test;
import java.io.StringReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BufferTest
{
    private Buffer buffer;

    @Test
    public void returnsOneCharacterAtTimeTillEof() throws IOException
    {
        Buffer b = new Buffer(5, new StringReader("0123456789abcdef"));
        assertEquals('0', b.getc());
        assertEquals('1', b.getc());
        assertEquals('2', b.getc());
        assertEquals('3', b.getc());
        assertEquals('4', b.getc());
        assertEquals('5', b.getc());
        assertEquals('6', b.getc());
        assertEquals('7', b.getc());
        assertEquals('8', b.getc());
        assertEquals('9', b.getc());
        assertEquals('a', b.getc());
        assertEquals('b', b.getc());
        assertEquals('c', b.getc());
        assertEquals('d', b.getc());
        assertEquals('e', b.getc());
        assertEquals('f', b.getc());
        assertEquals('0', b.getc());
        assertEquals('0', b.getc());
    }

    @Test(expected = AssertionError.class)
    public void firstUngetcFails() throws IOException
    {
        Buffer b = new Buffer(5, new StringReader("0123"));
        b.ungetc();
    }

    @Test
    public void ungetcCharactersAndReturnThemAgain() throws IOException
    {
        Buffer b = new Buffer(2, new StringReader("0123"));
        assertEquals('0', b.getc());
        assertEquals('1', b.getc());
        b.ungetc();
        assertEquals('1', b.getc());
        b.ungetc();
        assertEquals('1', b.getc());
        assertEquals('2', b.getc());
        b.ungetc();
        assertEquals('2', b.getc());
        assertEquals('3', b.getc());
        assertEquals('0', b.getc());
        b.ungetc();
        assertEquals('3', b.getc());
        assertEquals('0', b.getc());
    }
}
