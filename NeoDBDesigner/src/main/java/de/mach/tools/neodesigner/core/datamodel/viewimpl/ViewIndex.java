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

package de.mach.tools.neodesigner.core.datamodel.viewimpl;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Implementation von Index für den View.
 *
 * @author Chris Deter
 *
 */
public class ViewIndex extends ViewNodeImpl<Index> implements Index {
  private final BooleanProperty unique = new SimpleBooleanProperty(false);
  private final StringProperty fieldListAsString = new SimpleStringProperty();
  private boolean modifiedUnique = false;
  private boolean modifiedDatafields = false;

  /**
   * Konstruktor.
   *
   * @param name
   *          Name des Index
   * @param nodeOf
   *          Tabelle vom Index
   */
  public ViewIndex(final String name, final ViewTable nodeOf) {
    super(new IndexImpl(name, nodeOf));
    indexPrepWork();
  }

  /**
   * Konstruktor.
   *
   * @param i
   *          Index zum kopieren
   * @param nodeOf
   *          Tabelle vom Index
   */
  public ViewIndex(final Index i, final ViewTable nodeOf) {
    super(new IndexImpl(i.getName(), i.getType(), nodeOf));
    for (final Field f : i.getFieldList()) {
      getNode().addField(nodeOf.getDataFieldsRaw().get(nodeOf.getDataFieldsRaw().indexOf(f)));

    }
    setUnique(i.isUnique());
    unique.addListener((obs, oldValue, newValue) -> modifyUnique());
    for (final Field f : getNode().getFieldList()) {
      getNode().setAltName(f.getName(), i.getAltName(f.getName()));
    }
    nameProperty().addListener((obs, oldValue, newValue) -> modifyName());
    indexPrepWork();
  }

  /**
   * Initialisieren von der Feldliste des Indexes.
   */
  private void indexPrepWork() {
    fieldListAsString.set(getNode().getFieldList().toString());
  }

  /**
   * wird aufgerufen wenn die Feldliste des Indexes verändert wurde.
   */
  private void modifiedFieldList() {
    fieldListAsString.set(getNode().getFieldList().toString());
    modifiedDatafields = true;
    setModified();
  }

  /**
   * View Methode um zu erkennen ob die Eigenschaft verändert wurde.
   *
   * @return True wenn eine Änderung aufgetreten ist
   */
  public boolean isModifiedUnique() {
    return modifiedUnique;
  }

  /**
   * View Methode um zu erkennen ob die Eigenschaft verändert wurde.
   *
   * @return True wenn eine Änderung aufgetreten ist
   */
  public boolean isModifiedDatafields() {
    return modifiedDatafields;
  }

  @Override
  public void saved() {
    super.saved();
    modifiedUnique = false;
    modifiedDatafields = false;
  }

  /**
   * wird aufgerufen wenn der Name des Indexes verändert wurde, syncronisiert die
   * Werte mit dem internen Index Objekt.
   */
  private void modifyName() {
    if (getNode().isUnique() != unique.get()) {
      unique.set(getNode().isUnique());
    }
  }

  /**
   * Wird aufgerufen, wenn die Unique Eigenschaft verändert wird. Syncronisiert
   * die Änderung mit dem internen Index Model
   */
  private void modifyUnique() {
    // Modify muss neu gespeichert werden
    modifiedUnique = true;
    setModified();
    // In den Daten muss das Unique übereinstimmen
    if (getNode().isUnique() != unique.get()) {
      getNode().setUnique(unique.get());
      // den Namen anpassen bei geänderten Unique
      setName(getNode().getName());
      if (getNode().isUnique() != unique.get()) {
        unique.set(getNode().isUnique());
      }
    }
  }

  /**
   * Property für die Anzeige in der GUI.
   *
   * @return BooleanProperty unique
   */
  public BooleanProperty uniqueProperty() {
    return unique;
  }

  /**
   * Funktion zum Herausfinden ob dieser Index einem FK zugeordnet ist.
   *
   * @return true wenn ein fk existiert
   */
  public boolean hasFk() {
    if (getType().equals(Index.Type.XIF)) {
      final List<ForeignKey> lfk = getTable().getForeignKeys().stream().filter(l -> l.getIndex().equals(this))
          .collect(Collectors.toList());
      if (!lfk.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isUnique() {
    return getNode().isUnique();
  }

  @Override
  public void setUnique(final boolean unique) {
    this.unique.set(unique);
  }

  public StringProperty fieldListStringProperty() {
    return fieldListAsString;
  }

  @Override
  public Type getType() {
    return getNode().getType();
  }

  @Override
  public void setType(final Type s) {
    getNode().setType(s);
    unique.set(getNode().isUnique());
  }

  @Override
  public List<Field> getFieldList() {
    return getNode().getFieldList();
  }

  @Override
  public void addField(final Field f) {
    getNode().addField(f, "");
    modifiedFieldList();
  }

  @Override
  public void addField(final Field f, final String altName) {
    getNode().addField(f, altName);
    modifiedFieldList();
  }

  @Override
  public void removeField(final int i) {
    getNode().removeField(i);
    modifiedFieldList();
  }

  @Override
  public void clearFieldList() {
    getNode().clearFieldList();
    modifiedFieldList();
  }

  @Override
  public void setAltName(final String name, final String altName) {
    getNode().setAltName(name, altName);
  }

  @Override
  public String getAltName(final String name) {
    return getNode().getAltName(name);
  }

  @Override
  public String getNameFromAltName(final String name) {
    return getNode().getNameFromAltName(name);
  }

  @Override
  public Integer getOrder(final String name) {
    return getNode().getOrder(name);
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

  @Override
  public void replaceField(final Field field, final String oldFieldName) {
    getNode().replaceField(field, oldFieldName);
    modifiedFieldList();
  }

  @Override
  public void setOrder(final String name, final int order) {
    getNode().setOrder(name, order);
  }
}
