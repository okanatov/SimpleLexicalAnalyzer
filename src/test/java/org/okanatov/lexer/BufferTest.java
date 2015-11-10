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
    public void testBufferReturnsOneSymbolAtTimeIfBeginAndForwardAreTheSame()
    {
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
        buffer.setForward(4);
        buffer.setBegin(1);
        assertEquals("123", buffer.getString());
    }

    @Test
    public void testBeginAndForwardInDifferentBuffersAndBeginInFirst() {
        for (int i = 0; i < 12; i++) buffer.read();
        buffer.setBegin(4);

        assertEquals("45678901", buffer.getString());
    }

    @Test
    public void testBeginAndForwardInDifferentBuffersAndBeginInSecond() {
        for (int i = 0; i < 8; i++) buffer.read();
        buffer.setBegin(8);
        for (int i = 0; i < 8; i++) buffer.read();

        assertEquals("89012345", buffer.getString());
    }

    @Test
    public void testBeginForwardInSecond() {
        for (int i = 0; i < 12; i++) buffer.read();
        buffer.setBegin(8);

        assertEquals("8901", buffer.getString());
    }

    @Test
    public void testGetAndSetForward() {
        buffer.setForward(5);
        assertEquals(5, buffer.getForward());

        buffer.setForward(11);
        assertEquals(11, buffer.getForward());

        buffer.setForward(12);
        assertEquals(0, buffer.getForward());
    }

    @Test
    public void testGetAndSetBegin() {
        buffer.setBegin(5);
        assertEquals(5, buffer.getBegin());

        buffer.setBegin(11);
        assertEquals(11, buffer.getBegin());

        buffer.setBegin(12);
        assertEquals(0, buffer.getBegin());
    }

    @Test(expected = Error.class)
    public void testBufferOverflow() {
        buffer.setBegin(4);
        buffer.read();
    }
}
