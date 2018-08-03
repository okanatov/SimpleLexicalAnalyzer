package org.okanatov.lexer;

import java.io.StringReader;
import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This is a double buffer class.
 */
final public class DoubleBuffer {
    public static final int eof = 256;

    private static Logger logger = LogManager.getLogger(DoubleBuffer.class);

    private int size;
    private StringReader source;
    private char[] buffer;
    private int forward = 0;
    private final int startOfFirst;
    private final int endOfFirst;
    private final int startOfSecond;
    private final int endOfSecond;

    public DoubleBuffer(int s, StringReader r) throws IOException {
        logger.traceEntry("Ctor: {}, {}", s, r);

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
     * Loads characters from {@link StringReader} to the internal buffer, starting
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
