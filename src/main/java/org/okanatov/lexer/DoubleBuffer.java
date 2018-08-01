package org.okanatov.lexer;

import java.io.StringReader;
import java.io.IOException;

final public class DoubleBuffer {
    public static final int eof = 256;

    private int size;
    private StringReader source;
    private char[] buffer;
    private int forward = 0;
    private final int startOfFirst;
    private final int endOfFirst;
    private final int startOfSecond;
    private final int endOfSecond;

    public DoubleBuffer(int s, StringReader r) throws IOException {
        // Always check functions input
        assert(s != 0);
        assert r != null;

        size = s;
        source = r;

        startOfFirst = 0;
        endOfFirst = size;
        startOfSecond = size + 1;
        endOfSecond = size * 2 + 1;

        int totalSize = size * 2 + 2;

        buffer = new char[totalSize];

        load(startOfFirst);
    }

    public char getc() throws IOException {
        char ch = buffer[forward++];

        switch (ch) {
            case eof:
                if (forward == endOfFirst + 1) {
                    load(startOfSecond);
                    forward = startOfSecond;
                    return getc();
                } else if (forward == endOfSecond + 1) {
                    load(startOfFirst);
                    forward = startOfFirst;
                    return getc();
                } else {
                    return eof;
                }
            default:
                return ch;
        }
    }

    private void load(int pos) throws IOException {
        assert((pos == startOfFirst) || (pos == startOfSecond));

        int len = source.read(buffer, pos, size);

        if (len <= 0) {
            // Logging
            System.out.println("End of the stream has been reached");
            buffer[pos] = eof;
        } else {
            buffer[pos + len] = eof;
        }
    }
}
