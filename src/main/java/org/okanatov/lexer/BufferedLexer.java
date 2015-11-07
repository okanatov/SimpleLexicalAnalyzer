package org.okanatov.lexer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BufferedLexer {
    private final Buffer buffer;
    private final ArrayList<String> tokens = new ArrayList<>();
    private final Pattern pattern;
    private boolean finished = false;

    public BufferedLexer(StringReader reader, String pattern, int size) {
        this.pattern = Pattern.compile(pattern);
        this.buffer = new Buffer(size, reader);
    }

    public String readChar() {
        while (!finished && tokens.isEmpty()) {
            char temp = buffer.read();

            if (temp == '$') {
                String text = buffer.getString();
                Matcher matcher = pattern.matcher(text);

                if (matcher.find()) {
                    tokens.add(text.substring(0, matcher.start()));
                    tokens.add(text.substring(matcher.start(), matcher.end()));
                } else {
                    tokens.add(text);
                }

                finished = true;
                break;
            }

            String text = buffer.getString();
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                if (matcher.end() < text.length()) {
                    tokens.add(text.substring(0, matcher.start()));
                    tokens.add(text.substring(matcher.start(), matcher.end()));

                    int diff = text.length() - matcher.end();
                    int oldForward = buffer.getForward();
                    buffer.setForward(oldForward - diff);
                    buffer.setBegin(buffer.getForward());
                }
            }
        }

        removeEmptyTokens();
        if (!tokens.isEmpty())
            return tokens.remove(0);
        return null;
    }

    private void removeEmptyTokens() {
        Iterator<String> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            String text = iterator.next();
            if (text.equals(""))
                iterator.remove();
        }
    }
}
