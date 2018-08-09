package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a double buffer class. It reads characters from {@link StringReader} passed in the class
 * constructor and keeps them in an internal buffer. The class considers the buffer divided in to
 * same halfs. If all the characters from the first half are returned to an application, the second
 * half is filled in with new characters. The same thing is if the characters from the second half
 * are returned to an application. The first half of the buffer is loaded, then.
 */
final public class DoubleBuffer {
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

  /**
   * Taking size of a half of the buffer and the characters source
   * {@link StringReader}, returns the buffer object.
   *
   *  @param size size of a half of the buffer. The total buffer size
   *              consisting of two halves is twice more.
   *  @param source {@link StringReader} object to read characters from.
   */
  public DoubleBuffer(final int size, final StringReader source) throws IOException {
    logger.traceEntry("Ctor: {}, {}", size, source);

    // Always check functions input
    assert size != 0;
    assert source != null;

    this.size = size;
    this.source = source;

    startOfFirst = 0;
    endOfFirst = size;
    startOfSecond = size + 1;
    endOfSecond = size * 2 + 1;

    buffer = new char[size * 2 + 2];
    load(startOfFirst);

    logger.traceExit();
  }

  /** 
   * Returns the next character from the internal buffer.
   * In case DoubleBuffer.EOF is reached at end of the first buffer half or
   * at end of the second one, then reloads the opposite half. Returns
   * DoubleBuffer.EOF at once if this character is reached not at end of the halfs.
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

          if (isInSecondHalf(begin)) {
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

          if (isInFirstHalf(begin)) {
            forward--;
            throw new Error("Buffer overflown");
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
      if (isInFirstHalf(begin)) {
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
      if (isInFirstHalf(begin)) {
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
   * Checks if a pos is in the first buffer half or not.
   *
   * @return true if the pos is in the first half, false otherwise.
   */
  private boolean isInFirstHalf(final int pos) {
    return pos >= startOfFirst && pos <= endOfFirst;
  }

  /**
   * Checks if a pos is in the second buffer half or not.
   *
   * @return true if the pos is in the second half, false otherwise.
   */
  private boolean isInSecondHalf(final int pos) {
    return pos >= startOfSecond && pos <= endOfSecond;
  }

  /**
   * Checks if the begin and forward pointers are in the same half.
   *
   * @return true if both pointers are in the same half, false otherwise.
   */
  private boolean arePointersInSameHalf() {
    return isInFirstHalf(begin) && isInFirstHalf(forward)
           ||
           isInSecondHalf(begin) && isInSecondHalf(forward);
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

    if (pos == startOfFirst) {
      loadedHalves.add(1);
    } else {
      loadedHalves.add(2);
    }

    logger.traceExit();
  }
}
