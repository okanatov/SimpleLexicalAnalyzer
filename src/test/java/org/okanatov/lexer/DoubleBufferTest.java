package org.okanatov.lexer;

import java.io.StringReader;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;

public class DoubleBufferTest
{
    private DoubleBuffer b;

    @Test(expected = AssertionError.class)
    public void testCtorChecksNullReader() {
        try {
            b = new DoubleBuffer(5, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = AssertionError.class)
    public void testCtorChecksNullSize() {
        try {
            b = new DoubleBuffer(0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetcReturnsChars() {
        try {
            b = new DoubleBuffer(5, new StringReader("0123456789abcdef"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals('0', b.getc());
        assertEquals('1', b.getc());
    }

    @Test
    public void testGetcReturnsEOFOnEmptyReader() {
        try {
            b = new DoubleBuffer(5, new StringReader(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals('$', b.getc());
    }

    @Test
    public void testGetcReturnsEOF() {
        try {
            b = new DoubleBuffer(5, new StringReader("0"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals('0', b.getc());
        assertEquals('$', b.getc());
    }
}
