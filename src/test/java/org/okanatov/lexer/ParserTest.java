package org.okanatov.lexer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

public class ParserTest {

  @Test
  public void parsesTerm() throws IOException {
    Parser parser = new Parser("a");
    assertEquals('a', parser.parse());
  }

  @Test
  public void parsesExpressionInBraces() throws IOException {
    Parser parser = new Parser("(a)");
    assertEquals('a', parser.parse());
  }
}
