package org.okanatov.lexer;

import java.io.IOException;
import java.io.StringReader;

/** Simple buffer that reads a number of characters from {@link StringReader},
 * stores them in an internal storage and returns them one by one until all
 * no characters in the storage are left. Then, the class reads a new
 * portion of characters in to the storage.
 * Simple unget, which makes the class to return the last-read character again,
 * is supported.
 */
class Buffer {
  public static final int EOF = 256;

  private final int size;
  private final StringReader source;
  private final char[] storage;
  private int pos; // inv: points to position in the storage next to the last-read character 
  private int read; // inv: contains a number of characters read successfully

  public Buffer(int size, StringReader source) {
    this.size = size;
    this.source = source;
    this.storage = new char[size];
    this.pos = 0;
    this.read = 0;
  }

  public char getc() throws IOException {
    if (pos == read) {
      int charsAmount = source.read(storage, 0, size);
      if (charsAmount == -1) {
        return EOF;
      }
      pos = 0;
      read = charsAmount;
    }

    return storage[pos++];
  }

  public void ungetc() {
    assert pos > 0;
    pos--;
  }
}
