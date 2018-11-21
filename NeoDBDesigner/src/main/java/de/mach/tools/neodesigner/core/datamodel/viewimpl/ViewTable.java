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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;


/** Implementation von Table für den View.
 *
 * @author Chris Deter */
public class ViewTable extends ViewNodeImpl<Table> implements Table {
  private final ObservableList<ViewField> dataFields = FXCollections.observableArrayList();
  private ViewIndex xpk;
  private final ObservableList<ViewForeignKey> foreignKeys = FXCollections.observableArrayList();
  private final ObservableList<ViewIndex> indizes = FXCollections.observableArrayList();
  private final ObservableList<ViewNodeImpl<?>> delete = FXCollections.observableArrayList();

  /** Konstruktor.
   *
   * @param n Name der neuen Tabelle */
  public ViewTable(final String n) {
    super(new TableImpl(n), null);
    final ViewIndex t = new ViewIndex(Strings.INDEXTYPE_XPK + n, this);
    t.setAsNewCreated();
    xpk = t;
    setListener();
  }

  /** Konstruktor für Tabellenimport.
   *
   * @param t Das zu importierende Tabellenobjekt
   * @param fullCopy (Kopiert FKs nicht mit in neue ViewTable) */
  public ViewTable(final Table t, final boolean fullCopy) {
    super(new TableImpl(t.getName()), null);
    setCategory(t.getCategory());
    setComment(t.getComment());
    for (final Field f : t.getFields()) {
      addField(new ViewField(f, this));
    }
    for (final Index i : t.getIndizies()) {
      addIndex(new ViewIndex(i, this));
    }
    setXpk(t.getXpk());
    // FKs mitkopieren oder auslassen
    if (fullCopy) {
      for (final ForeignKey i : t.getForeignKeys()) {
        addForeignKey(new ViewForeignKey(i, this), false);
      }
      for (final ForeignKey i : t.getRefForeignKeys()) {
        addForeignKey(new ViewForeignKey(i, new ViewTable(i.getTable(), false), this), true);
      }
    }
    saved(); // nur kopiert und nicht modifiziert
    setListener();
  }

  @Override
  public void addField(final Field f) {
    if (dataFields.contains(f)) {
      dataFields.remove(f);
    }
    dataFields.add(new ViewField(f, this));
    getNode().addField(f);
  }

  @Override
  public void addForeignKey(final ForeignKey fk, final boolean refkey) {
    if (refkey) {
      getNode().addForeignKey(fk, true);
    }
    else {
      if (foreignKeys.contains(fk)) {
        foreignKeys.remove(fk);
      }
      foreignKeys.add(new ViewForeignKey(fk, this));
    }
    getNode().addForeignKey(fk, refkey);
  }

  @Override
  public void addIndex(final Index i) {
    if (indizes.contains(i)) {
      indizes.remove(i);
    }
    indizes.add(new ViewIndex(i, this));
    getNode().addIndex(i);
  }

  @Override
  public void deleteField(final String fieldName) {
    final Field f = new ViewField(new FieldImpl(fieldName), this);
    if (dataFields.contains(f)) {
      dataFields.remove(dataFields.indexOf(f));
    }
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
  public String getCategory() {
    return getNode().getCategory();
  }

  public ObservableList<ViewField> getDataFieldsRaw() {
    return dataFields;
  }

  public ObservableList<ViewNodeImpl<?>> getDeleteRaw() {
    return delete;
  }

  @Override
  public Optional<Field> getField(final String fieldName) {
    return getNode().getField(fieldName);
  }

  @Override
  public List<Field> getFields() {
    return new ArrayList<>(dataFields);
  }

  /** Prüft ob die angegebenen Nummer bereits in der Tabelle vergeben ist und erhöht die aktuelle Nummer ggf.
   *
   * @param actualNumber Die zu prüfenden Nummer
   * @return die ggf erhöhte laufende Nummer des Fremdschlüssels */
  public int getForeignKeyNumber(final int actualNumber) {
    int maxNum = actualNumber;
    int number;
    for (final ForeignKey fk : getForeignKeys()) {
      number = 0;
      final String posNum = fk.getName().substring(2, fk.getName().length());
      final boolean isNumber = Pattern.matches("[0-9]+", posNum);
      if (isNumber) {
        number = Integer.parseInt(posNum);
      }
      if (number > maxNum) {
        maxNum = number;
      }
    }
    return maxNum;
  }

  @Override
  public List<ForeignKey> getForeignKeys() {
    return new ArrayList<>(foreignKeys);
  }

  public ObservableList<ViewForeignKey> getForeignKeysRaw() {
    return foreignKeys;
  }

  public ObservableList<ViewIndex> getIndizesRaw() {
    return indizes;
  }

  @Override
  public List<Index> getIndizies() {
    return new ArrayList<>(indizes);
  }

  @Override
  public List<ForeignKey> getRefForeignKeys() {
    return getNode().getRefForeignKeys();
  }

  @Override
  public Index getXpk() {
    return xpk;
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public void setCategory(final String kategory) {
    getNode().setCategory(kategory);
  }

  private void setListener() {
    foreignKeys
        .addListener((ListChangeListener<? super ViewForeignKey>) c -> setListener(c, getNode().getForeignKeys()));
    indizes.addListener((ListChangeListener<? super ViewIndex>) c -> setListener(c, getNode().getIndizies()));
    dataFields.addListener((ListChangeListener<? super ViewField>) c -> setListener(c, getNode().getFields()));
  }

  private <E extends Node> void setListener(final Change<? extends E> c, final List<E> targetList) {
    while (c.next()) {
      if (c.wasAdded()) {
        targetList.addAll(c.getAddedSubList());
      }
      if (c.wasRemoved()) {
        for (final E f : c.getRemoved()) {
          targetList.remove(f);
        }
      }
    }
  }

  @Override
  public void setXpk(final Index lxpk) {
    xpk = new ViewIndex(lxpk, this);
    getNode().setXpk(xpk);
  }

  @Override
  public String toString() {
    return getName();
  }
}
