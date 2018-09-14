package org.okanatov.lexer;

import org.okanatov.lexer.Node;

public final class StarNode implements Node {
  private Node node;

  public StarNode(final Node node) {
    this.node = node;
  }

  public void build() {
  }

  public String toString() {
    return new String("StarNode(" + node.toString() + ")");
  }
}
