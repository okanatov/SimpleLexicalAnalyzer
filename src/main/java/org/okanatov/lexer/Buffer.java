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

    private final int lastElemOfFirstBuffer;
    private final int lastElemOfSecondBuffer;

    public Buffer(int bufferSize, StringReader reader) {
        this.bufferSize = bufferSize;
        this.lastElemOfFirstBuffer = bufferSize;
        this.lastElemOfSecondBuffer = bufferSize * 2 + 1;
        int totalBufferSize = bufferSize * 2 + 2;

        this.reader = reader;
        this.current = new char[totalBufferSize];
        this.current[lastElemOfFirstBuffer] = current[lastElemOfSecondBuffer] = '$';

        tryRead();
    }

    public char read() {
        char ch = current[forward++];
        ensureNotOverflown();

        if (ch != '$')
            return ch;

        if (forward == lastElemOfFirstBuffer + 1) {
            if (!tryRead()) return returnEnd();

            return current[forward++];
        } else if (forward == lastElemOfSecondBuffer + 1) {
            forward = 0;
            if (!tryRead()) return returnEnd();

            return current[forward++];
        } else {
            return returnEnd();
        }
    }

    public String getString() {
        if (isBeginAndForwardInOneBuffer()) {
            return new String(Arrays.copyOfRange(current, begin, forward));
        } else {
            if (isPosInFirstBuffer(begin)) {
                return new String(Arrays.copyOfRange(current, begin, lastElemOfFirstBuffer)) +
                        new String(Arrays.copyOfRange(current, lastElemOfFirstBuffer + 1, forward));
            } else {
                return new String(Arrays.copyOfRange(current, begin, lastElemOfSecondBuffer)) +
                        new String(Arrays.copyOfRange(current, 0, forward));

            }

        }
    }

    public void setBegin(int newBegin) {
        assert ((newBegin >= 0 && newBegin < lastElemOfSecondBuffer));
        if (newBegin >= lastElemOfFirstBuffer) newBegin++;
        if (newBegin == lastElemOfSecondBuffer) newBegin = 0;

        this.begin = newBegin;
    }

    public void setForward(int newForward) {
        assert ((newForward >= 0 && newForward < lastElemOfSecondBuffer));
        if (newForward >= lastElemOfFirstBuffer) newForward++;
        if (newForward == lastElemOfSecondBuffer) newForward = 0;

        this.forward = newForward;
    }

    public int getBegin() {
        int temp = begin;
        if (temp > lastElemOfFirstBuffer) temp--;

        return temp;
    }

    public int getForward() {
        int temp = forward;
        if (temp > lastElemOfFirstBuffer) temp--;

        return temp;
    }

    private boolean isBeginAndForwardInOneBuffer() {
        return (isPosInFirstBuffer(begin) && isPosInFirstBuffer(forward)) ||
                (isPosInSecondBuffer(begin) && isPosInSecondBuffer(forward));
    }

    private boolean isPosInFirstBuffer(final int pos) {
        return pos >= 0 && pos <= lastElemOfFirstBuffer;
    }

    private boolean isPosInSecondBuffer(final int pos) {
        return pos > lastElemOfFirstBuffer && pos <= lastElemOfSecondBuffer;
    }

    private char returnEnd() {
        --forward;
        assert (current[forward] == '$');
        return current[forward];
    }

    private boolean tryRead() {
        int len = read(reader, forward);
        if (len <= 0) {
            current[forward] = '$';
            return false;
        }
        if (len < bufferSize) current[forward + len] = '$';

        return true;
    }

    private int read(StringReader reader, final int pos) {
        int len = 0;
        try {
            len = reader.read(current, pos, bufferSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return len;
    }

    private void ensureNotOverflown() {
        if (isBeginAndForwardInOneBuffer() && (begin > forward))
            throw new Error("buffer overflow");
    }
}
