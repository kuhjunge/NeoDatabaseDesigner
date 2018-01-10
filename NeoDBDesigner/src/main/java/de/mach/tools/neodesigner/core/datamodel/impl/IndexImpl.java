package de.mach.tools.neodesigner.core.datamodel.impl;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;

import java.util.ArrayList;
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
   * Konstruktor Index
   *
   * @param name
   *          des Indexes
   * @param nodeOf
   *          Übergeordnete Tabelle
   */
  public IndexImpl(final String name, final Node nodeOf) {
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
  public IndexImpl(final String name, final Type type, final Node nodeOf) {
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
   * Ändert den Typ mithilfe des Namens
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
    return indexType == Type.XAK;
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
    fields.add(new FieldWrapper(f, altName, fields.size() + 1));
  }

  @Override
  public void addField(final Field f) {
    addField(f, "");
  }

  @Override
  public List<Field> getFieldList() {
    final List<Field> fl = new ArrayList<>();
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
    if (name.charAt(0) == Strings.INDEXTYPE_R) {
      ret = Type.R;
    } else if (name.substring(0, 3).equals(Strings.INDEXTYPE_XPK)) {
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
    fields.remove(i);
    for (int x = i; x < fields.size(); x++) {
      fields.get(x).setOrder(x + 1);
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
    String str = "";
    for (final FieldWrapper fw : fields) {
      if (fw.getField().getName().equals(name)) {
        str = fw.getAltName();
      }
    }
    return str;
  }

  @Override
  public String getOrder(final String name) {
    String str = "";
    for (final FieldWrapper fw : fields) {
      if (fw.getField().getName().equals(name)) {
        str = Integer.toString(fw.getOrder());
      }
    }
    return str;
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
