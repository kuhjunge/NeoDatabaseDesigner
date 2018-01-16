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

import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Anpassung der NodeImpl für den View.
 *
 * @author Chris Deter
 *
 */
public abstract class ViewNodeImpl<T extends Node> implements Node {
  private final StringProperty nameProp = new SimpleStringProperty("");
  private final StringProperty commentProp = new SimpleStringProperty("");
  private boolean modifiedName = false;
  private boolean modifiedComment = false;
  private boolean isCreated = false;
  private String oldName = "";
  private final T data;
  private final BooleanProperty modified = new SimpleBooleanProperty(false);

  /**
   * View Methode um zu erkennen ob die Eigenschaft verändert wurde.
   *
   * @return True wenn eine Änderung aufgetreten ist
   */
  public boolean isModifiedName() {
    return modifiedName;
  }

  /**
   * View Methode um zu erkennen ob die Eigenschaft verändert wurde.
   *
   * @return True wenn eine Änderung aufgetreten ist
   */
  public boolean isModifiedComment() {
    return modifiedComment;
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
   * Booleanproperty für Modified.
   *
   * @return modified
   */
  public BooleanProperty modified() {
    return modified;
  }

  /**
   * Setzt dieses und das übergeordnete Objekt als Modified.
   */
  public void setModified() {
    modified.set(true);
    if (getTable() instanceof ViewNodeImpl) {
      final ViewTable vni = (ViewTable) getTable();
      vni.setModified();
    }
  }

  /**
   * Setzt das Node als Neu erstellt.
   */
  public void setAsNewCreated() {
    isCreated = true;
    setModified();
  }

  /**
   * entfernt alle Marker, die Änderungen aufzeigen sollen.
   */
  public void saved() {
    modifiedName = false;
    isCreated = false;
    oldName = data.getName();
    modified.set(false);
    modifiedComment = false;
  }

  /**
   * Grundgerüst für alle View Elemente.
   *
   * @param n
   *          das Node
   */
  ViewNodeImpl(final T n) {
    data = n;
    commentProp.set(n.getComment());
    nameProp.set(data.getName());
    oldName = data.getName();
    // Alle Änderungen am Namen auch ins Ursprungsobjekt übertragen
    nameProp.addListener((obs, oldValue, newValue) -> setName(newValue));
    commentProp.addListener((obs, oldValue, newValue) -> setComment(newValue));
  }

  /**
   * Property Eigenschaft für die GUI.
   *
   * @return StringProperty name
   */
  public StringProperty nameProperty() {
    return nameProp;
  }

  /**
   * Property Eigenschaft für die GUI.
   *
   * @return StringProperty comment
   */
  public StringProperty commentProperty() {
    return commentProp;
  }

  /**
   * Getter Name.
   *
   * @return Name des Nodes
   */
  @Override
  public String getName() {
    return data.getName();
  }

  /**
   * Getter Comment.
   *
   * @return Kommentar des Nodes
   */
  @Override
  public String getComment() {
    return data.getComment();
  }

  /**
   * Getter oldName.
   *
   * @return Ursprünglicher Name des Nodes
   */
  public String getOldName() {
    return oldName;
  }

  /**
   * Setter Name.
   *
   * @param n
   *          Name
   */
  @Override
  public void setName(final String n) {
    data.setName(n);
    if (!nameProp.get().equals(n)) {
      nameProp.set(data.getName());
    }
    modifiedName = true;
    setModified();
  }

  /**
   * Setter Comment.
   *
   * @param c
   *          Comment
   */
  @Override
  public void setComment(final String c) {
    data.setComment(c);
    if (!commentProp.get().equals(c)) {
      commentProp.set(data.getComment());
    }
    modifiedComment = true;
    setModified();
  }

  /**
   * Getter NodeOf.
   *
   * @return NodeOf
   */
  @Override
  public Table getTable() {
    return data.getTable();
  }

  @Override
  public String toString() {
    return data.getName();
  }

  /**
   * getter Node (Interne Datenhaltung im Node).
   *
   * @return Node
   */
  public T getNode() {
    return data;
  }

  @Override
  public int compareTo(final Node o) {
    return getName().compareToIgnoreCase(o.getName());
  }
}
