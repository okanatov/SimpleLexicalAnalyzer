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

  @Test
  public void parsesConcatination() throws IOException {
    Parser parser = new Parser("ab");
    assertEquals("ConcatinationNode(left: SingleNode(a), "
               + "right: SingleNode(b))",
               parser.parse().toString());
  }

  @Test
  public void parsesCombinationWithSpaces() throws IOException {
    Parser parser = new Parser("a bc d");
    assertEquals("ConcatinationNode(left: ConcatinationNode(left: "
               + "ConcatinationNode(left: ConcatinationNode(left: "
               + "ConcatinationNode(left: SingleNode(a), right: "
               + "SingleNode( )), right: SingleNode(b)), right: "
               + "SingleNode(c)), right: SingleNode( )), right: "
               + "SingleNode(d))",
               parser.parse().toString());
  }

  @Test
  public void parsesAlternation() throws IOException {
    Parser parser = new Parser("a|b");
    assertEquals("AlternationNode(left: SingleNode(a), "
               + "right: SingleNode(b))",
               parser.parse().toString());
  }

  @Test
  public void parsesSeveralAlternations() throws IOException {
    Parser parser = new Parser("a|b|c");
    assertEquals("AlternationNode(left: AlternationNode(left: SingleNode(a), "
               + "right: SingleNode(b)), right: SingleNode(c))",
               parser.parse().toString());
  }

  @Test
  public void parsesExpressionInBraces() throws IOException {
    Parser parser = new Parser("(a)");
    assertEquals("SingleNode(a)", parser.parse().toString());
  }
}
