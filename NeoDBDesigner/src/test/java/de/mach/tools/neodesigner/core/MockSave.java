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


import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;


public class MockSave implements Save {

  public Table retTable;

  @Override
  public Optional<Table> getTable(final String tableName) {
    return Optional.of(retTable);
  }

  @Override
  public void changeTableName(final String tableName, final String newName, final String xpkName) {}

  String changeNodeNameFromTable = "";

  @Override
  public void changeNodeNameFromTable(final String nodeName, final String tableName, final String newNodeName,
                                      final String type) {
    changeNodeNameFromTable = nodeName + tableName + newNodeName;
  }

  @Override
  public void changeTableCategory(final String tableName, final String newKategory) {}

  @Override
  public void changeFieldRequired(final String nodeName, final String tableName, final Boolean value) {}

  @Override
  public void changeFieldDomain(final String nodeName, final String tableName, final DomainId type, final int length) {}

  @Override
  public void changeFieldIsPartOfPrim(final String tableName, final String xpkName, final String columnName,
                                      final Boolean partOfPrimaryKey, final int size) {}

  @Override
  public void changeIndexUnique(final String nodeName, final String tableName, final Boolean value) {}

  @Override
  public void insertNewTable(final Table table) {}

  @Override
  public void insertNewField(final Field field) {}

  @Override
  public void insertNewIndex(final Index index) {}

  public ForeignKey insertedForeignKey;

  @Override
  public void insertNewForeignKey(final ForeignKey foreignKey) {
    insertedForeignKey = foreignKey;
  }

  @Override
  public void deleteNode(final Node node) {}

  @Override
  public void changeDataFields(final Index index) {}

  @Override
  public void changeFkRelations(final String foreignKeyName, final String tableName, final String refTableName,
                                final String indexName, final String columnName, final int order) {}

  @Override
  public List<String> getFieldNameWithoutCase(final String name) {
    return null;
  }

  @Override
  public void changeComment(final String name, final String tableName, final String comment) {}

  @Override
  public void changeOrder(final String name, final String tableName, final int order) {}

  @Override
  public void changeXpkOrder(String tableName, Map<Integer, String> newOrder) {

  }

  @Override
  public void changeComment(final String tableName, final String comment) {}

  @Override
  public boolean hasTable(final String name) {
    return false;
  }

  @Override
  public List<Table> getTables() {
    return null;
  }
}
