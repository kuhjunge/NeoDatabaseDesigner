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
import java.util.List;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.FieldList;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;


/** Implementation von Index.
 *
 * @author Chris Deter */
public class IndexImpl extends NodeImpl implements Index {
  /** Ändert den Type eines Indexes anhand des Names.
   *
   * @param name des Indexes
   * @return entsprechender Typ des Indexes */
  private static Type setIndexType(final String name) {
    Type ret = Type.XIE;
    switch (name.substring(0, 3)) {
      case Strings.INDEXTYPE_XPK:
        ret = Type.XPK;
        break;
      case Strings.INDEXTYPE_XIG:
        // Genauso behandeln wie XIF
      case Strings.INDEXTYPE_XIF:
        ret = Type.XIF;
        break;
      case Strings.INDEXTYPE_XAK:
        ret = Type.XAK;
        break;
    }
    return ret;
  }

  private final FieldList fields = new OrderedFieldList(new ArrayList());

  private Type indexType = Type.XIE;

  /** Copy Constructor. Verwendet vorhandene Felder und die vorhandene Obertabelle.
   *
   * @param i Der zu kopierende Index */
  public IndexImpl(final Index i, final Table origin) {
    super(i.getName(), origin);
    indexType = i.getType();
    for (final Field f : i.getFieldList()) {
      final Field newField = origin.getField(f.getName());
      if (newField != null) {
        addField(newField);
        setOrder(newField.getName(), i.getOrder(newField.getName(), false), false);
        if (indexType.equals(Type.XIF)) {
          setOrder(newField.getName(), i.getOrder(newField.getName(), true), true);
        }
      }
    }
  }

  /** Konstruktor Index.
   *
   * @param name des Indexes
   * @param nodeOf Übergeordnete Tabelle */
  public IndexImpl(final String name, final Table nodeOf) {
    super(name, nodeOf);
    changeTypeViaName(name);
  }

  /** Konstruktor Index.
   *
   * @param name des Indexes
   * @param type des Indexes
   * @param nodeOf übergeorndete Tabelle */
  public IndexImpl(final String name, final Type type, final Table nodeOf) {
    super(name, nodeOf);
    indexType = type;
  }

  @Override
  public void addField(final Field f) {
    fields.addField(f);
  }

  /** Ändert den Typ mithilfe des Namens.
   *
   * @param name der Name */
  private void changeTypeViaName(final String name) {
    if (name.length() > 3) {
      indexType = IndexImpl.setIndexType(name);
    }
  }

  @Override
  public void clearFieldList() {
    fields.clear();
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Index)) {
      return false;
    }
    final Index other = (Index) o;
    return getName().equals(other.getName());
  }

  @Override
  public Field getFieldByOrder(final int order, final boolean ref) {
    return ref ? fields.getRefFieldByOrder(order) : fields.getFieldByOrder(order);
  }

  @Override
  public List<Field> getFieldList() {
    return fields.get();
  }

  @Override
  public String getNodeType() {
    return "Index";
  }

  @Override
  public Integer getOrder(final String name, final boolean isRef) {
    return isRef ? fields.getReferenceOrder(name) : fields.getOrder(name);
  }

  @Override
  public Type getType() {
    return indexType;
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public boolean isUnique() {
    return indexType == Type.XAK || indexType == Type.XPK;
  }

  @Override
  public void removeField(final int i) {
    fields.deleteField(fields.getFieldByOrder(i + 1).getName());
  }

  @Override
  public void replaceField(final Field field, final String oldFieldName) {
    fields.replaceField(field, oldFieldName);
  }

  @Override
  public void setName(final String name) {
    String n = name.toUpperCase();
    super.setName(n);
    changeTypeViaName(n);
  }

  @Override
  public void setOrder(final String name, final int order, final boolean isRef) {
    if (isRef) {
      fields.setReferenceOrder(name, order);
    }
    else {
      fields.setOrder(name, order);
    }
  }

  @Override
  public void setType(final Type indexType) {
    this.indexType = indexType;
    switch (indexType) {
      case XAK:
        setName(Strings.INDEXTYPE_XAK + getName().substring(3));
        break;
      case XIE:
        setName(Strings.INDEXTYPE_XIE + getName().substring(3));
        break;
      case XIF:
        setName(Strings.INDEXTYPE_XIF + getName().substring(3));
        break;
    }
  }

  @Override
  public void setUnique(final boolean unique) {
    if (getName().length() > 3 && (indexType == Type.XAK || indexType == Type.XIE)) {
      if (unique) {
        setType(Type.XAK);
      }
      else {
        setType(Type.XIE);
      }
    }
  }

  @Override
  public boolean hasField(String fieldname) {
    return fields.getField(fieldname) != null;
  }
}
