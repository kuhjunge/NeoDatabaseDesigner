package de.mach.tools.neodesigner.core;
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

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.List;
import java.util.Optional;

public class MockSave implements Save {

  public Table retTable;

  @Override
  public Optional<Table> getTable(final String tableName) {
    return Optional.of(retTable);
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {
    // TODO Auto-generated method stub

  }

  String changeNodeNameFromTable = "";

  @Override
  public void changeNodeNameFromTable(final String nodeName, final String tableName, final String newNodeName) {
    changeNodeNameFromTable = nodeName + tableName + newNodeName;
  }

  @Override
  public void changeTableCategory(final String tableName, final String newKategory) {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeFieldDomain(final String nodeName, final String tableName, final DomainId type, final int length) {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeFieldIsPartOfPrim(final String nodeName, final String tableName, final Boolean partOfPrimaryKey) {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void insertNewTable(final Table table) {
    // TODO Auto-generated method stub

  }

  @Override
  public void insertNewField(final Field field) {
    // TODO Auto-generated method stub

  }

  @Override
  public void insertNewIndex(final Index index) {
    // TODO Auto-generated method stub

  }

  public ForeignKey insertedForeignKey;

  @Override
  public void insertNewForeignKey(final ForeignKey foreignKey) {
    insertedForeignKey = foreignKey;
  }

  @Override
  public void deleteNode(final Node node) {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeDataFields(final Index index) {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
      final String indexName) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<String> getFieldNameCase(final String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void saveComment(final String name, final String tableName, final String comment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void saveComment(final String tableName, final String comment) {
    // TODO Auto-generated method stub

  }

  public String changePrimFieldNameRelation = "";

  @Override
  public void changePrimFieldNameRelation(final String tableName, final String newNodeName, final String oldNodeName) {
    changePrimFieldNameRelation = oldNodeName + tableName + newNodeName;
  }

  @Override
  public boolean hasTable(final String name) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public List<Table> getTables() {
    // TODO Auto-generated method stub
    return null;
  }

}
