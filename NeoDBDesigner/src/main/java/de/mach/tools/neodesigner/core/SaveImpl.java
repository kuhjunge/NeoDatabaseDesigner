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

package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.SaveDatabase;
import de.mach.tools.neodesigner.database.local.DataModelManager;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation des Save Interface. Alle Operationen werden im internen
 * Datenmodell und auf der Datenbank durchgeführt
 *
 * @author Chris Deter
 *
 */
public class SaveImpl implements Save {
  private final DataModelManager dmm;
  private final SaveDatabase db;
  private static final Logger LOG = Logger.getLogger(SaveImpl.class.getName());

  /**
   * Constructor.
   *
   * @param dmm
   *          DataModelManager
   * @param db
   *          DatabaseConnection
   */
  public SaveImpl(final DataModelManager dmm, final DatabaseConnection db) {
    this.dmm = dmm;
    this.db = db;
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGETABLENAME);
    dmm.changeTableName(tableName, newName, xpkName);
    db.changeTableName(tableName, newName, xpkName);
  }

  @Override
  public void changeNodeNameFromTable(final String nodeName, final String tableName, final String newNodeName) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGENODENAME);
    dmm.changeNodeNameFromTable(nodeName, tableName, newNodeName);
    db.changeNodeNameFromTable(nodeName, tableName, newNodeName);
  }

  @Override
  public void changeTableCategory(final String tableName, final String newKategory) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGETABLECAT);
    dmm.changeTableCategory(tableName, newKategory);
    db.changeTableCategory(tableName, newKategory);
  }

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean value) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGEFIELDREQ);
    dmm.changeFieldRequired(nodeName, tableName, value);
    db.changeFieldRequired(nodeName, tableName, value);
  }

  @Override
  public void changeFieldDomain(final String nodeName, final String tableName, final DomainId type, final int length) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGEFIELDTOD);
    dmm.changeFieldDomain(nodeName, tableName, type, length);
    db.changeFieldDomain(nodeName, tableName, type, length);
  }

  @Override
  public void changeFieldIsPartOfPrim(final String nodeName, final String tableName, final Boolean partOfPrimaryKey) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGEFIELDPRIM);
    dmm.changeFieldIsPartOfPrim(nodeName, tableName, partOfPrimaryKey);
    db.changeFieldIsPartOfPrim(nodeName, tableName, partOfPrimaryKey);
  }

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean value) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGEINDEXUNIQUE);
    dmm.changeIndexUnique(nodeName, tableName, value);
    db.changeIndexUnique(nodeName, tableName, value);
  }

  @Override
  public void insertNewTable(final Table table) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_TABLENEW);
    dmm.insertNewTable(table);
    db.insertNewTable(table);
  }

  @Override
  public void insertNewField(final Field field) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_NEWFIELD);
    db.insertNewField(field);
    dmm.insertNewField(field);
  }

  @Override
  public void insertNewIndex(final Index index) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_NEWINDEX);
    dmm.insertNewIndex(index);
    db.insertNewIndex(index);
  }

  @Override
  public void insertNewForeignKey(final ForeignKey foreignKey) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_NEWFK);
    dmm.insertNewForeignKey(foreignKey);
    db.insertNewForeignKey(foreignKey);
  }

  @Override
  public void deleteNode(final Node node) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_DELETENODE);
    dmm.deleteNode(node);
    db.deleteNode(node);
  }

  @Override
  public void changeDataFields(final Index index) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGEDATAFIELDS);
    dmm.changeDataFields(index);
    db.changeDataFields(index);
  }

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
      final String indexName) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGEFKREL);
    dmm.changeFkRelations(foreignKeyName, tableName, refTableName, indexName);
    db.changeFkRelations(foreignKeyName, tableName, refTableName, indexName);
  }

  @Override
  public Optional<Table> getTable(final String tablename) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_GETTABLE);
    return dmm.getTable(tablename);
  }

  @Override
  public boolean hasTable(final String name) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_HASTABLE);
    return dmm.hasTable(name);
  }

  @Override
  public List<Table> getTables() {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_GETTABLES);
    return dmm.getTables();
  }

  @Override
  public List<String> getFieldNameCase(final String name) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_GETFIELDNAMECASE);
    return db.getFieldNameCase(name);
  }

  @Override
  public void saveComment(final String name, final String tableName, final String comment) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_SAVECOMMENT);
    dmm.saveComment(name, tableName, comment);
    db.saveComment(name, tableName, comment);
  }

  @Override
  public void saveComment(final String tableName, final String comment) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_SAVECOMMENT);
    dmm.saveComment(tableName, comment);
    db.saveComment(tableName, comment);
  }

  @Override
  public void changePrimFieldNameRelation(final String tableName, final String newNodeName, final String oldNodeName) {
    SaveImpl.LOG.log(Level.FINE, Strings.LOG_CHANGEFIELDPRIM);
    dmm.changePrimFieldNameRelation(tableName, newNodeName, oldNodeName);
    db.changePrimFieldNameRelation(tableName, newNodeName, oldNodeName);
  }
}
