package org.okanatov.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.okanatov.lexer.Utils;
import java.util.List;

public class ChainedLexer implements Iterable<Token> {
  private final List<Token> tokens = new ArrayList<>();
  private StringBuilder buffer = new StringBuilder("");
  private final String matching_text;
  private Object source;

  public ChainedLexer(Object source, String matchingString) {
    this(matchingString);

    if (source instanceof InputStream) {
      this.source = (InputStream) source;
    } else {
      this.source = (ChainedLexer) source;
    }
  }

  public Token readToken() {
    if (source instanceof InputStream) return readTokenFromStream();
    else                               return readTokenFromLexer();
  }

  private Token readTokenFromStream() {
    try {
      while (tokens.isEmpty()) {
        int ch;
        if ((ch = ((InputStream) source).read()) != -1) {
          buffer.append((char) ch);

          List<String> results = Utils.split(matching_text, buffer.toString());

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
        if (ch == -1) {
          tokens.add(new Token(buffer.toString(), Token.Type.UNKNOWN));
          buffer = null;
          buffer = new StringBuilder("");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    removeEmptyTokens();
    if (!tokens.isEmpty())
      return tokens.remove(0);
    return null;
  }

  private Token readTokenFromLexer() {
    Token token;
    Iterator<Token> iterator = ((ChainedLexer) source).iterator();

    while (tokens.isEmpty()) {
      if (iterator.hasNext())
        token = iterator.next();
      else
        return null;

      if (token.getType() == Token.Type.KNOWN) return token;

      List<String> results = Utils.split(matching_text, token.toString());

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
    if (!tokens.isEmpty())
      return tokens.remove(0);
    return null;
  }

  @Override
  public Iterator<Token> iterator() {
    return new LexerIterator();
  }

  private ChainedLexer(String matchingString) {
    this.matching_text = matchingString;
  }

  private void removeEmptyTokens() {
    tokens.removeIf(x -> x.toString().equals("") || x.toString().equals(" "));
  }

  private class LexerIterator implements Iterator<Token> {

    private Token token = null;
    private boolean read = false;

    @Override
    public boolean hasNext() {
      token = ChainedLexer.this.readToken();
      read = true;
      return token != null;
    }

    @Override
    public Token next() {
      if (read) {
        read = false;
        return checkTokenValueAndReturn();
      } else {
        token = ChainedLexer.this.readToken();
        return checkTokenValueAndReturn();
      }
    }

    private Token checkTokenValueAndReturn() {
      if (token != null)
        return token;
      else
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
  }
}
