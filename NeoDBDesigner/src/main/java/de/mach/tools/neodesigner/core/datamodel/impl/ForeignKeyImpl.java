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

import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;

/**
 * Implementation von ForeignKey.
 *
 * @author Chris Deter
 *
 */
public class ForeignKeyImpl extends NodeImpl implements ForeignKey {

  private Table refTable = null;
  private Index fieldIndex = null;

  /**
   * Konstruktor Foreign Key.
   *
   * @param name
   *          des FK
   * @param nodeOf
   *          Tabelle dem der FK zugeordnet ist
   */
  public ForeignKeyImpl(final String name, final Table nodeOf) {
    super(name, nodeOf);
  }

  @Override
  public Table getRefTable() {
    return refTable;
  }

  @Override
  public void setRefTable(final Table refTable) {
    this.refTable = refTable;
  }

  @Override
  public Index getIndex() {
    return fieldIndex;
  }

  @Override
  public void setIndex(final Index i) {
    fieldIndex = i;
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof ForeignKey)) {
      return false;
    }
    final ForeignKey other = (ForeignKey) o;
    return getName().equals(other.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
