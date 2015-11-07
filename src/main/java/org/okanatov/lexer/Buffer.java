package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

class Buffer
{
    private final int bufferSize;
    private final StringReader reader;
    private int begin = 0;
    private int forward = 0;
    private final char[] current;

    private final int totalBufferSize;
    private final int lastElemOfFirstBuffer;
    private final int lastElemOfSecondBuffer;

    public Buffer(int bufferSize, StringReader reader) {
        this.bufferSize = bufferSize;
        this.lastElemOfFirstBuffer = bufferSize;
        this.lastElemOfSecondBuffer = bufferSize * 2 + 1;
        this.totalBufferSize = bufferSize * 2 + 2;

        this.reader = reader;
        this.current = new char[totalBufferSize];
        this.current[lastElemOfFirstBuffer] = current[lastElemOfSecondBuffer] = '$';

        int len = read(reader, 0);
        if (len < bufferSize)
            this.current[len] = '$';
    }

    public char read() {
        char ch = current[forward++];

        if (ch != '$')
            return ch;

        if (forward == lastElemOfFirstBuffer + 1) {
            ensureNotOverflown();

            int len = read(reader, lastElemOfFirstBuffer + 1);
            if (len <= 0) return '$';
            if (len < bufferSize) current[lastElemOfFirstBuffer + 1 + len] = '$';

            return current[forward++];
        } else if (forward == lastElemOfSecondBuffer + 1) {
            forward = 0;
            ensureNotOverflown();

            int len = read(reader, 0);
            if (len <= 0) return '$';
            if (len < bufferSize) current[len] = '$';

            return current[forward++];
        } else {
            --forward;
            assert (current[forward] == '$');
            return current[forward];
        }
    }

    private void ensureNotOverflown() {
        if (isBeginAndForwardInOneBuffer() && (begin >= forward))
            throw new Error("buffer overflow");
    }

    public String getString() {
        if (isBeginAndForwardInOneBuffer()) {
            return new String(Arrays.copyOfRange(current, begin, forward));
        } else {
            if (isBeginInFirstBuffer()) {
                return new String(Arrays.copyOfRange(current, begin, lastElemOfFirstBuffer)) +
                        new String(Arrays.copyOfRange(current, lastElemOfFirstBuffer + 1, forward));
            } else {
                return new String(Arrays.copyOfRange(current, begin, lastElemOfSecondBuffer)) +
                        new String(Arrays.copyOfRange(current, 0, forward));

            }

        }
    }

    public void setBegin(int begin) {
        assert ((begin >= 0 && begin <= lastElemOfSecondBuffer));

        if (current[begin] == '$')
            begin = (begin + 1) % totalBufferSize;

        this.begin = begin;
    }

    public void setForward(int forward) {
        assert ((forward >= 0 && forward <= lastElemOfSecondBuffer));

        if (current[forward] == '$')
            forward = (forward + 1) % totalBufferSize;

        this.forward = forward;
    }

    public int getForward() {
        return forward;
    }

    private boolean isBeginInFirstBuffer() {
        return begin >= 0 && begin <= lastElemOfFirstBuffer;
    }

    private boolean isBeginInSecondBuffer() {
        return begin > lastElemOfFirstBuffer && begin <= lastElemOfSecondBuffer;
    }

    private boolean isForwardInFirstBuffer() {
        return forward >= 0 && forward <= lastElemOfFirstBuffer;
    }

    private boolean isForwardInSecondBuffer() {
        return forward > lastElemOfFirstBuffer && forward <= lastElemOfSecondBuffer;
    }

    private boolean isBeginAndForwardInOneBuffer() {
        return (isBeginInFirstBuffer() && isForwardInFirstBuffer()) ||
                (isBeginInSecondBuffer() && isForwardInSecondBuffer());
    }

    private int read(StringReader reader, int pos) {
        int len = 0;
        try {
            len = reader.read(current, pos, bufferSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return len;
    }
}
