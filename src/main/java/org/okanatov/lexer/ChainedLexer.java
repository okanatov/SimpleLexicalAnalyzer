package org.okanatov.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.okanatov.lexer.Utils;
import java.util.List;

public class ChainedLexer implements Iterable<Token> {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private StringBuilder buffer = new StringBuilder("");
    private final Pattern pattern;
    private final String matching_text;
    private InputStream source;
    private ChainedLexer chainedLexer;

    public ChainedLexer(InputStream source, String matchingString) {
        this(matchingString);
        this.source = source;
    }

    public ChainedLexer(ChainedLexer chainedLexer, String matchingString) {
        this(matchingString);
        this.chainedLexer = chainedLexer;
    }

    public Token readToken() {
        if (source != null) return readTokenFromStream();
        else return readTokenFromLexer();
    }

    private Token readTokenFromStream() {
        try {
            while (tokens.isEmpty()) {
                int ch;
                if ((ch = source.read()) != -1) {
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
                    } else if (results.size() == 2) {
                      Matcher matcher = pattern.matcher(results.get(0));
                      if (matcher.matches()) {
                        tokens.add(new Token(results.get(0), Token.Type.KNOWN));

                        buffer = null;
                        buffer = new StringBuilder("");

                        for (int i = 1; i < results.size(); i++) {
                          buffer.append(results.get(i));
                        }
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
        Iterator<Token> iterator = chainedLexer.iterator();

        while (tokens.isEmpty()) {
            if (iterator.hasNext())
                token = iterator.next();
            else
                return null;

            if (token.getType() == Token.Type.KNOWN) return token;

            String text = token.toString();
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                tokens.add(new Token(text.substring(0, matcher.start()), Token.Type.UNKNOWN));
                tokens.add(new Token(text.substring(matcher.start(), matcher.end()), Token.Type.KNOWN));
                tokens.add(new Token(text.substring(matcher.end(), text.length()), Token.Type.UNKNOWN));
            } else {
                tokens.add(new Token(text, Token.Type.UNKNOWN));
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
        pattern = Pattern.compile(matchingString);
        this.matching_text = matchingString;
    }

    private void removeEmptyTokens() {
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            Token object = iterator.next();
            String text = object.toString();
            if (text.equals("") || text.equals(" "))
                iterator.remove();
        }
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
