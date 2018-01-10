package de.mach.tools.neodesigner.core.datamodel.viewimpl;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Implementation von Fremdschlüssel für den View.
 *
 * @author Chris Deter
 *
 */
public class ViewForeignKey extends ViewIndex implements ForeignKey {
  private ViewTable refTable = null;
  private ViewIndex fieldIndex = null;
  private final StringProperty refTableName = new SimpleStringProperty("");
  private boolean modifiedRel = false;
  private final ForeignKey data;

  /**
   * Konstruktor.
   *
   * @param i
   *          der ForeignKey der Importiert werden soll
   * @param nodeOf
   *          die Tabelle dem der neue FK zugeordnet werden soll
   */
  public ViewForeignKey(final ForeignKey i, final ViewTable nodeOf) {
    super(new ForeignKeyImpl(i.getName(), nodeOf));
    data = (ForeignKey) super.getNode();
    fieldIndex = new ViewIndex(i.getIndex(), nodeOf);
    data.setIndex(fieldIndex);
    refTable = new ViewTable(i.getRefTable().getName());
    data.setRefTable(refTable);
    refTableName.set(refTable.getName());
    fieldListAsString.set(data.getFieldList().toString());
  }

  /**
   * Konstruktor.
   *
   * @param name
   *          Name des FKs
   * @param nodeOf
   *          ViewTable die dieser FK zugeordnet ist
   */
  public ViewForeignKey(final String name, final ViewTable nodeOf) {
    super(new ForeignKeyImpl(name, nodeOf));
    data = (ForeignKey) super.getNode();
    fieldListAsString.set(data.getFieldList().toString());
  }

  @Override
  public Table getRefTable() {
    return refTable;
  }

  /**
   * View Methode um zu erkennen ob der FK verändert wurde.
   *
   * @return True wenn eine Änderung aufgetreten ist
   */
  public boolean isModified() {
    return modifiedRel;
  }

  /**
   * setzt die Modified Eigenschaft auf True, die besagt, ob die Felder des FK
   * sich geändert haben.
   */
  public void setModified() {
    modifiedRel = true;
  }

  /**
   * Referenztabelle als ViewTable Object (notwendig für GUI)
   *
   * @return RefTable als ViewTable
   */
  public ViewTable getVRefTable() {
    return refTable;
  }

  /**
   * Property für die Tabelle in der GUI
   * 
   * @return TableRefName als StringProperty
   */
  public StringProperty getRefTableName() {
    return refTableName;
  }

  @Override
  protected void modifiedFieldList() {
    fieldListAsString.set(data.getFieldList().toString());
    modifiedDatafields = true;
  }

  @Override
  public void setRefTable(final Table refTable) {
    refTableName.set(refTable.getName());
    this.refTable = new ViewTable(refTable);
    modifiedRel = true;
  }

  @Override
  public Index getIndex() {
    return fieldIndex;
  }

  public ViewIndex getVIndex() {
    return fieldIndex;
  }

  @Override
  public void setIndex(final Index i) {
    fieldIndex = new ViewIndex(i, (ViewTable) getNodeOf());
    modifiedRel = true;
  }

  @Override
  public void saved() {
    super.saved();
    modifiedRel = false;
  }

  @Override
  public List<Field> getFieldList() {
    return fieldIndex.getFieldList();
  }

  @Override
  public void addField(final Field f) {
    fieldIndex.addField(f);
    modifiedFieldList();
  }

  @Override
  public void addField(final Field f, final String altName) {
    fieldIndex.addField(f, altName);
    modifiedFieldList();
  }

  @Override
  public void removeField(final int i) {
    fieldIndex.removeField(i);
    modifiedFieldList();
  }

  @Override
  public void clearFieldList() {
    fieldIndex.clearFieldList();
    modifiedFieldList();
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
