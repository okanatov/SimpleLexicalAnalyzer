package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;
import org.okanatov.lexer.AlternationNode;
import org.okanatov.lexer.ConcatinationNode;
import org.okanatov.lexer.Node;
import org.okanatov.lexer.SingleNode;
import org.okanatov.lexer.StarNode;

public class Parser {
  private StringReader pattern;
  private char lookahead;

  public Parser(String pattern) throws IOException {
    this.pattern = new StringReader(pattern);
    lookahead = (char) this.pattern.read();
  }

  public Node parse() throws IOException {
    return expr();
  }

  private Node expr() throws IOException {
    return rest(term());
  }

  private Node term() throws IOException {
    Node result;

    if (Character.isLetterOrDigit(lookahead) || Character.isWhitespace(lookahead)) {
      result = new SingleNode(lookahead);
      match(lookahead);
    } else if (lookahead == '(') {
      match('(');
      result = expr();
      match(')');
    } else {
      // can't happen
      throw new IOException();
    }
    return result;
  }

  private Node rest(Node node) throws IOException {
    Node left = node;

    while (true) {
      if (lookahead == '|') {
        match('|');
        Node right = expr();
        left = new AlternationNode(left, right);
      } else if (Character.isLetterOrDigit(lookahead) || Character.isWhitespace(lookahead)) {
        Node right = term();
        left = new ConcatinationNode(left, right);
      } else if (lookahead == '*') {
        match('*');
        left = new StarNode(left);
      } else if (lookahead == '(') {
        match('(');
        Node right = expr();
        match(')');
        left = new ConcatinationNode(left, right);
      } else {
        break;
      }
    }

    return left;
  }

  private boolean match(char ch) throws IOException {
    if (ch == lookahead) {
      lookahead = (char) this.pattern.read();
      return true;
    } else {
      return false;
    }
  }
}
