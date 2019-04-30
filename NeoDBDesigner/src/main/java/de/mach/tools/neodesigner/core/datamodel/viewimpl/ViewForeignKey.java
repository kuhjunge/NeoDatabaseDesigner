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

package de.mach.tools.neodesigner.core.datamodel.viewimpl;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;


/** Implementation von Fremdschlüssel für den View.
 *
 * @author Chris Deter */
public class ViewForeignKey extends ViewNodeImpl<ForeignKey> implements ForeignKey {
  private ViewTable refTable = null;
  private ViewIndex fieldIndex = null;
  private final StringProperty refTableName = new SimpleStringProperty("");
  private boolean modifiedRel = false;

  /** Konstruktor.
   *
   * @param i der ForeignKey der Importiert werden soll
   * @param nodeOf die Tabelle dem der neue FK zugeordnet werden soll */
  public ViewForeignKey(final ForeignKey i, final ViewTable nodeOf) {
    this(i, nodeOf, new ViewTable(i.getRefTable(), false));
  }

  ViewForeignKey(final ForeignKey i, final ViewTable nodeOf, final ViewTable refTbl) {
    super(new ForeignKeyImpl(i.getName(), nodeOf), nodeOf);
    fieldIndex = new ViewIndex(i.getIndex(), nodeOf);
    getNode().setIndex(fieldIndex);
    refTable = refTbl;
    getNode().setRefTable(refTable);
    refTableName.set(refTable.getName());
  }

  /** Konstruktor.
   *
   * @param name Name des FKs
   * @param nodeOf ViewTable die dieser FK zugeordnet ist */
  public ViewForeignKey(final String name, final ViewIndex i, final ViewTable nodeOf) {
    super(new ForeignKeyImpl(name, nodeOf), nodeOf);
    setIndex(i);
    modifiedRel = false;
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof ForeignKey)) {
      return false;
    }
    final ForeignKey other = (ForeignKey) o;
    return getName().equals(other.getName());
  }

  public StringProperty fieldListStringProperty() {
    return fieldIndex.fieldListStringProperty();
  }

  @Override
  public String getAltName(final String name) {
    return getNode().getAltName(name);
  }

  @Override
  public Field getFieldOfTableByOrder(final Field refTblField) {
    return getNode().getFieldOfTableByOrder(refTblField);
  }

  @Override
  public Index getIndex() {
    return getVIndex();
  }

  @Override
  public String getNameFromAltName(final String fieldname) {
    return getNode().getNameFromAltName(fieldname);
  }

  @Override
  public Table getRefTable() {
    return getVRefTable();
  }

  /** Property für die Tabelle in der GUI.
   *
   * @return TableRefName als StringProperty */
  public StringProperty getRefTableName() {
    return refTableName;
  }

  public ViewIndex getVIndex() {
    return fieldIndex;
  }

  /** Referenztabelle als ViewTable Object (notwendig für GUI).
   *
   * @return RefTable als ViewTable */
  public ViewTable getVRefTable() {
    return refTable;
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  /** View Methode um zu erkennen ob der FK verändert wurde.
   *
   * @return True wenn eine Änderung aufgetreten ist */
  public boolean isModifiedRel() {
    return modifiedRel;
  }

  @Override
  public void saved() {
    super.saved();
    modifiedRel = false;
  }

  @Override
  public void setFkOrder(final Field f, final int order) {
    getNode().setFkOrder(f, order);
  }

  @Override
  public void setIndex(final Index i) {
    fieldIndex = new ViewIndex(i, (ViewTable) getTable());
    getNode().setIndex(i);
    modifiedRel = true;
  }

  /** setzt die Modified Eigenschaft auf True, die besagt, ob die Felder des FK sich geändert haben. */
  public void setModifiedRel() {
    setModified();
    modifiedRel = true;
  }

  @Override
  public void setRefTable(final Table refTbl) {
    refTableName.set(refTbl.getName());
    refTable = new ViewTable(refTbl, false);
    getNode().setRefTable(refTable);
    modifiedRel = true;
  }
}
