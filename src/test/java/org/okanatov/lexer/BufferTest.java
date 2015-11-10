package org.okanatov.lexer;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class BufferTest
{
    private Buffer buffer;

    @Test
    public void testBufferReturnsOneSymbolAtTimeIfBeginAndForwardAreTheSame()
    {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));

        int i = 0;
        String text;
        for (char ch = buffer.read(); ch != '$'; ch = buffer.read()) {
            text = buffer.getString();
            buffer.setBegin(buffer.getForward());

            assertEquals(Integer.valueOf(i++).toString(), text);
            i %= 10;
        }
    }

    @Test
    public void testBeginAndForwardInFirstBuffer() {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));

        buffer.setForward(4);
        buffer.setBegin(1);
        assertEquals("123", buffer.getString());
    }

    @Test
    public void testBeginAndForwardInDifferentBuffersAndBeginInFirst() {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));

        for (int i = 0; i < 12; i++) buffer.read();
        buffer.setBegin(4);

        assertEquals("45678901", buffer.getString());
    }

    @Test
    public void testBeginAndForwardInDifferentBuffersAndBeginInSecond() {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));

        for (int i = 0; i < 8; i++) buffer.read();
        buffer.setBegin(8);
        for (int i = 0; i < 8; i++) buffer.read();

        assertEquals("89012345", buffer.getString());
    }

    @Test
    public void testBeginForwardInSecond() {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));

        for (int i = 0; i < 12; i++) buffer.read();
        buffer.setBegin(8);

        assertEquals("8901", buffer.getString());
    }

    @Test
    public void testGetAndSetForward() {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));

        buffer.setForward(5);
        assertEquals(5, buffer.getForward());

        buffer.setForward(11);
        assertEquals(11, buffer.getForward());

        buffer.setForward(12);
        assertEquals(0, buffer.getForward());
    }

    @Test
    public void testGetAndSetBegin() {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));

        buffer.setBegin(5);
        assertEquals(5, buffer.getBegin());

        buffer.setBegin(11);
        assertEquals(11, buffer.getBegin());

        buffer.setBegin(12);
        assertEquals(0, buffer.getBegin());
    }

    @Ignore
    public void testBufferOverflow1() {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));
        buffer.setBegin(4);

        for (int i = 0; i < 13; i++)
            buffer.read();
    }

    @Ignore
    public void testBufferOverflow2() {
        buffer = new Buffer(6, new StringReader("01234567890123456789"));
        buffer.setBegin(8);

        for (int i = 0; i < 13; i++)
            buffer.read();
    }
}

