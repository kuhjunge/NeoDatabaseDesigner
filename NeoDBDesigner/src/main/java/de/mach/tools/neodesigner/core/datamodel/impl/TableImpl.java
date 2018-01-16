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

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation von Table.
 *
 * @author Chris Deter
 *
 */
public class TableImpl extends NodeImpl implements Table {
  private String kategory = Strings.CATEGORYNONE;
  private final List<Field> data = new ArrayList<>();
  private Index xpk;
  private final List<ForeignKey> foreignKeys = new ArrayList<>();
  private final List<ForeignKey> refForeignKeys = new ArrayList<>();
  private final List<Index> indizies = new ArrayList<>();

  /**
   * Konstruktor Table.
   *
   * @param name
   *          der Tabelle
   */
  public TableImpl(final String name) {
    super(name);
    xpk = new IndexImpl(Strings.INDEXTYPE_XPK + name, this);
  }

  @Override
  public void addField(final Field f) {
    if (data.contains(f)) {
      data.remove(f);
    }
    data.add(f);
  }

  @Override
  public String getCategory() {
    return kategory;
  }

  @Override
  public void setCategory(final String kategory) {
    this.kategory = kategory;
  }

  @Override
  public List<Field> getFields() {
    return data;
  }

  @Override
  public Index getXpk() {
    return xpk;
  }

  @Override
  public void setXpk(final Index ixpk) {
    xpk = ixpk;
  }

  @Override
  public List<ForeignKey> getForeignKeys() {
    return foreignKeys;
  }

  @Override
  public List<Index> getIndizies() {
    return indizies;
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Table)) {
      return false;
    }
    final Table other = (Table) o;
    return getName().equals(other.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public List<ForeignKey> getRefForeignKeys() {
    return refForeignKeys;
  }

  @Override
  public Optional<Field> getField(final String fieldName) {
    Optional<Field> field = Optional.empty();
    final int index = data.indexOf(new FieldImpl(fieldName));
    if (index >= 0) {
      field = Optional.ofNullable(data.get(index));
    }
    return field;
  }

  @Override
  public String toString() {
    return getName() + Strings.COLON + Strings.SPACE + getCategory() + Strings.EOL + getFields() + Strings.EOL
        + getXpk() + Strings.EOL + getIndizies() + Strings.EOL + getForeignKeys();
  }

  @Override
  public void deleteField(final String fieldName) {
    final Field f = new FieldImpl(fieldName);
    if (data.contains(f)) {
      data.remove(data.indexOf(f));
    }
  }
}
