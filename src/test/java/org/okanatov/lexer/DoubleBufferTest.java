package org.okanatov.lexer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class "unit"tests the {@DoubleBuffer} class.
 */
public class DoubleBufferTest {
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
      assertEquals(DoubleBuffer.EOF, b.getc());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetcCanReturnEOFOnAllCharsRead() {
    try {
      b = new DoubleBuffer(5, new StringReader("0"));
      assertEquals('0', b.getc());
      assertEquals(DoubleBuffer.EOF, b.getc());
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
        System.out.println(Character.toString(c));
        assertEquals(Character.toString(c), b.getString());
      }

      assertEquals(DoubleBuffer.EOF, b.getc());
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
      assertEquals(DoubleBuffer.EOF, b.getc());
      b.ungetc();
      assertEquals("", b.getString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testNewCharactersAreNotLoadedIfBufferIsOverflown() {
    try {
      b = new DoubleBuffer(5, new StringReader("0123456789abcdef"));

      for (int i = 0; i < 10; i++) {
        assertEquals((char) (i + '0'), b.getc());
      }

      b.getc(); // causes Exception
    } catch (Error e) {
      System.out.println(e);
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    try {
      assertEquals("0123456789", b.getString());
      assertEquals('a', b.getc());
      assertEquals('b', b.getc());
      assertEquals('c', b.getc());
      assertEquals('d', b.getc());
      assertEquals('e', b.getc());
      assertEquals('f', b.getc());
      assertEquals(DoubleBuffer.EOF, b.getc());
      b.ungetc();
      assertEquals("abcdef", b.getString());
    } catch (IOException e2) {
      e2.printStackTrace();
    }
  }

  @Test
  public void testNewCharactersAreNotLoadedIfTheyWereBefore() {
    try {
      b = new DoubleBuffer(5, new StringReader("0123456"));

      assertEquals('0', b.getc());
      assertEquals('1', b.getc());
      assertEquals('2', b.getc());
      assertEquals('3', b.getc());
      assertEquals('4', b.getc());
      assertEquals('5', b.getc());
      assertEquals('6', b.getc());
      b.ungetc();
      assertEquals('6', b.getc());
      b.ungetc();
      b.ungetc();
      b.ungetc();
      assertEquals('4', b.getc());
      assertEquals('5', b.getc());
      assertEquals('6', b.getc());
      assertEquals(DoubleBuffer.EOF, b.getc());
      b.ungetc();
      b.ungetc();
      assertEquals('6', b.getc());
      assertEquals(DoubleBuffer.EOF, b.getc());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testUngetcCharacter() {
    try {
      b = new DoubleBuffer(3, new StringReader("0123456789abcdef"));

      assertEquals('0', b.getc());
      assertEquals('1', b.getc());
      assertEquals('2', b.getc());
      assertEquals('3', b.getc());
      b.ungetc();
      b.ungetc();
      assertEquals('2', b.getc());
      assertEquals('3', b.getc());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetSize() {
    try {
      b = new DoubleBuffer(5, new StringReader("0123456789abcdef"));

      b.getc();
      b.getc();
      b.getc();
      assertEquals(3, b.getSize());
      b.getc();
      b.getc();
      b.getc();
      assertEquals(6, b.getSize());
      b.getString();
      b.getc();
      b.getc();
      assertEquals(2, b.getSize());
      b.getc();
      b.getc();
      b.getc();
      b.getc();
      b.getc();
      assertEquals(7, b.getSize());
      b.getc();
      b.getc();
      b.getc();
    } catch (Error e) {
      System.out.println(e);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals(9, b.getSize());
  }
}
