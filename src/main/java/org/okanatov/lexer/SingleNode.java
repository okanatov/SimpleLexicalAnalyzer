package org.okanatov.lexer;

import org.okanatov.lexer.Node;

public final class SingleNode implements Node {
  private char ch;

  public SingleNode(final char ch) {
    this.ch = ch;
  }

  public void build() {
  }

  public String toString() {
    return new String("SingleNode(" + ch + ")");
  }
}
