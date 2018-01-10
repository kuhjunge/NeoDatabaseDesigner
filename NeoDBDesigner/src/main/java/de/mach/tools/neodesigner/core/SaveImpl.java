package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.SaveDatabase;

/**
 * Implementation des Save Interface. Alle Operationen werden im internen
 * Datenmodell und auf der Datenbank durchgeführt
 * 
 * @author Chris Deter
 *
 */
class SaveImpl implements Save {
  private final DataModelManager dmm;
  private final SaveDatabase db;

  SaveImpl(final DataModelManager dmm, final DatabaseConnection db) {
    this.dmm = dmm;
    this.db = db;
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {
    dmm.changeTableName(tableName, newName, xpkName);
    db.changeTableName(tableName, newName, xpkName);
  }

  @Override
  public void changeNodeNameFromTable(final String nodeName, final String tableName, final String newNodeName) {
    dmm.changeNodeNameFromTable(nodeName, tableName, newNodeName);
    db.changeNodeNameFromTable(nodeName, tableName, newNodeName);
  }

  @Override
  public void changeTableCategory(final String tableName, final String newKategory) {
    dmm.changeTableCategory(tableName, newKategory);
    db.changeTableCategory(tableName, newKategory);
  }

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean value) {
    dmm.changeFieldRequired(nodeName, tableName, value);
    db.changeFieldRequired(nodeName, tableName, value);
  }

  @Override
  public void changeFieldTypeOfData(final String nodeName, final String tableName, final String type) {
    dmm.changeFieldTypeOfData(nodeName, tableName, type);
    db.changeFieldTypeOfData(nodeName, tableName, type);
  }

  @Override
  public void changeFieldIsPartOfPrim(final String nodeName, final String tableName, final Boolean partOfPrimaryKey) {
    dmm.changeFieldIsPartOfPrim(nodeName, tableName, partOfPrimaryKey);
    db.changeFieldIsPartOfPrim(nodeName, tableName, partOfPrimaryKey);
  }

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean value) {
    dmm.changeIndexUnique(nodeName, tableName, value);
    db.changeIndexUnique(nodeName, tableName, value);
  }

  @Override
  public void insertNewTable(final Table table) {
    dmm.insertNewTable(table);
    db.insertNewTable(table);
  }

  @Override
  public void insertNewField(final Field field) {
    db.insertNewField(field);
    dmm.insertNewField(field);
  }

  @Override
  public void insertNewIndex(final Index index) {
    dmm.insertNewIndex(index);
    db.insertNewIndex(index);
  }

  @Override
  public void insertNewForeignKey(final ForeignKey foreignKey) {
    dmm.insertNewForeignKey(foreignKey);
    db.insertNewForeignKey(foreignKey);
  }

  @Override
  public void deleteNode(final Node node) {
    dmm.deleteNode(node);
    db.deleteNode(node);
  }

  @Override
  public void changeDataFields(final Index index) {
    dmm.changeDataFields(index);
    db.changeDataFields(index);
  }

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
      final String indexName) {
    dmm.changeFkRelations(foreignKeyName, tableName, refTableName, indexName);
    db.changeFkRelations(foreignKeyName, tableName, refTableName, indexName);
  }

  @Override
  public Table getTable(final String tablename) {
    return dmm.getTable(tablename);
  }

  @Override
  public boolean hasTable(final String name) {
    return dmm.hasTable(name);
  }
}
