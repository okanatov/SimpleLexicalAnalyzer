package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;

public class Parser {
  private StringReader pattern;
  private char lookahead;

  public Parser(String pattern) throws IOException {
    this.pattern = new StringReader(pattern);
    this.lookahead = read();
  }

  public char parse() throws IOException {
    return expr();
  }

  private char expr() throws IOException {
    return term();
  }

  private char term() throws IOException {
    char result = '0';

    if (Character.isLetterOrDigit(lookahead)) {
      result = lookahead;
    } else if (lookahead == '(') {
      lookahead = read();
      result = expr();
      lookahead = read();
    }
    return result;
  }

  private char read() throws IOException {
    return (char) this.pattern.read();
  }
}
