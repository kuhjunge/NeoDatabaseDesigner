/*******************************************************************************
 * Copyright (C) 2017 Chris Deter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package de.mach.tools.neodesigner.core.datamodel.impl;

import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;

/**
 * Implementation von Node.
 *
 * @author Chris Deter
 *
 */
abstract class NodeImpl implements Node {
  private String name = "";
  private Table nodeOf = null;
  private String comment = "";

  /**
   * Konstruktor Node.
   *
   * @param n
   *          der Name
   */
  NodeImpl(final String n) {
    name = n;
  }

  /**
   * Konstruktor Node.
   *
   * @param n
   *          der Name
   * @param nodeOf
   *          das übergeordnete Node
   */
  NodeImpl(final String n, final Table nodeOf) {
    name = n;
    this.nodeOf = nodeOf;
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public void setComment(final String comment) {
    this.comment = comment;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public Table getTable() {
    return nodeOf;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public int compareTo(final Node o) {
    return getName().compareToIgnoreCase(o.getName());
  }
}
