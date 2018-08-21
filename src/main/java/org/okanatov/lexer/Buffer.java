package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

class Buffer
{
    private final int bufferSize;
    private final StringReader reader;
    private final char[] current;
    private int pos;
    private int len;

    public Buffer(int bufferSize, StringReader reader) {
        this.bufferSize = bufferSize;
        this.reader = reader;
        this.current = new char[bufferSize];
        this.pos = 0;
        this.len = 0;
    }

    public char getc() throws IOException {
        if (pos == len) {
            int r = reader.read(current, 0, bufferSize);
            if (r == -1) {
                return '0';
            }
            pos = 0;
            len = r;
        }

        char ch = current[pos++];

        return ch;
    }

    public void ungetc() {
        assert pos > 0;
        pos--;
    }
}
