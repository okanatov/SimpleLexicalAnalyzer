package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;
import java.util.function.Predicate;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Double buffer class.
 * It reads characters from {@link StringReader} and keeps them in an internal buffer.
 * The internal buffer is divided in to 2 halves. As all the characters from one half
 * are returned to an application, the other half is filled up with new characters.
 */
public final class DoubleBuffer {
  public static final int EOF = 256;

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
  private Queue<Integer> loadedHalves = new LinkedList<>();
  private Predicate<Integer> isInFirstHalf;
  private Predicate<Integer> isInSecondHalf;

  /**
   * Takes size of a half of the internal buffer and
   * the characters source {@link StringReader},
   * returns the buffer object.
   *
   *  @param size size of a half of the buffer. The total buffer size
   *              consisting of two halves is twice more.
   *  @param source {@link StringReader} object to read characters from.
   */
  public DoubleBuffer(final int size, final StringReader source) throws IOException {
    logger.traceEntry("Ctor: {}, {}", size, source);

    // Always check functions input
    // TODO: Assertions should verify conditions. Wrong parameters should be handled by Exceptions.
    assert size != 0;
    assert source != null;

    this.size = size;
    this.source = source;

    startOfFirst = 0;
    endOfFirst = size;
    startOfSecond = size + 1;
    endOfSecond = size * 2 + 1;

    isInFirstHalf = (pos) -> pos >= startOfFirst && pos <= endOfFirst;
    isInSecondHalf = (pos) -> pos >= startOfSecond && pos <= endOfSecond;

    buffer = new char[size * 2 + 2];
    load(startOfFirst);

    logger.traceExit();
  }

  public DoubleBufferIterator iterator() {
    return DoubleBuffer.this.new DoubleBufferIterator();
  }

  /** 
   * Returns the next character from the internal buffer.
   * In case DoubleBuffer.EOF is reached at end of any halves, then reloads
   * the other half. Returns DoubleBuffer.EOF at once if this character
   * is reached not at end of the halves.
   *
   * @return the next character from the buffer or DoubleBuffer.EOF if EOF is reached.
   */
  public char getc() throws IOException, Error {
    logger.traceEntry("getc");

    char ch = buffer[forward++];

    switch (ch) {
      case EOF:
        if (forward == endOfFirst + 1) {
          logger.debug("EOF found at end of the first buffer. Loading the second...");

          if (isInSecondHalf.test(begin)) { // TODO: extract to a separate method
            forward--;
            throw new Error("Buffer overflown");
          } else {
            loadedHalves.remove();

            if (loadedHalves.isEmpty()) {
              load(startOfSecond);
            }

            forward = startOfSecond;

            if (begin == endOfFirst) {
              begin = startOfSecond;
            }

            return getc();
          }

        } else if (forward == endOfSecond + 1) {
          logger.debug("EOF found at end of the second buffer. Loading the first...");

          if (isInFirstHalf.test(begin)) {
            forward--;
            throw new Error("Buffer overflown"); // TODO: Error shouldn't be thrown in a code
          } else {
            loadedHalves.remove();

            if (loadedHalves.isEmpty()) {
              load(startOfFirst);
            }

            forward = startOfFirst;

            if (begin == endOfSecond) {
              begin = startOfFirst;
            }
            return getc();
          }

        } else {
          logger.debug("EOF found. Quiting...");

          logger.traceExit();
          return EOF;
        }
      default:
        logger.debug("Non-EOF found: {}", ch);

        logger.traceExit(ch);
        return ch;
    }
  }

  /**
   * Returns size of the string.
   *
   * @return size of the string
   */
  public int getSize() {
    logger.traceEntry("getSize");

    int size;

    if (arePointersInSameHalf()) {
      logger.debug("Begin and forward are in same half");

      assert begin <= forward;

      size = forward - begin;
    } else {
      if (isInFirstHalf.test(begin)) {
        logger.debug("Begin and forward are not in same half. Begin in the first half");

        size = forward - begin - 1;
      } else {
        logger.debug("Begin and forward are not in same half. Begin in the second half");

        size = endOfSecond - (begin - forward);
      }
    }

    return size;
  }

  /**
   * Returns a string of characters located between the begin and
   * the forward pointers. Makes begin point to the same position
   * as forward does.
   *
   * @return a string of characters located between the begin and
   *         the forward pointers.
   */
  public String getString() {
    logger.traceEntry("getString");

    String result;
    final String buffer = new String(this.buffer); // Convert buffer array to String

    if (arePointersInSameHalf()) {
      logger.debug("Begin and forward are in same half");

      assert begin <= forward;

      result = buffer.substring(begin, forward);
    } else {
      if (isInFirstHalf.test(begin)) {
        logger.debug("Begin and forward are not in same half. Begin in the first half");

        result = buffer.substring(begin, endOfFirst) + buffer.substring(startOfSecond, forward);
      } else {
        logger.debug("Begin and forward are not in same half. Begin in the second half");

        result = buffer.substring(begin, endOfSecond) + buffer.substring(startOfFirst, forward);
      }
    }

    begin = forward;

    logger.traceExit(result);
    return result;
  }

  /**
   * Returns one last-read character back to the buffer so that
   * next getc() will return it again.
   */
  public void ungetc() {
    logger.traceEntry("ungetc");

    forward = (endOfSecond + 1 + (forward - 1)) % (endOfSecond + 1);

    if (buffer[forward] == DoubleBuffer.EOF) {
      if (forward == endOfFirst) {
        loadedHalves.add(2);
        --forward;
      } else if (forward == endOfSecond) {
        loadedHalves.add(1);
        --forward;
      } else {
        logger.debug("End of stream has been reached");
      }
    }

    logger.traceExit();
  }

  /**
   * Checks if the begin and forward pointers are in the same half.
   *
   * @return true if both pointers are in the same half, false otherwise.
   */
  private boolean arePointersInSameHalf() {
    return isInFirstHalf.test(begin) && isInFirstHalf.test(forward)
           ||
           isInSecondHalf.test(begin) && isInSecondHalf.test(forward);
  }

  /** 
   * Loads characters from {@link StringReader} to the internal buffer starting
   * from position pos.
   *
   * @param pos Offset at which to start writing characters in the buffer
   */
  private void load(final int pos) throws IOException {
    logger.traceEntry("load: {}", pos);

    assert pos == startOfFirst || pos == startOfSecond;

    final int len = source.read(buffer, pos, size);

    if (len <= 0) {
      logger.debug("End of the stream has been reached");

      buffer[pos] = EOF;
    } else {
      buffer[pos + len] = EOF;
    }

    if (pos == startOfFirst) { // TODO: move it from here to the getc method
      loadedHalves.add(1);
    } else {
      loadedHalves.add(2);
    }

    logger.traceExit();
  }

  public class DoubleBufferIterator {
    private int forward;
    private int begin;

    public DoubleBufferIterator() {
      logger.traceEntry("DoubleBufferIterator Ctor");
      forward = 0;
      begin = 0;
      logger.traceExit();
    }

    public boolean hasNext() {
      logger.traceEntry("hasNext");

      if (buffer[forward] == EOF) {
        logger.debug("EOF found");
        logger.traceExit("false");
        return false;
      }

      logger.traceExit("true");
      return true;
      }

    public char next() {
      logger.traceEntry("next");
      logger.traceExit();
      return buffer[forward++];
    }
  }
}
