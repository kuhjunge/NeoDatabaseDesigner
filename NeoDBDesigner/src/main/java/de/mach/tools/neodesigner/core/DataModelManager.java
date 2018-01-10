package de.mach.tools.neodesigner.core;

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

/**
 * Der interne Datenmodel Manager
 *
 * @author Chris Deter
 *
 */
class DataModelManager extends Observable implements Save {
  private final List<Table> tables = new ArrayList<>();

  @Override
  public boolean hasTable(final String name) {
    return tables.indexOf(new TableImpl(name)) >= 0;
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
   */
  void addAll(final List<Table> allTables) {
    tables.addAll(allTables);
    setDataChanged();
  }

  /**
   * gibt alle Tabellen zurück.
   *
   * @return alle Tabellen
   */
  public List<Table> getTables() {
    final List<Table> t = new ArrayList<>();
    t.addAll(tables);
    return t;
  }

  /**
   * löscht alle Tabellen aus dem Datenmodell.
   */
  void clear() {
    tables.clear();
  }

  /**
   * gibt eine bestimmte Tabelle zurück.
   */
  @Override
  public Table getTable(final String name) {
    if (hasTable(name)) {
      return tables.get(tables.indexOf(new TableImpl(name)));
    } else {
      return null;
    }
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {
    final Table t = getTable(tableName);
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
  public void changeTableCategory(final String tableName, final String newKategory) {
    getTable(tableName).setCategory(newKategory);
  }

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean value) {
    getField(nodeName, tableName).setRequired(value);
  }

  @Override
  public void changeFieldTypeOfData(final String nodeName, final String tableName, final String type) {
    getField(nodeName, tableName).setTypeOfData(type);
  }

  @Override
  public void changeFieldIsPartOfPrim(final String nodeName, final String tableName, final Boolean partOfPrimaryKey) {
    final Table t = getTable(tableName);
    final Field f = getField(nodeName, tableName);
    final int i = t.getXpk().getFieldList().indexOf(f);
    if (i < 0 && partOfPrimaryKey) {
      t.getXpk().addField(f);
    } else if (i >= 0 && !partOfPrimaryKey) {
      t.getXpk().removeField(i);
    }
  }

  @Override
  public void insertNewTable(final Table t) {
    tables.add(t);
    setDataChanged();
  }

  @Override
  public void insertNewField(final Field f) {
    getTable(f.getNodeOf().getName()).getData().add(f);
    setDataChanged();
  }

  @Override
  public void insertNewIndex(final Index i) {
    getTable(i.getNodeOf().getName()).getIndizies().add(i);
    setDataChanged();
  }

  @Override
  public void insertNewForeignKey(final ForeignKey i) {
    getTable(i.getNodeOf().getName()).getForeignKeys().add(i);
    setDataChanged();
  }

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
      final String indexName) {
    final Table t = getTable(tableName);
    final Table rt = getTable(refTableName);
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(new IndexImpl(indexName, t)));
    final ForeignKey fk = t.getForeignKeys().get(t.getForeignKeys().indexOf(new ForeignKeyImpl(foreignKeyName, t)));
    fk.setRefTable(rt);
    fk.setIndex(i);
    setDataChanged();
  }

  @Override
  public void changeDataFields(final Index index) {
    final Table t = getTable(index.getNodeOf().getName());
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(index));
    final List<Field> lf = index.getFieldList();
    i.clearFieldList();
    for (final Field f : lf) {
      final String str = index.getAltName(f.getName());
      if (!"".equals(str)) {
        i.addField(t.getData().get(t.getData().indexOf(f)), str);
      } else {
        i.addField(t.getData().get(t.getData().indexOf(f)));
      }
    }
    setDataChanged();
  }

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean value) {
    final Table t = getTable(tableName);
    final Index i = t.getIndizies().get(t.getIndizies().indexOf(new IndexImpl(nodeName, t)));
    i.setUnique(value);
  }

  @Override
  public void deleteNode(final Node node) {
    Table t;
    if (node instanceof Field) {
      t = getTable(node.getNodeOf().getName());
      deleteField(t.getName(), node.getName());
    } else if (node instanceof ForeignKey) {
      t = getTable(node.getNodeOf().getName());
      deleteForeignKey(t.getName(), node.getName());
    } else if (node instanceof Index) {
      t = getTable(node.getNodeOf().getName());
      deleteIndex(t.getName(), node.getName());
    } else if (node instanceof Table) {
      deleteTable(node.getName());
    }
    setDataChanged();
  }

  /**
   * löscht eine Tabelle
   *
   * @param name
   */
  private void deleteTable(final String name) {
    tables.remove(tables.indexOf(getTable(name)));
  }

  /**
   * löscht ein Feld
   *
   * @param tableName
   * @param name
   * @return
   */
  private Field deleteField(final String tableName, final String name) {
    final Table t = getTable(tableName);
    Field f = null;
    final int index = t.getData().indexOf(new FieldImpl(name));
    if (index >= 0) {
      f = t.getData().remove(index);
    }
    return f;
  }

  /**
   * Löscht einen Index
   *
   * @param tableName
   * @param name
   * @return
   */
  private Index deleteIndex(final String tableName, final String name) {
    final Table t = getTable(tableName);
    Index f = null;
    final int index = t.getIndizies().indexOf(new IndexImpl(name, t));
    if (index >= 0) {
      f = t.getIndizies().remove(index);
    }
    return f;
  }

  /**
   * Löscht eine Fremdschlüssel
   *
   * @param tableName
   * @param name
   * @return
   */
  private ForeignKey deleteForeignKey(final String tableName, final String name) {
    final Table t = getTable(tableName);
    ForeignKey f = null;
    final int index = t.getForeignKeys().indexOf(new ForeignKeyImpl(name, t));
    if (index >= 0) {
      f = t.getForeignKeys().remove(index);
    }
    return f;
  }

  /**
   * Gibt ein Element einer Tabelle zurück
   *
   * @param nodeName
   * @param tableName
   * @return
   */
  private Node getNodeInTable(final String nodeName, final String tableName) {
    final Table t = getTable(tableName);
    Node ret = null;
    int index = t.getData().indexOf(new FieldImpl(nodeName));
    if (index >= 0) {
      ret = t.getData().get(index);
    }
    index = t.getIndizies().indexOf(new IndexImpl(nodeName, t));
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
   * Läd ein Feld aus einer Tabelle
   *
   * @param nodeName
   * @param tableName
   * @return
   */
  private Field getField(final String nodeName, final String tableName) {
    final Table t = getTable(tableName);
    return t.getData().get(t.getData().indexOf(new FieldImpl(nodeName)));
  }

  /**
   * Benachrichtigt den Observer, dass sich etwas im Datenmodell geändert hat.
   */
  private void setDataChanged() {
    setChanged();
    notifyObservers();
  }
}
