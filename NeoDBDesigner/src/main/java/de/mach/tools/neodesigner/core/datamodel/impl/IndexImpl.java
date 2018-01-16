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
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation von Index.
 *
 * @author Chris Deter
 *
 */
public class IndexImpl extends NodeImpl implements Index {
  private final List<FieldWrapper> fields = new ArrayList<>();
  private Type indexType = Type.XIE;

  /**
   * Copy Constructor. Verwendet vorhandene Felder und die vorhandene Obertabelle.
   *
   * @param i
   *          Der zu kopierende Index
   */
  public IndexImpl(final Index i) {
    super(i.getName(), i.getTable());
    indexType = i.getType();
    for (final Field f : i.getFieldList()) {
      addField(f, i.getAltName(f.getName()));
    }
  }

  /**
   * Konstruktor Index.
   *
   * @param name
   *          des Indexes
   * @param nodeOf
   *          Übergeordnete Tabelle
   */
  public IndexImpl(final String name, final Table nodeOf) {
    super(name, nodeOf);
    changeTypeViaName(name);
  }

  /**
   * Konstruktor Index.
   *
   * @param name
   *          des Indexes
   * @param type
   *          des Indexes
   * @param nodeOf
   *          übergeorndete Tabelle
   */
  public IndexImpl(final String name, final Type type, final Table nodeOf) {
    super(name, nodeOf);
    indexType = type;
  }

  @Override
  public Type getType() {
    return indexType;
  }

  @Override
  public void setName(final String name) {
    super.setName(name);
    changeTypeViaName(name);
  }

  /**
   * Ändert den Typ mithilfe des Namens.
   *
   * @param name
   *          der Name
   */
  private void changeTypeViaName(final String name) {
    if (name.length() > 3) {
      indexType = IndexImpl.setIndexType(name);
    }
  }

  @Override
  public void setType(final Type indexType) {
    this.indexType = indexType;
    if (indexType == Type.XAK) {
      setName(Strings.INDEXTYPE_XAK + getName().substring(3));
    } else if (indexType == Type.XIE) {
      setName(Strings.INDEXTYPE_XIE + getName().substring(3));
    } else if (indexType == Type.XIF) {
      setName(Strings.INDEXTYPE_XIF + getName().substring(3));
    }
  }

  @Override
  public boolean isUnique() {
    return indexType == Type.XAK || indexType == Type.XPK;
  }

  @Override
  public void setUnique(final boolean unique) {
    if (getName().length() > 3 && (indexType == Type.XAK || indexType == Type.XIE)) {
      if (unique) {
        setType(Type.XAK);
      } else {
        setType(Type.XIE);
      }
    }
  }

  @Override
  public void addField(final Field f, final String altName) {
    // Wenn Feld "mit gleichen Namen" bereits vorhanden, dann lösche das alte Feld
    final Iterator<FieldWrapper> iter = fields.iterator();
    while (iter.hasNext()) {
      final FieldWrapper fw = iter.next();
      if (fw.getField().getName().equals(f.getName())) {
        iter.remove();
      }
    }
    // hinzufügen
    fields.add(new FieldWrapper(f, altName, fields.size() + 1));
    Collections.sort(fields);
  }

  @Override
  public void addField(final Field f) {
    addField(f, "");
  }

  @Override
  public List<Field> getFieldList() {
    final List<Field> fl = new ArrayList<>();
    Collections.sort(fields);
    for (final FieldWrapper f : fields) {
      fl.add(f.getField());
    }
    return fl;
  }

  /**
   * Ändert den Type eines Indexes anhand des Names.
   *
   * @param name
   *          des Indexes
   * @return entsprechender Typ des Indexes
   */
  private static Type setIndexType(final String name) {
    Type ret = Type.XIE;
    if (name.substring(0, 3).equals(Strings.INDEXTYPE_XPK)) {
      ret = Type.XPK;
    } else if (name.substring(0, 3).equals(Strings.INDEXTYPE_XIF)) {
      ret = Type.XIF;
    } else if (name.substring(0, 3).equals(Strings.INDEXTYPE_XAK)) {
      ret = Type.XAK;
    }
    return ret;
  }

  @Override
  public void removeField(final int i) {
    Collections.sort(fields);
    fields.remove(i);
    for (int x = i; x < fields.size(); x++) {
      fields.get(x).setOrder(x + 1);
    }
  }

  @Override
  public void replaceField(final Field field, final String oldFieldName) {
    Collections.sort(fields);
    for (final FieldWrapper fw : fields) {
      if (fw.getField().getName().equals(oldFieldName)) {
        fw.setField(field);
      }
    }
  }

  @Override
  public void clearFieldList() {
    fields.clear();
  }

  @Override
  public void setAltName(final String name, final String altName) {
    for (final FieldWrapper fw : fields) {
      if (fw.getField().getName().equals(name)) {
        fw.setAltName(altName);
      }
    }
  }

  @Override
  public String getAltName(final String name) {
    String str = Strings.EMPTYSTRING;
    for (final FieldWrapper fw : fields) {
      if (fw.getField().getName().equals(name)) {
        str = fw.getAltName();
      }
    }
    return str;
  }

  @Override
  public String getNameFromAltName(final String name) {
    String str = Strings.EMPTYSTRING;
    for (final FieldWrapper fw : fields) {
      if (fw.getAltName().equals(name)) {
        str = fw.getField().getName();
      }
    }
    return str;
  }

  @Override
  public Integer getOrder(final String name) {
    Integer order = 0;
    for (final FieldWrapper fw : fields) {
      if (fw.getField().getName().equals(name)) {
        order = fw.getOrder();
      }
    }
    return order;
  }

  @Override
  public void setOrder(final String name, final int order) {
    for (final FieldWrapper fw : fields) {
      if (fw.getField().getName().equals(name)) {
        fw.setOrder(order);
      }
    }
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
  public int hashCode() {
    return getName().hashCode();
  }
}
