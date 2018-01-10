package de.mach.tools.neodesigner.core.datamodel.impl;

import de.mach.tools.neodesigner.core.datamodel.Node;

/**
 * Implementation von Node.
 *
 * @author Chris Deter
 *
 */
abstract class NodeImpl implements Node {
  private String name = "";
  private Node nodeOf = null;

  /**
   * Konstruktor Node
   * 
   * @param n
   *          der Name
   */
  NodeImpl(final String n) {
    name = n;
  }

  /**
   * Konstruktor Node
   * 
   * @param n
   *          der Name
   * @param nodeOf
   *          das übergeordnete Node
   */
  NodeImpl(final String n, final Node nodeOf) {
    name = n;
    this.nodeOf = nodeOf;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(final String n) {
    name = n;
  }

  @Override
  public Node getNodeOf() {
    return nodeOf;
  }

  @Override
  public String toString() {
    return getName();
  }
}
