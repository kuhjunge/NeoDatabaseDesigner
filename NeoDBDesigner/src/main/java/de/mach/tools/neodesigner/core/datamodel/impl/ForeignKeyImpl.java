package de.mach.tools.neodesigner.core.datamodel.impl;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation von ForeignKey.
 *
 * @author Chris Deter
 *
 */
public class ForeignKeyImpl extends IndexImpl implements ForeignKey {

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
  public ForeignKeyImpl(final String name, final Node nodeOf) {
    super(name, Index.Type.R, nodeOf);
  }

  @Override
  public Type getType() {
    return Type.R;
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
  public final List<Field> getFieldList() {
    List<Field> listOfFields = new ArrayList<>();
    if (fieldIndex != null) {
      listOfFields = fieldIndex.getFieldList();
    }
    return listOfFields;
  }

  @Override
  public void addField(final Field f) {
    fieldIndex.addField(f);
  }

  @Override
  public void addField(final Field f, final String altName) {
    fieldIndex.addField(f, altName);

  }

  @Override
  public void removeField(final int i) {
    fieldIndex.removeField(i);
  }

  @Override
  public void clearFieldList() {
    fieldIndex.clearFieldList();
  }

  @Override
  public String getOrder(final String name) {
    return fieldIndex.getOrder(name);
  }

  @Override
  public void setAltName(final String name, final String altName) {
    fieldIndex.setAltName(name, altName);
  }

  @Override
  public String getAltName(final String name) {
    return fieldIndex.getAltName(name);
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
