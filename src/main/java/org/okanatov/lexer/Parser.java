package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;
import org.okanatov.lexer.Node;
import org.okanatov.lexer.SingleNode;
import org.okanatov.lexer.AlternationNode;

public class Parser {
  public static final int EOF = 256;

  private StringReader pattern;
  private char lookahead = ' ';

  public Parser(String pattern) throws IOException {
    this.pattern = new StringReader(pattern);
    passSpaces();
  }

  public Node parse() throws IOException {
    return expr();
  }

  private Node expr() throws IOException {
    return rest(term());
  }

  private Node term() throws IOException {
    Node result;

    if (Character.isLetterOrDigit(lookahead)) {
      result = new SingleNode(lookahead);
      match(lookahead);
    } else if (lookahead == '(') {
      match('(');
      result = expr();
      match(')');
    } else {
      result = new SingleNode(' ');
      // can't happen
      // throw new IOException()
    }
    return result;
  }

  private Node rest(Node left) throws IOException {
    if (lookahead == '|') {
      match('|');
      Node right = expr();
      left = new AlternationNode(left, right);
      left = rest(left);
    }
    return left;
  }

  private boolean match(char ch) throws IOException {
    if (ch == lookahead) {
      lookahead = (char) this.pattern.read();
      passSpaces();

      return true;
    } else {
      return false;
    }
  }

  private void passSpaces() throws IOException {
    while (lookahead == ' ') {
    lookahead = (char) this.pattern.read();
    }
  }
}
