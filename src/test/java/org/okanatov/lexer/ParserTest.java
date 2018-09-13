package org.okanatov.lexer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

public class ParserTest {

  @Test
  public void parsesTerm() throws IOException {
    Parser parser = new Parser("a");
    assertEquals("SingleNode(a)", parser.parse().toString());
  }

  @Ignore
  @Test
  public void parsesCombination() throws IOException {
    Parser parser = new Parser("ab");
    assertEquals('a', parser.parse());
    assertEquals('b', parser.parse());
  }

  @Ignore
  @Test
  public void parsesCombinationWithSpaces() throws IOException {
    Parser parser = new Parser("a bc d");
    assertEquals('a', parser.parse());
    assertEquals('b', parser.parse());
    assertEquals('c', parser.parse());
    assertEquals('d', parser.parse());
  }

  @Test
  public void parsesAlternation() throws IOException {
    Parser parser = new Parser("a|b");
    assertEquals("AlternationNode(left: SingleNode(a), right: SingleNode(b))", parser.parse().toString());
  }

  @Ignore
  @Test
  public void parsesExpressionInBraces() throws IOException {
    Parser parser = new Parser("(a)");
    assertEquals('a', parser.parse());
  }
}
