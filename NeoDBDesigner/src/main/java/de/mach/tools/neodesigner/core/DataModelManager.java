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

package de.mach.tools.neodesigner.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;


/** Der interne Datenmodel Manager.
 *
 * @author Chris Deter */
public class DataModelManager implements Save {
  private static final Logger LOG = Logger.getLogger(DataModelManager.class.getName());
  private final List<Table> tables = new ArrayList<>();
  private List<DatamodelListener> listeners = new ArrayList<>();

  public void addListener(DatamodelListener toAdd) {
    listeners.add(toAdd);
  }

  void clearListener() {
    if (!listeners.isEmpty()) {
      listeners.clear();
    }
  }

  /** Fügt alle Tabellen dem Datenmodell hinzu.
   *
   * @param allTables Eine Liste mit allen Tabellen */
  void addAll(final List<Table> allTables) {
    if (allTables != null) {
      tables.clear();
      tables.addAll(allTables);
      dataChanged();
    }
  }

  @Override
  public void changeDataFields(final Index index) {
    final Table t = getRawTable(index.getTable().getName());
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(index));
    final List<Field> lf = index.getFieldList();
    i.clearFieldList();
    for (final Field f : lf) {
      final Field field = t.getField(f.getName());
      i.addField(field);
    }
    dataChanged();
  }

  @Override
  public void changeFieldDomain(final String nodeName, final String tableName, final DomainId type, final int length) {
    final Field f = getField(nodeName, tableName);
    if (f != null) {
      f.setDomain(type);
      f.setDomainLength(length);
    }
  }

  @Override
  public void changeFieldIsPartOfPrim(final String tableName, final String xpkName, final String nodeName,
                                      final Boolean partOfPrimaryKey, final int size) {
    final Table t = getRawTable(tableName);
    final Field f = getField(nodeName, tableName);
    final int i = t.getXpk().getFieldList().indexOf(f);
    if (f != null) {
      if (i < 0 && partOfPrimaryKey) {
        t.getXpk().addField(f);
      }
      else if (i >= 0 && !partOfPrimaryKey) {
        t.getXpk().removeField(i);
      }
      dataChanged();
    }
  }

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean value) {
    final Field f = getField(nodeName, tableName);
    if (f != null) {
      f.setRequired(value);
    }
  }

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
                                final String indexName, final String columnName, final int order) {
    final Table t = getRawTable(tableName);
    final Table rt = getRawTable(refTableName);
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(new IndexImpl(indexName, t)));
    final ForeignKey fk = t.getForeignKeys().get(t.getForeignKeys().indexOf(new ForeignKeyImpl(foreignKeyName, t)));
    fk.setRefTable(rt);
    fk.setIndex(i);
    fk.getIndex().setOrder(columnName, order, true);
    dataChanged();
  }

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean value) {
    final Table t = getRawTable(tableName);
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(new IndexImpl(nodeName, t)));
    i.setUnique(value);
  }

  @Override
  public void changeNodeNameFromTable(final String nodeName, final String tableName, final String newNodeName,
                                      final String type) {
    final Node n = getNodeInTable(nodeName, tableName);
    if (n != null) {
      n.setName(newNodeName);
    }
    dataChanged();
  }

  @Override
  public void changeTableCategory(final String tableName, final String newKategory) {
    getRawTable(tableName).setCategory(newKategory);
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {
    final Table t = getRawTable(tableName);
    t.setName(newName);
    t.getXpk().setName(xpkName);
    dataChanged();
  }

  /** löscht alle Tabellen aus dem Datenmodell. */
  public void clear() {
    tables.clear();
  }

  /** löscht ein Feld.
   *
   * @param tableName Name der Tabelle
   * @param fieldName Name des Feldes
   * @return das gelöschte Feld */
  private Field deleteField(final String tableName, final String fieldName) {
    final Table t = getRawTable(tableName);
    final Field f = t.getField(fieldName);
    if (f != null) {
      t.deleteField(fieldName);
    }
    final Field searchDummy = new FieldImpl(fieldName);
    int index;
    for (final Index i : t.getIndizies()) {
      index = i.getFieldList().indexOf(searchDummy);
      if (index >= 0) {
        i.removeField(index);
      }
    }
    return f;
  }

  /** Löscht eine Fremdschlüssel.
   *
   * @param tableName Name der Tabelle
   * @param fkName Name des Fremdschlüssels
   * @return den gelöschten Fremdschlüssel */
  private ForeignKey deleteForeignKey(final String tableName, final String fkName) {
    final Table t = getRawTable(tableName);
    ForeignKey f = null;
    final int index = t.getForeignKeys().indexOf(new ForeignKeyImpl(fkName, t));
    if (index >= 0) {
      f = t.getForeignKeys().remove(index);
    }
    return f;
  }

  /** Löscht einen Index.
   *
   * @param tableName Name der Tabelle
   * @param indexName Name des Indexes
   * @return der gelöschte Index */
  private Index deleteIndex(final String tableName, final String indexName) {
    final Table t = getRawTable(tableName);
    Index f = null;
    final int index = t.getIndizies().indexOf(new IndexImpl(indexName, t));
    if (index >= 0) {
      f = t.getIndizies().remove(index);
    }
    return f;
  }

  @Override
  public void deleteNode(final Node node) {
    Table t;
    if (node instanceof Field) {
      t = getRawTable(node.getTable().getName());
      deleteField(t.getName(), node.getName());
    }
    else if (node instanceof ForeignKey) {
      t = getRawTable(node.getTable().getName());
      deleteForeignKey(t.getName(), node.getName());
    }
    else if (node instanceof Index) {
      t = getRawTable(node.getTable().getName());
      deleteIndex(t.getName(), node.getName());
    }
    else if (node instanceof Table) {
      deleteTable(node.getName());
    }
    dataChanged();
  }

  /** löscht eine Tabelle.
   *
   * @param name der Tabelle */
  private void deleteTable(final String name) {
    tables.remove(getRawTable(name));
  }

  /** Läd ein Feld aus einer Tabelle.
   *
   * @param fieldName Name des Feldes
   * @param tableName Name der Tabelle
   * @return das Feld */
  private Field getField(final String fieldName, final String tableName) {
    return getRawTable(tableName).getField(fieldName);
  }

  @Override
  public List<String> getFieldNameWithoutCase(final String name) {
    final List<String> list = new ArrayList<>();
    for (final Table t : tables) {
      for (final Field f : t.getFields()) {
        if (f.getName().equalsIgnoreCase(name) && !list.contains(f.getName())) {
          list.add(f.getName());
        }
      }
    }
    return list;
  }

  /** Gibt ein Element einer Tabelle zurück.
   *
   * @param nodeName Name des Nodes
   * @param tableName Name der Tabelle
   * @return den Node */
  private Node getNodeInTable(final String nodeName, final String tableName) {
    final Table t = getRawTable(tableName);
    Node ret = null;
    final Field field = t.getField(nodeName);
    if (field != null) {
      ret = field;
    }
    int index = t.getIndizies().indexOf(new IndexImpl(nodeName, t));
    if (index >= 0) {
      ret = t.getIndizies().get(index);
    }
    index = t.getForeignKeys().indexOf(new ForeignKeyImpl(nodeName, t));
    if (index >= 0) {
      ret = t.getForeignKeys().get(index);
    }
    return ret;
  }

  /** direkte Zugriffsfunktion, die Nullpointer erzeugt, wenn ein Fehlerhafter Zugriff auf das Datenmodell erfolgt.
   * (Sollte nicht möglich sein!)
   *
   * @param name der tabelle
   * @return die Tabelle (oder NULL) */
  private Table getRawTable(final String name) {
    final Optional<Table> table = getTable(name);
    if (table.isPresent()) {
      return table.get();
    }
    else {
      DataModelManager.LOG.log(Level.SEVERE, Strings.LOG_TABLEERROR);
      return new TableImpl(name);
    }
  }

  /** gibt eine bestimmte Tabelle zurück. */
  @Override
  public Optional<Table> getTable(final String name) {
    Optional<Table> table = Optional.empty();
    final int i = tables.indexOf(new TableImpl(name));
    if (i >= 0) {
      table = Optional.ofNullable(tables.get(i));
    }
    return table;
  }

  @Override
  public List<Table> getTables() {
    return new ArrayList<>(tables);
  }

  @Override
  public boolean hasTable(final String name) {
    boolean exists = false;
    for (final Table t : tables) {
      if (t.getName().equalsIgnoreCase(name)) {
        exists = true;
      }
    }
    return exists;
  }

  @Override
  public void insertNewField(final Field f) {
    getRawTable(f.getTable().getName()).addField(f);
    dataChanged();
  }

  @Override
  public void insertNewForeignKey(final ForeignKey i) {
    getRawTable(i.getTable().getName()).addForeignKey(i, false);
    getRawTable(i.getRefTable().getName()).addForeignKey(i, true);
    dataChanged();
  }

  @Override
  public void insertNewIndex(final Index i) {
    getRawTable(i.getTable().getName()).getIndizies().add(i);
    dataChanged();
  }

  @Override
  public void insertNewTable(final Table t) {
    tables.add(t);
    dataChanged();
  }

  /** Gibt zurück ob das Datenmodell leer ist.
   *
   * @return True wenn leer */
  public boolean isEmpty() {
    return tables.isEmpty();
  }

  @Override
  public void changeComment(final String tableName, final String comment) {
    getRawTable(tableName).setComment(comment);
  }

  @Override
  public void changeComment(final String name, final String tableName, final String comment) {
    final Field f = getField(name, tableName);
    if (f != null) {
      f.setComment(comment);
    }
    else {
      DataModelManager.LOG.log(Level.SEVERE, Strings.LOG_TABLEERROR);
    }
  }

  @Override
  public void changeOrder(String name, String tableName, int order) {
    final Field f = getField(name, tableName);
    if (f != null) {
      f.setDisplayOrder(order);
    }
    else {
      DataModelManager.LOG.log(Level.SEVERE, Strings.LOG_TABLEERROR);
    }
  }

  @Override
  public void changeXpkOrder(String tableName, Map<Integer, String> newOrderList) {
    Table t = getRawTable(tableName);
    for (Map.Entry<Integer, String> orderset : newOrderList.entrySet()) {
      // Alle zugehörigen Fremdschlüsselfelder ändern
      for (ForeignKey fk : t.getRefForeignKeys()) {
        fk.getIndex().setOrder(fk.getNameFromAltName(orderset.getValue()), orderset.getKey(), false);
        fk.getIndex().setOrder(fk.getNameFromAltName(orderset.getValue()), orderset.getKey(), true);
      }
      // Reihenfolge ist hier wichtig, da sich die FKs auf die PK Reihenfolge beziehen um den Refnamen aufzulösen
      // Primärschlüsselreihenfolge ändern
      t.getXpk().setOrder(orderset.getValue(), orderset.getKey(), false);
    }
  }

  /** Benachrichtigt den Observer, dass sich etwas im Datenmodell geändert hat. */
  private void dataChanged() {
    for (DatamodelListener dl : listeners) {
      dl.trigger();
    }
  }
}
