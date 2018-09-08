package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;

public class Parser {
  public static final int EOF = 256;

  private StringReader pattern;
  private char lookahead = ' ';

  public Parser(String pattern) throws IOException {
    this.pattern = new StringReader(pattern);

    while (lookahead == ' ') {
      this.lookahead = (char) this.pattern.read();
    }
  }

  public char parse() throws IOException {
    return expr();
  }

  private char expr() throws IOException {
    return rest(term());
  }

  private char term() throws IOException {
    char result = EOF;

    if (Character.isLetterOrDigit(lookahead)) {
      result = lookahead;
      match(lookahead);
    } else if (lookahead == '(') {
      match('(');
      result = expr();
      match(')');
    } else {
      // throw new IOException()
    }
    return result;
  }

  private char rest(char left) throws IOException {
    if (lookahead == '|') {
      match('|');
      System.out.println("ALT");
      return expr();
    }
    return left;
  }

  private boolean match(char ch) throws IOException {
    if (ch == lookahead) {
      lookahead = (char) this.pattern.read();
      while (lookahead == ' ') {
        lookahead = (char) this.pattern.read();
      }
      return true;
    } else {
      return false;
    }
  }
}
