package org.okanatov.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer implements Iterable<Token> {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private StringBuilder buffer = new StringBuilder("");
    private final Pattern pattern;
    private InputStream source;
    private Lexer lexer;

    public Lexer(InputStream source, String matchingString) {
        this(matchingString);
        this.source = source;
    }

    public Lexer(Lexer lexer, String matchingString) {
        this(matchingString);
        this.lexer = lexer;
    }

    public Token readToken() {
        if (source != null)
            return readTokenFromStream();
        else {
            return readTokenFromLexer();
        }
    }

    private Token readTokenFromStream() {
        try {
            while (tokens.isEmpty()) {
                int ch;
                if ((ch = source.read()) != -1) {
                    buffer.append((char) ch);
                    Matcher matcher = pattern.matcher(buffer);
                    if (matcher.find()) {
                        tokens.add(new Token(buffer.substring(0, matcher.start()), 0));
                        tokens.add(new Token("[" + buffer.substring(matcher.start(), matcher.end()) + "]", 1));
                        buffer.delete(0, matcher.end());
                    }
                }
                if (ch == -1) {
                    tokens.add(new Token(buffer.toString(), 0));
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
        if (!tokens.isEmpty())
            return tokens.remove(0);

        Token token = lexer.readToken();
        if (token == null || token.getType() != 0) return token;

        String text = token.toString();
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            tokens.add(new Token(text.substring(0, matcher.start()), 0));
            tokens.add(new Token("[" + text.substring(matcher.start(), matcher.end()) + "]", 1));
            tokens.add(new Token(text.substring(matcher.end(), text.length()), 0));
        } else {
            tokens.add(new Token(text, 0));
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

    private Lexer(String matchingString) {
        pattern = Pattern.compile(matchingString);
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

        @Override
        public boolean hasNext() {
            return ((token = Lexer.this.readToken()) != null);
        }

        @Override
        public Token next() {
            return token;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove is not supported by Lexer");
        }
    }
}
