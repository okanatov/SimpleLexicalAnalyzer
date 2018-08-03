package org.okanatov.lexer;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

/**
 * This class "unit"tests the {@DoubleBuffer} class.
 */
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
    public void testCtorChecksZeroSize() {
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
    public void testGetcCanReturnEOFOnAllCharsRead() {
        try {
            b = new DoubleBuffer(5, new StringReader("0"));
            assertEquals('0', b.getc());
            assertEquals(DoubleBuffer.eof, b.getc());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetcCanLoadBuffersAndReturnEOFOnAllCharsRead() {
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

    @Test
    public void testGetStringCanReturnDiffBetweenPointers() {
        try {
            b = new DoubleBuffer(5, new StringReader("012"));
            assertEquals('0', b.getc());
            assertEquals('1', b.getc());
            assertEquals('2', b.getc());
            assertEquals("012", b.getString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStringReturnsEmptyStringIfPointersAreSame() {
        try {
            b = new DoubleBuffer(5, new StringReader("012"));
            assertEquals("", b.getString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStringReturnsEmptyStringIfPointersAreSameAfterSomeRead() {
        try {
            b = new DoubleBuffer(5, new StringReader("0123456"));
            assertEquals('0', b.getc());
            assertEquals('1', b.getc());
            assertEquals('2', b.getc());
            assertEquals("012", b.getString());
            assertEquals("", b.getString());
            assertEquals('3', b.getc());
            assertEquals("3", b.getString());
            assertEquals('4', b.getc());
            assertEquals('5', b.getc());
            assertEquals('6', b.getc());
            assertEquals("456", b.getString());
            assertEquals(DoubleBuffer.eof, b.getc());
            // assertEquals("", b.getString()); in order for this to work it needs to make forward--
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
