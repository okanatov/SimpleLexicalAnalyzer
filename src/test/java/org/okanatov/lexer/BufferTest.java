package org.okanatov.lexer;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class BufferTest
{
    private Buffer buffer;

    @Before
    public void setUp() throws Exception {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));
    }

    @Test
    public void testApp()
    {
        char ch = buffer.read();
        while (ch  != '$') {
            buffer.setBegin(buffer.getForward() - 1);
            System.out.println(buffer.getString());
            ch = buffer.read();
        }
    }

    @Test
    public void testBeginForwardInFirst() {
        buffer.setForward(4);
        buffer.setBegin(1);
        assertEquals("123", buffer.getString());
    }

    @Test(expected = Error.class)
    public void testBufferOverflow1() {
        buffer.setBegin(4);

        for (int i = 0; i < 13; i++)
            buffer.read();
    }

    @Test
    public void testBeginForwardInDifferent() {
        for (int i = 0; i < 12; i++)
            buffer.read();

        buffer.setBegin(4);

        assertEquals("45678901", buffer.getString());
    }

    @Test
    public void testBeginForwardInSecond() {
        for (int i = 0; i < 12; i++)
            buffer.read();

        buffer.setBegin(8);

        assertEquals("78901", buffer.getString());
    }

    @Test(expected = Error.class)
    public void testBufferOverflow2() {
        buffer.setBegin(8);

        for (int i = 0; i < 13; i++)
            buffer.read();
    }
}

