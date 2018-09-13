package org.okanatov.lexer;

import org.okanatov.lexer.Node;

final public class AlternationNode implements Node {
    private Node left;
    private Node right;

    public AlternationNode(final Node left, final Node right) {
        this.left = left;
        this.right = right;
    }

  public void build() {
  }

  public String toString() {
    return new String("AlternationNode(left: " + left.toString() + ", right: " + right.toString() + ")");
  }
}
