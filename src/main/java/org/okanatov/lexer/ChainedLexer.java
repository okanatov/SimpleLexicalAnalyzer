package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.okanatov.lexer.Buffer;
import org.okanatov.lexer.Utils;

public class ChainedLexer implements Iterable<Token> {
  private final List<Token> tokens = new ArrayList<>();
  private StringBuilder buffer = new StringBuilder("");
  private final String matchingText;
  private Object source;

  public ChainedLexer(Object source, String matchingString) {
    if (source instanceof StringReader) {
      this.source = new Buffer(4, (StringReader) source);
    } else {
      this.source = (ChainedLexer) source;
    }

    this.matchingText = matchingString;
  }

  public Token readToken() throws IOException {
    return source instanceof Buffer ? readTokenFromStream() : readTokenFromLexer();
  }

  private Token readTokenFromStream() throws IOException {
    while (tokens.isEmpty()) {
      int ch;
      if ((ch = ((Buffer) source).getc()) != Buffer.EOF) {
        buffer.append((char) ch);

        List<String> results = Utils.split(matchingText, buffer.toString());

        if (results.size() > 2) {

          tokens.add(new Token(results.get(0), Token.Type.UNKNOWN));
          tokens.add(new Token(results.get(1), Token.Type.KNOWN));

          buffer = null;
          buffer = new StringBuilder("");

          for (int i = 2; i < results.size(); i++) {
            buffer.append(results.get(i));
          }
        }
      }

      if (ch == Buffer.EOF) {
        tokens.add(new Token(buffer.toString(), Token.Type.UNKNOWN));
        buffer = null;
        buffer = new StringBuilder("");
      }
    }

    removeEmptyTokens();
    if (!tokens.isEmpty()) {
      return tokens.remove(0);
    }

    return null;
  }

  private Token readTokenFromLexer() {
    Token token;
    Iterator<Token> iterator = ((ChainedLexer) source).iterator();

    while (tokens.isEmpty()) {
      if (iterator.hasNext()) {
        token = iterator.next();
      } else {
        return null;
      }

      if (token.getType() == Token.Type.KNOWN) {
        return token;
      }

      List<String> results = Utils.split(matchingText, token.toString());

      if (results.size() > 2) {
        tokens.add(new Token(results.get(0), Token.Type.UNKNOWN));
        tokens.add(new Token(results.get(1), Token.Type.KNOWN));

        for (int i = 2; i < results.size(); i++) {
          tokens.add(new Token(results.get(i), Token.Type.UNKNOWN));
        }
      } else {
        tokens.add(new Token(token.toString(), Token.Type.UNKNOWN));
      }
    }

    removeEmptyTokens();
    if (!tokens.isEmpty()) {
      return tokens.remove(0);
    }

    return null;
  }

  @Override
  public Iterator<Token> iterator() {
    return new LexerIterator();
  }

  private void removeEmptyTokens() {
    tokens.removeIf(x -> x.toString().equals("") || x.toString().equals(" "));
  }

  private class LexerIterator implements Iterator<Token> {

    private Token token = null;
    private boolean read = false;

    @Override
    public boolean hasNext() {
      try {
        token = ChainedLexer.this.readToken();
      } catch (IOException e) {
      }

      read = true;
      return token != null;
    }

    @Override
    public Token next() {
      if (read) {
        read = false;
        return checkTokenValueAndReturn();
      } else {
        try {
          token = ChainedLexer.this.readToken();
        } catch (IOException e) {
        }
        return checkTokenValueAndReturn();
      }
    }

    private Token checkTokenValueAndReturn() {
      if (token != null) {
        return token;
      }

      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
  }
}
