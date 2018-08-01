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
    public void testGetcReturnsEOFOnEmptyReader() {
        try {
            b = new DoubleBuffer(5, new StringReader(""));
            assertEquals(DoubleBuffer.eof, b.getc());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetcReturnsEOF() {
        try {
            b = new DoubleBuffer(5, new StringReader("0"));
            assertEquals('0', b.getc());
            assertEquals(DoubleBuffer.eof, b.getc());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetcReturnsChars() {
        String input = "0123456789abcdef";
        try {
            b = new DoubleBuffer(5, new StringReader(input));

            char arr[] = input.toCharArray();
            for (char c : arr) {
                assertEquals(c, b.getc());
            }

            assertEquals(DoubleBuffer.eof, b.getc());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
