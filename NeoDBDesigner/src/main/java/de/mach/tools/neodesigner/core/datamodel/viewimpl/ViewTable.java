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

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/**
 * Implementation von Table für den View.
 *
 * @author Chris Deter
 *
 */
public class ViewTable extends ViewNodeImpl<Table> implements Table {
  private final ObservableList<ViewField> dataFields = FXCollections.observableArrayList();
  private ViewIndex xpk;
  private final ObservableList<ViewForeignKey> foreignKeys = FXCollections.observableArrayList();
  private final ObservableList<ViewIndex> indizes = FXCollections.observableArrayList();
  private final ObservableList<ViewNodeImpl<?>> delete = FXCollections.observableArrayList();

  public ObservableList<ViewField> getDataFieldsRaw() {
    return dataFields;
  }

  public ObservableList<ViewIndex> getIndizesRaw() {
    return indizes;
  }

  public ObservableList<ViewForeignKey> getForeignKeysRaw() {
    return foreignKeys;
  }

  public ObservableList<ViewNodeImpl<?>> getDeleteRaw() {
    return delete;
  }

  /**
   * Konstruktor.
   *
   * @param n
   *          Name der neuen Tabelle
   */
  public ViewTable(final String n) {
    super(new TableImpl(n));
    final ViewIndex t = new ViewIndex(Strings.INDEXTYPE_XPK + n, this);
    t.setAsNewCreated();
    xpk = t;
    setListener();
  }

  /**
   * Konstruktor für Tabellenimport.
   *
   * @param t
   *          Das zu importierende Tabellenobjekt
   */
  public ViewTable(final Table t) {
    super(new TableImpl(t.getName()));
    getNode().setCategory(t.getCategory());
    getNode().setComment(t.getComment());
    for (final Field f : t.getFields()) {
      dataFields.add(new ViewField(f, this));
    }
    for (final Index i : t.getIndizies()) {
      indizes.add(new ViewIndex(i, this));
    }
    xpk = new ViewIndex(t.getXpk(), this);
    for (final ForeignKey i : t.getForeignKeys()) {
      foreignKeys.add(new ViewForeignKey(i, this));
    }
    getNode().getRefForeignKeys().addAll(t.getRefForeignKeys());
    setListener();
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
        for (final E f : c.getAddedSubList()) {
          targetList.add(f);
        }
      }
      if (c.wasRemoved()) {
        for (final E f : c.getRemoved()) {
          targetList.remove(f);
        }
      }
    }
  }

  @Override
  public String getCategory() {
    return getNode().getCategory();
  }

  @Override
  public List<Field> getFields() {
    final List<Field> l = new ArrayList<>();
    l.addAll(dataFields);
    return l;
  }

  /**
   * Gibt die Field Objekte als ViewField zurück.
   *
   * @return Data la ViewField
   */
  public List<ViewField> getVData() {
    return dataFields;
  }

  @Override
  public Index getXpk() {
    return xpk;
  }

  @Override
  public void setXpk(final Index lxpk) {
    xpk = new ViewIndex(lxpk, this);
  }

  @Override
  public List<ForeignKey> getForeignKeys() {
    final List<ForeignKey> l = new ArrayList<>();
    l.addAll(foreignKeys);
    return l;
  }

  @Override
  public List<Index> getIndizies() {
    final List<Index> l = new ArrayList<>();
    l.addAll(indizes);
    return l;
  }

  @Override
  public void addField(final Field f) {
    if (dataFields.contains(f)) {
      dataFields.remove(f);
    }
    dataFields.add(new ViewField(f, this));
  }

  @Override
  public void setCategory(final String kategory) {
    getNode().setCategory(kategory);
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

  @Override
  public List<ForeignKey> getRefForeignKeys() {
    return getNode().getRefForeignKeys();
  }

  @Override
  public Optional<Field> getField(final String fieldName) {
    return getNode().getField(fieldName);
  }

  @Override
  public void deleteField(final String fieldName) {
    final Field f = new ViewField(new FieldImpl(fieldName), this);
    if (dataFields.contains(f)) {
      dataFields.remove(dataFields.indexOf(f));
    }
  }
}
