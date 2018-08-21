package org.okanatov.lexer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;

public class ChainedLexerTest {
  private Token token;

  @Test
  public void testV() throws IOException {
    ChainedLexer chainedLexer = new ChainedLexer(
        new StringReader("IVVVX"),
        "V+");

    token = chainedLexer.readToken();
    assertEquals("I", token.toString());

    token = chainedLexer.readToken();
    assertEquals("VVV", token.toString());

    token = chainedLexer.readToken();
    assertEquals("X", token.toString());

    token = chainedLexer.readToken();
    assertEquals(null, token);
  }

  @Test
  public void testBraces() throws IOException, Exception {
    ChainedLexer chainedLexer = new ChainedLexer(
        new ChainedLexer(
          new StringReader("a<b>c"),
          "<"),
        ">");

    token = chainedLexer.readToken();
    assertEquals("a", token.toString());

    token = chainedLexer.readToken();
    assertEquals("<", token.toString());

    token = chainedLexer.readToken();
    assertEquals("b", token.toString());

    token = chainedLexer.readToken();
    assertEquals(">", token.toString());

    token = chainedLexer.readToken();
    assertEquals("c", token.toString());

    token = chainedLexer.readToken();
    assertEquals(null, token);

  }

  @Test
  public void testLookAheadAndBehind() throws IOException,Exception {
    ChainedLexer chainedLexer = new ChainedLexer(new StringReader("IIVVXX"), "(?<=I)V+(?=X)");

    token = chainedLexer.readToken();
    assertEquals("II", token.toString());

    token = chainedLexer.readToken();
    assertEquals("VV", token.toString());

    token = chainedLexer.readToken();
    assertEquals("XX", token.toString());

    token = chainedLexer.readToken();
    assertEquals(null, token);
  }

  @Test
  public void testIteration() throws IOException {
    ChainedLexer chainedLexer =
      new ChainedLexer(
          new ChainedLexer(
            new ChainedLexer(
              new StringReader("IIIVX"), "IV"),
            "II"),
          "X");

    for (Token aLexer : chainedLexer) {
      token = aLexer;
      System.out.println(token.toString());
    }
  }

  @Test
  public void test4() throws IOException {
    ChainedLexer chainedLexer = new ChainedLexer(new StringReader("IVX"), "IV");

    token = chainedLexer.readToken();
    assertEquals("IV", token.toString());

    token = chainedLexer.readToken();
    assertEquals("X", token.toString());

    token = chainedLexer.readToken();
    assertEquals(null, token);
  }

  @Test
  public void testComplex() throws IOException {
    ChainedLexer chainedLexer =
      new ChainedLexer(
          new ChainedLexer(
            new ChainedLexer(
              new StringReader("IIIVX"), "IV"),
            "I{2}"),
          "X");

    token = chainedLexer.readToken();
    assertEquals("II", token.toString());

    token = chainedLexer.readToken();
    assertEquals("IV", token.toString());

    token = chainedLexer.readToken();
    assertEquals("X", token.toString());

    token = chainedLexer.readToken();
    assertEquals(null, token);
  }
}
