package de.mach.tools.neodesigner.core.datamodel.impl;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation von Table.
 *
 * @author Chris Deter
 *
 */
public class TableImpl extends NodeImpl implements Table {
  private String kategory = "none";
  private final List<Field> data = new ArrayList<>();
  private Index xpk;
  private final List<ForeignKey> foreignKeys = new ArrayList<>();
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
  public List<Field> getData() {
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
}
