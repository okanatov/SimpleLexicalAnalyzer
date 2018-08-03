package org.okanatov.lexer;

import java.io.StringReader;
import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This is a double buffer class. It reads characters from {@link StringReader} passed in the class
 * constructor and keeps them in an internal buffer. The class considers the buffer divided in to
 * same halfs. If all the characters from the first half are returned to an application, the second
 * half is filled in with new characters. The same thing is if the characters from the second half
 * are returned to an application. The first half of the buffer is loaded, then.
 */
final public class DoubleBuffer {
    public static final int eof = 256;

    private static Logger logger = LogManager.getLogger(DoubleBuffer.class);

    private int size;
    private StringReader source;
    private final int startOfFirst;
    private final int endOfFirst;
    private final int startOfSecond;
    private final int endOfSecond;
    private char[] buffer;
    private int forward = 0;
    private int begin = 0;

    /**
     * Takes size of a half of the buffer, i.e. the total buffer will be twice more, and {@link StringReader}
     * object to read characters from.
     *
     *  @param s size of a half of the buffer. The total buffer size consisting of two halves is twice more
     *  @param r {@link StringReader} object to read characters from
     */
    public DoubleBuffer(int s, StringReader r) throws IOException {
        logger.traceEntry("Ctor: {}, {}", s, r);

        // Always check functions input
        assert s != 0;
        assert r != null;

        size = s;
        source = r;

        startOfFirst = 0;
        endOfFirst = size;
        startOfSecond = size + 1;
        endOfSecond = size * 2 + 1;

        buffer = new char[size * 2 + 2];

        load(startOfFirst);

        logger.exit();
    }

    /** 
     * Returns the next character from the internal buffer.
     * In case DoubleBuffer.eof is reached at end of the first buffer half or
     * at end of the second one, then reloads the opposite half. Returns
     * DoubleBuffer.eof at once if this character is reached not at end of the halfs.
     *
     * @return the next character from the buffer or DoubleBuffer.eof if EOF is reached.
     */
    public char getc() throws IOException {
        logger.traceEntry("getc");

        char ch = buffer[forward++];

        switch (ch) {
            case eof:
                if (forward == endOfFirst + 1) {
                    logger.debug("EOF found at end of the first buffer. Loading the second...");

                    load(startOfSecond);
                    forward = startOfSecond;
                    return getc();

                } else if (forward == endOfSecond + 1) {
                    logger.debug("EOF found at end of the second buffer. Loading the first...");

                    load(startOfFirst);
                    forward = startOfFirst;
                    return getc();

                } else {
                    logger.debug("EOF found. Quiting...");

                    logger.exit();
                    return eof;
                }
            default:
                logger.debug("Non-EOF found: {}", ch);

                logger.exit(ch);
                return ch;
        }
    }

    /**
     * Returns a string of characters located between the begin and the forward pointers. Makes begin point
     * to the same position as forward does.
     *
     * @return a string of characters located between the begin and the forward pointers
     */
    public String getString() {
        logger.traceEntry("getString");

        String result = "";
        String buffer = new String(this.buffer); // Convert buffer array to String

        if(ArePointersInSameHalf()) {
            logger.debug("Begin and forward are in same half");

            result = buffer.substring(begin, forward);
        } else {
            if(begin >= startOfFirst && begin < endOfFirst) {
                logger.debug("Begin and forward are not in same half. Begin in the first half");

                result = buffer.substring(begin, endOfFirst) + buffer.substring(startOfSecond, forward);
            } else {
                logger.debug("Begin and forward are not in same half. Begin in the second half");

                result = buffer.substring(begin, endOfSecond) + buffer.substring(startOfFirst, forward);
            }
        }

        begin = forward;

        logger.exit(result);
        return result;
    }

    /**
     * Checks if the begin and forward pointers are in the same half.
     *
     * @return true if both pointers are in the same half, false otherwise
     */
    private boolean ArePointersInSameHalf() {
        if ((begin >= startOfFirst && begin < endOfFirst) &&       // We can create a method to check
            (forward >= startOfFirst && forward < endOfFirst)) {   // whether a pointer is in first half or not
                return true;
        }

        if ((begin >= startOfSecond && begin < endOfSecond) &&
            (forward >= startOfSecond && forward < endOfSecond)) {
                return true;
        }

        return false;
    }

    /** 
     * Loads characters from {@link StringReader} to the internal buffer starting
     * from position pos.
     *
     * @param pos Offset at which to start writing characters in the buffer
     */
    private void load(int pos) throws IOException {
        logger.traceEntry("load: {}", pos);

        assert((pos == startOfFirst) || (pos == startOfSecond));

        int len = source.read(buffer, pos, size);

        if (len <= 0) {
            logger.debug("End of the stream has been reached");
            buffer[pos] = eof;
        } else {
            buffer[pos + len] = eof;
        }

        logger.exit();
    }
}
