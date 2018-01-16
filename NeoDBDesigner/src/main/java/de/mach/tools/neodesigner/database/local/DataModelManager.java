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

package de.mach.tools.neodesigner.database.local;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.Save;
import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Der interne Datenmodel Manager.
 *
 * @author Chris Deter
 *
 */
public class DataModelManager extends Observable implements Save {
  private static final Logger LOG = Logger.getLogger(DataModelManager.class.getName());
  private final List<Table> tables = new ArrayList<>();

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

  /**
   * Gibt zurück ob das Datenmodell leer ist.
   *
   * @return True wenn leer
   */
  public boolean isEmpty() {
    return tables.isEmpty();
  }

  /**
   * Fügt alle Tabellen dem Datenmodell hinzu.
   *
   * @param allTables
   *          Eine Liste mit allen Tabellen
   */
  public void addAll(final List<Table> allTables) {
    if (allTables != null) {
      tables.clear();
      tables.addAll(allTables);
      setDataChanged();
    }
  }

  @Override
  public List<Table> getTables() {
    final List<Table> t = new ArrayList<>();
    t.addAll(tables);
    return t;
  }

  /**
   * löscht alle Tabellen aus dem Datenmodell.
   */
  public void clear() {
    tables.clear();
  }

  /**
   * gibt eine bestimmte Tabelle zurück.
   */
  @Override
  public Optional<Table> getTable(final String name) {
    Optional<Table> table = Optional.empty();
    final int i = tables.indexOf(new TableImpl(name));
    if (i >= 0) {
      table = Optional.ofNullable(tables.get(i));
    }
    return table;
  }

  /**
   * direkte Zugriffsfunktion, die Nullpointer erzeugt, wenn ein Fehlerhafter
   * Zugriff auf das Datenmodell erfolgt. (Sollte nicht möglich sein!)
   *
   * @param name
   *          der tabelle
   * @return die Tabelle (oder NULL)
   */
  private Table getRawTable(final String name) {
    final Optional<Table> table = getTable(name);
    if (table.isPresent()) {
      return table.get();
    } else {
      DataModelManager.LOG.log(Level.SEVERE, Strings.LOG_TABLEERROR);
      return new TableImpl(name);
    }
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {
    final Table t = getRawTable(tableName);
    t.setName(newName);
    t.getXpk().setName(xpkName);
    setDataChanged();
  }

  @Override
  public void changeNodeNameFromTable(final String nodeName, final String tableName, final String newNodeName) {
    final Node n = getNodeInTable(nodeName, tableName);
    if (n != null) {
      n.setName(newNodeName);
    }
    setDataChanged();
  }

  @Override
  public void changePrimFieldNameRelation(final String tableName, final String newNodeName, final String oldNodeName) {
    final Table t = getRawTable(tableName);
    for (final ForeignKey fk : t.getRefForeignKeys()) {
      fk.getIndex().setAltName(fk.getIndex().getNameFromAltName(oldNodeName), newNodeName);
    }
  }

  @Override
  public void changeTableCategory(final String tableName, final String newKategory) {
    getRawTable(tableName).setCategory(newKategory);
  }

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean value) {
    final Field f = getField(nodeName, tableName);
    if (f != null) {
      f.setRequired(value);
    }
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
  public void changeFieldIsPartOfPrim(final String nodeName, final String tableName, final Boolean partOfPrimaryKey) {
    final Table t = getRawTable(tableName);
    final Field f = getField(nodeName, tableName);
    final int i = t.getXpk().getFieldList().indexOf(f);
    if (f != null) {
      if (i < 0 && partOfPrimaryKey) {
        t.getXpk().addField(f);
        // TODO: Testfall hierfür bauen
        f.setPartOfPrimaryKey(true);
      } else if (i >= 0 && !partOfPrimaryKey) {
        t.getXpk().removeField(i);
        f.setPartOfPrimaryKey(false);
      }
      setDataChanged();
    }
  }

  @Override
  public void insertNewTable(final Table t) {
    tables.add(t);
    setDataChanged();
  }

  @Override
  public void insertNewField(final Field f) {
    getRawTable(f.getTable().getName()).addField(f);
    setDataChanged();
  }

  @Override
  public void insertNewIndex(final Index i) {
    getRawTable(i.getTable().getName()).getIndizies().add(i);
    setDataChanged();
  }

  @Override
  public void insertNewForeignKey(final ForeignKey i) {
    getRawTable(i.getTable().getName()).getForeignKeys().add(i);
    setDataChanged();
  }

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
      final String indexName) {
    final Table t = getRawTable(tableName);
    final Table rt = getRawTable(refTableName);
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(new IndexImpl(indexName, t)));
    final ForeignKey fk = t.getForeignKeys().get(t.getForeignKeys().indexOf(new ForeignKeyImpl(foreignKeyName, t)));
    fk.setRefTable(rt);
    fk.setIndex(i);
    setDataChanged();
  }

  @Override
  public void changeDataFields(final Index index) {
    final Table t = getRawTable(index.getTable().getName());
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(index));
    final List<Field> lf = index.getFieldList();
    i.clearFieldList();
    for (final Field f : lf) {
      final String str = index.getAltName(f.getName());
      final Optional<Field> field = t.getField(f.getName());
      if (field.isPresent()) {
        if (!Strings.EMPTYSTRING.equals(str)) {
          i.addField(field.get(), str);
        } else {
          i.addField(field.get());
        }
      }
    }
    setDataChanged();
  }

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean value) {
    final Table t = getRawTable(tableName);
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(new IndexImpl(nodeName, t)));
    i.setUnique(value);
  }

  @Override
  public void deleteNode(final Node node) {
    Table t;
    if (node instanceof Field) {
      t = getRawTable(node.getTable().getName());
      deleteField(t.getName(), node.getName());
    } else if (node instanceof ForeignKey) {
      t = getRawTable(node.getTable().getName());
      deleteForeignKey(t.getName(), node.getName());
    } else if (node instanceof Index) {
      t = getRawTable(node.getTable().getName());
      deleteIndex(t.getName(), node.getName());
    } else if (node instanceof Table) {
      deleteTable(node.getName());
    }
    setDataChanged();
  }

  /**
   * löscht eine Tabelle.
   *
   * @param name
   *          der Tabelle
   */
  private void deleteTable(final String name) {
    tables.remove(tables.indexOf(getRawTable(name)));
  }

  /**
   * löscht ein Feld.
   *
   * @param tableName
   *          Name der Tabelle
   * @param fieldName
   *          Name des Feldes
   * @return das gelöschte Feld
   */
  private Field deleteField(final String tableName, final String fieldName) {
    final Table t = getRawTable(tableName);
    final Optional<Field> f = t.getField(fieldName);
    if (f.isPresent()) {
      t.deleteField(fieldName);
    }
    final Field searchDummy = new FieldImpl(fieldName);
    int index = 0;
    for (final Index i : t.getIndizies()) {
      index = i.getFieldList().indexOf(searchDummy);
      if (index >= 0) {
        i.removeField(index);
      }
    }
    return f.isPresent() ? f.get() : null;
  }

  /**
   * Löscht einen Index.
   *
   * @param tableName
   *          Name der Tabelle
   * @param indexName
   *          Name des Indexes
   * @return der gelöschte Index
   */
  private Index deleteIndex(final String tableName, final String indexName) {
    final Table t = getRawTable(tableName);
    Index f = null;
    final int index = t.getIndizies().indexOf(new IndexImpl(indexName, t));
    if (index >= 0) {
      f = t.getIndizies().remove(index);
    }
    return f;
  }

  /**
   * Löscht eine Fremdschlüssel.
   *
   * @param tableName
   *          Name der Tabelle
   * @param fkName
   *          Name des Fremdschlüssels
   * @return den gelöschten Fremdschlüssel
   */
  private ForeignKey deleteForeignKey(final String tableName, final String fkName) {
    final Table t = getRawTable(tableName);
    ForeignKey f = null;
    final int index = t.getForeignKeys().indexOf(new ForeignKeyImpl(fkName, t));
    if (index >= 0) {
      f = t.getForeignKeys().remove(index);
    }
    return f;
  }

  /**
   * Gibt ein Element einer Tabelle zurück.
   *
   * @param nodeName
   *          Name des Nodes
   * @param tableName
   *          Name der Tabelle
   * @return den Node
   */
  private Node getNodeInTable(final String nodeName, final String tableName) {
    final Table t = getRawTable(tableName);
    Node ret = null;
    final Optional<Field> field = t.getField(nodeName);
    if (field.isPresent()) {
      ret = field.get();
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

  /**
   * Läd ein Feld aus einer Tabelle.
   *
   * @param fieldName
   *          Name des Feldes
   * @param tableName
   *          Name der Tabelle
   * @return das Feld
   */
  private Field getField(final String fieldName, final String tableName) {
    final Table t = getRawTable(tableName);
    final Optional<Field> opFi = t.getField(fieldName);
    if (opFi.isPresent()) {
      return opFi.get();
    }
    return null;
  }

  /**
   * Benachrichtigt den Observer, dass sich etwas im Datenmodell geändert hat.
   */
  private void setDataChanged() {
    setChanged();
    notifyObservers();
  }

  @Override
  public List<String> getFieldNameCase(final String name) {
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

  @Override
  public void saveComment(final String name, final String tableName, final String comment) {
    final Field f = getField(name, tableName);
    if (f != null) {
      f.setComment(comment);
    } else {
      DataModelManager.LOG.log(Level.SEVERE, Strings.LOG_TABLEERROR);
    }
  }

  @Override
  public void saveComment(final String tableName, final String comment) {
    getRawTable(tableName).setComment(comment);
  }
}
