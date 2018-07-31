package org.okanatov.lexer;

import java.io.StringReader;
import java.io.IOException;

final public class DoubleBuffer {
    private int size;
    private StringReader source;
    private char[] buffer;
    private int forward = 0;

    public DoubleBuffer(int s, StringReader r) throws IOException {
        // Always check functions input
        assert(s != 0);
        assert r != null;

        size = s;
        source = r;

        buffer = new char[size + 1];

        load();
    }

    public char getc() {
        char ch = buffer[forward++];

        switch (ch) {
            case '$':
                return '$'; // EOF can be replaced with some other symbol
            default:
                return ch;
        }
    }

    private void load() throws IOException {
        int len = source.read(buffer, 0, size);

        if (len <= 0) {
            // Logging
            System.out.println("End of the stream has been reached");
            buffer[0] = '$';
        } else {
            buffer[len] = '$';
        }
    }
}