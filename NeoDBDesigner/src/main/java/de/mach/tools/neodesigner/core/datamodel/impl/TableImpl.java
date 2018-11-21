/* Copyright (C) 2018 Chris Deter Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The
 * above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */

package de.mach.tools.neodesigner.core.datamodel.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;


/** Implementation von Table.
 *
 * @author Chris Deter */
public class TableImpl extends NodeImpl implements Table {
  private String category = Strings.CATEGORYNONE;
  private final List<Field> fields = new ArrayList<>();
  private Index xpk;
  private final List<ForeignKey> foreignKeys = new ArrayList<>();
  private final List<ForeignKey> refForeignKeys = new ArrayList<>();
  private final List<Index> indizies = new ArrayList<>();

  /** Konstruktor Table.
   *
   * @param name der Tabelle */
  public TableImpl(final String name) {
    super(name);
    xpk = new IndexImpl(Strings.INDEXTYPE_XPK + getName(), this);
  }

  public TableImpl(final Table o) {
    super(o.getName());
    setCategory(o.getCategory());
    setComment(o.getComment());
    for (final Field f : o.getFields()) {
      addField(new FieldImpl(f.getName(), f.getDomain(), f.getDomainLength(), f.isRequired(), f.getComment(), this));
    }
    xpk = new IndexImpl(o.getXpk(), this);
    for (final Field f : xpk.getFieldList()) {
      f.setPartOfPrimaryKey(true);
      xpk.setOrder(f.getName(), o.getXpk().getOrder(f.getName(), false), false);
    }
    for (final Index i : o.getIndizies()) {
      addIndex(new IndexImpl(i, this));
    }
  }

  @Override
  public void addField(final Field f) {
    if (fields.contains(f)) {
      fields.remove(f);
    }
    fields.add(f);
    Collections.sort(fields);
  }

  @Override
  public void addForeignKey(final ForeignKey fk, final boolean refkey) {
    if (refkey) {
      if (refForeignKeys.contains(fk)) {
        refForeignKeys.remove(fk);
      }
      refForeignKeys.add(fk);
    }
    else {
      if (foreignKeys.contains(fk)) {
        foreignKeys.remove(fk);
      }
      foreignKeys.add(fk);
    }
  }

  @Override
  public void addIndex(final Index i) {
    if (indizies.contains(i)) {
      indizies.remove(i);
    }
    indizies.add(i);
  }

  @Override
  public void deleteField(final String fieldName) {
    final Field f = new FieldImpl(fieldName);
    if (fields.contains(f)) {
      fields.remove(fields.indexOf(f));
    }
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
  public String getCategory() {
    return category;
  }

  @Override
  public Optional<Field> getField(final String fieldName) {
    Optional<Field> field = Optional.empty();
    final int index = fields.indexOf(new FieldImpl(fieldName));
    if (index >= 0) {
      field = Optional.ofNullable(fields.get(index));
    }
    return field;
  }

  @Override
  public List<Field> getFields() {
    // TODO: Direkten Zugriff entfernen (achtung, ViewTable kann dann Felder nicht
    // mehr updaten)
    return fields;
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
  public String getNodeType() {
    return "Table";
  }

  @Override
  public List<ForeignKey> getRefForeignKeys() {
    return refForeignKeys;
  }

  @Override
  public Index getXpk() {
    return xpk;
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public void setCategory(final String category) {
    this.category = category;
  }

  @Override
  public void setXpk(final Index ixpk) {
    xpk = ixpk;
  }

  @Override
  public String toString() {
    return getName() + Strings.COLON + Strings.SPACE + getCategory() + Strings.EOL + getFields() + Strings.EOL
           + getXpk() + Strings.EOL + getIndizies() + Strings.EOL + getForeignKeys();
  }
}
