package org.okanatov.lexer;

import org.okanatov.lexer.Node;

public final class ConcatinationNode implements Node {
  private Node left;
  private Node right;

  public ConcatinationNode(final Node left, final Node right) {
    this.left = left;
    this.right = right;
  }

  public void build() {
  }

  public String toString() {
    return new String("ConcatinationNode(left: " + left.toString()
        + ", right: " + right.toString() + ")");
  }
}
