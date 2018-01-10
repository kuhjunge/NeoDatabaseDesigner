package de.mach.tools.neodesigner.core.datamodel.viewimpl;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;

import java.util.List;

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
public class ViewIndex extends ViewNodeImpl implements Index {
  private final BooleanProperty unique = new SimpleBooleanProperty(false);
  protected final StringProperty fieldListAsString = new SimpleStringProperty();
  private boolean modifiedUnique = false;
  protected boolean modifiedDatafields = false;
  private final Index data;

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
    data = (Index) super.getNode();
    indexPrepWork();
  }

  /**
   * Konstruktor für die abgeleiteten Klassen (Fremdschlüssel). Übernimmt im
   * gegensatz der anderen Konstruktoren den Index Schlüssel und keine Kopie von
   * ihm.
   *
   * @param i
   *          Index zum kopieren
   */
  ViewIndex(final Index i) {
    super(i);
    data = (Index) super.getNode();
    setUnique(i.isUnique());
    unique.addListener((obs, oldValue, newValue) -> modifyUnique());
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
    data = (Index) super.getNode();
    for (final Field f : i.getFieldList()) {
      data.addField(nodeOf.dataFields.get(nodeOf.dataFields.indexOf(f)));
    }
    setUnique(i.isUnique());
    unique.addListener((obs, oldValue, newValue) -> modifyUnique());
    for (final Field f : data.getFieldList()) {
      data.setAltName(f.getName(), i.getAltName(f.getName()));
    }
    nameProperty().addListener((obs, oldValue, newValue) -> modifyName());
    indexPrepWork();
  }

  /**
   * Initialisieren von der Feldliste des Indexes
   */
  private void indexPrepWork() {
    fieldListAsString.set(data.getFieldList().toString());
  }

  /**
   * wird aufgerufen wenn die Feldliste des Indexes verändert wurde
   */
  protected void modifiedFieldList() {
    fieldListAsString.set(data.getFieldList().toString());
    modifiedDatafields = true;
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
   * wird aufgerufen wenn der Name des Indexes verändert wurde, syncronisiert
   * die Werte mit dem internen Index Objekt.
   */
  private void modifyName() {
    if (data.isUnique() != unique.get()) {
      unique.set(data.isUnique());
    }
  }

  /**
   * Wird aufgerufen, wenn die Unique Eigenschaft verändert wird. Syncronisiert
   * die Änderung mit dem internen Index Model
   */
  private void modifyUnique() {
    // Modify muss neu gespeichert werden
    modifiedUnique = true;
    // In den Daten muss das Unique übereinstimmen
    if (data.isUnique() != unique.get()) {
      data.setUnique(unique.get());
      // den Namen anpassen bei geänderten Unique
      setName(data.getName());
      if (data.isUnique() != unique.get()) {
        unique.set(data.isUnique());
      }
    }
  }

  /**
   * Property für die Anzeige in der GUI
   * 
   * @return BooleanProperty unique
   */
  public BooleanProperty uniqueProperty() {
    return unique;
  }

  @Override
  public boolean isUnique() {
    return data.isUnique();
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
    return data.getType();
  }

  @Override
  public void setType(final Type s) {
    data.setType(s);
    unique.set(data.isUnique());
  }

  @Override
  public List<Field> getFieldList() {
    return data.getFieldList();
  }

  @Override
  public void addField(final Field f) {
    data.addField(f, "");
    modifiedFieldList();
  }

  @Override
  public void addField(final Field f, final String altName) {
    data.addField(f, altName);
    modifiedFieldList();
  }

  @Override
  public void removeField(final int i) {
    data.removeField(i);
    modifiedFieldList();
  }

  @Override
  public void clearFieldList() {
    data.clearFieldList();
    modifiedFieldList();
  }

  @Override
  public void setAltName(final String name, final String altName) {
    data.setAltName(name, altName);
  }

  @Override
  public String getAltName(final String name) {
    return data.getAltName(name);
  }

  @Override
  public String getOrder(final String name) {
    return data.getOrder(name);
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
