package de.mach.tools.neodesigner.core.datamodel.viewimpl;

import de.mach.tools.neodesigner.core.datamodel.Node;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Anpassung der NodeImpl für den View.
 *
 * @author Chris Deter
 *
 */
public abstract class ViewNodeImpl {
  private final StringProperty nameProp = new SimpleStringProperty("");
  private boolean modifiedName = false;
  private boolean isCreated = false;
  private String oldName = "";
  private final Node data;

  /**
   * View Methode um zu erkennen ob die Eigenschaft verändert wurde.
   *
   * @return True wenn eine Änderung aufgetreten ist
   */
  public boolean isModifiedName() {
    return modifiedName;
  }

  /**
   * View Methode um zu erkennen ob das Node neu erstellt wurde.
   *
   * @return True wenn das Node neu erstellt ist
   */
  public boolean isNewCreated() {
    return isCreated;
  }

  /**
   * Setzt das Node als Neu erstellt.
   */
  public void setAsNewCreated() {
    isCreated = true;
  }

  /**
   * entfernt alle Marker, die Änderungen aufzeigen sollen.
   */
  public void saved() {
    modifiedName = false;
    isCreated = false;
    oldName = data.getName();
  }

  /**
   * Grundgerüst für alle View Elemente.
   *
   * @param n
   *          das Node
   */
  ViewNodeImpl(final Node n) {
    data = n;
    nameProp.set(data.getName());
    oldName = data.getName();
    // Alle Änderungen am Namen auch ins Ursprungsobjekt übertragen
    nameProp.addListener((obs, oldValue, newValue) -> modifiedName(newValue));
  }

  /**
   * Wird aufgerufen wenn der Name geändert wurde, syncronisiert die Änderung
   * mit dem Internen Node Objekt
   *
   * @param newValue
   */
  private void modifiedName(final String newValue) {
    modifiedName = true;
    data.setName(newValue);
  }

  /**
   * Property Eigenschaft für die GUI
   *
   * @return StringProperty name
   */
  public StringProperty nameProperty() {
    return nameProp;
  }

  /**
   * Getter Name
   *
   * @return Name des Nodes
   */
  public String getName() {
    return data.getName();
  }

  /**
   * Getter oldName
   *
   * @return Ursprünglicher Name des Nodes
   */
  public String getOldName() {
    return oldName;
  }

  /**
   * Setter Name
   *
   * @param n
   *          Name
   */
  public void setName(final String n) {
    data.setName(n);
    nameProp.set(data.getName());
  }

  /**
   * Getter NodeOf
   *
   * @return NodeOf
   */
  public Node getNodeOf() {
    return data.getNodeOf();
  }

  @Override
  public String toString() {
    return data.getName();
  }

  /**
   * getter Node (Interne Datenhaltung im Node)
   *
   * @return Node
   */
  public Node getNode() {
    return data;
  }
}
