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


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Index.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;


class TestDataModelManager {

  @Test
  void testDataModelManagerTable() {
    final DataModelManager dmm_inside = new DataModelManager();
    dmm_inside.clearListener();
    MockDatamodelListener listener = new MockDatamodelListener();
    dmm_inside.addListener(listener);
    final Save save = dmm_inside;
    assertTrue(dmm_inside.isEmpty());
    assertTrue(!save.getTable(TestDatamodel.tableName).isPresent());
    Table t = new TableImpl(TestDatamodel.tableName);
    t.setXpk(new IndexImpl(Strings.INDEXTYPE_XPK + TestDatamodel.tableName, t));
    save.insertNewTable(t);
    assertEquals(getTable(save, TestDatamodel.tableName), t);
    dmm_inside.changeTableName(TestDatamodel.tableName, TestDatamodel.tableName2,
                               Strings.RELNAME_XPK + TestDatamodel.tableName2);
    assertTrue(listener.isTriggered());
    save.changeTableCategory(TestDatamodel.tableName2, TestDatamodel.kategory);
    assertEquals(getTable(save, TestDatamodel.tableName2), t);
    assertEquals(getTable(save, TestDatamodel.tableName2).getCategory(), TestDatamodel.kategory);
    final List<Table> tbl = new ArrayList<>();
    tbl.add(new TableImpl(TestDatamodel.tableName2));
    dmm_inside.addAll(tbl);
    assertEquals(getTable(save, TestDatamodel.tableName2), t);
    assertEquals(0, getTable(save, TestDatamodel.tableName2).getFields().size());
    dmm_inside.clear();
    assertTrue(dmm_inside.isEmpty());
    t = new TableImpl(TestDatamodel.tableName);
    final Table tt = new TableImpl(TestDatamodel.tableName2);
    final List<Table> lt = new ArrayList<>();
    lt.add(t);
    lt.add(tt);
    dmm_inside.addAll(lt);
    assertEquals(getTable(save, TestDatamodel.tableName2), tt);
    assertEquals(getTable(save, TestDatamodel.tableName), t);
    assertTrue(dmm_inside.getTables().contains(t));
    assertTrue(dmm_inside.getTables().contains(tt));

    save.deleteNode(tt);
    assertTrue(!save.getTable(TestDatamodel.tableName2).isPresent());
    assertTrue(!dmm_inside.getTables().contains(tt));
    assertTrue(dmm_inside.getTables().contains(t));
    save.deleteNode(t);
    assertTrue(!dmm_inside.getTables().contains(t));
    dmm_inside.clearListener();
  }

  private Table getTable(Save s, String name) {
    Optional<Table> tbl = s.getTable(name);
    assertTrue(tbl.isPresent());
    return tbl.orElseGet(() -> new TableImpl("fehler"));
  }

  @Test
  void testDataModelManagerIndex() {
    final Save save = new DataModelManager();
    final Table t = new TableImpl(TestDatamodel.tableName2);
    save.insertNewTable(t);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    save.insertNewIndex(i);
    assertTrue(getTable(save, TestDatamodel.tableName2).getIndizies().size() > 0);
    assertTrue(getTable(save, TestDatamodel.tableName2).getIndizies().get(0).isUnique());
    save.changeIndexUnique(TestDatamodel.indexName, TestDatamodel.tableName2, false);
    assertTrue(!getTable(save, TestDatamodel.tableName2).getIndizies().get(0).isUnique());
    assertTrue(getTable(save, TestDatamodel.tableName2).getIndizies().size() > 0);
    save.changeNodeNameFromTable(TestDatamodel.indexName2, TestDatamodel.tableName2, TestDatamodel.indexName, "Index");
    save.deleteNode(getTable(save, TestDatamodel.tableName2).getIndizies().get(0));
    assertEquals(0, getTable(save, TestDatamodel.tableName2).getIndizies().size());
    save.insertNewIndex(i);
    save.deleteNode(i);
    assertEquals(0, getTable(save, TestDatamodel.tableName2).getIndizies().size());
  }

  @Test
  void testDataModelManagerField() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save save = dmm_inside;
    final Table t = new TableImpl(TestDatamodel.tableName2);
    save.insertNewTable(t);

    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    save.insertNewField(f);
    assertTrue(getTable(save, TestDatamodel.tableName2).getFields().size() > 0);
    save.changeNodeNameFromTable(TestDatamodel.fieldName, TestDatamodel.tableName2, TestDatamodel.fieldName2, "Column");
    assertEquals(getTable(save, TestDatamodel.tableName2).getFields().get(0).getName(), TestDatamodel.fieldName2);
    save.changeFieldDomain(TestDatamodel.fieldName2, TestDatamodel.tableName2, TestDatamodel.fieldDatatype2,
                           TestDatamodel.fieldDatatypeSize2);
    assertEquals(getTable(save, TestDatamodel.tableName2).getFields().get(0).getDomain(), TestDatamodel.fieldDatatype2);
    save.changeFieldRequired(TestDatamodel.fieldName2, TestDatamodel.tableName2, !TestDatamodel.fieldIsNull);
    assertEquals(getTable(save, TestDatamodel.tableName2).getFields().get(0).isRequired(), !TestDatamodel.fieldIsNull);
    save.deleteNode(getTable(save, TestDatamodel.tableName2).getFields().get(0));
    assertEquals(0, getTable(save, TestDatamodel.tableName2).getFields().size());
    save.insertNewField(f);
    assertTrue(!getTable(save, TestDatamodel.tableName2).getXpk().getFieldList().contains(f));
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.tableName2, TestDatamodel.xpkName2, TestDatamodel.fieldName2, true,
                                       0);
    assertTrue(getTable(save, TestDatamodel.tableName2).getXpk().getFieldList().contains(f));
    save.changeFieldIsPartOfPrim(TestDatamodel.tableName2, TestDatamodel.fieldName2, TestDatamodel.fieldName2, false,
                                 0);
    assertTrue(!getTable(save, TestDatamodel.tableName2).getXpk().getFieldList().contains(f));
    save.changeFieldIsPartOfPrim(TestDatamodel.tableName2, TestDatamodel.xpkName2, TestDatamodel.fieldName2, false, 0);
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.tableName2, TestDatamodel.xpkName2, TestDatamodel.fieldName2, true,
                                       0);
    assertTrue(getTable(save, TestDatamodel.tableName2).getXpk().getFieldList().contains(f));
    save.deleteNode(f);
    assertEquals(0, getTable(save, TestDatamodel.tableName2).getFields().size());
  }

  @Test
  void testDataModelManagerForeignKey() {
    final Save dmm = new DataModelManager();
    final Table t = new TableImpl(TestDatamodel.tableName2);
    final Table tt = new TableImpl(TestDatamodel.tableName);
    dmm.insertNewTable(t);
    dmm.insertNewTable(tt);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIF, t);
    dmm.insertNewIndex(i);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fk.setRefTable(tt);
    fk.setIndex(i);
    dmm.insertNewForeignKey(fk);
    assertTrue(getTable(dmm, TestDatamodel.tableName2).getForeignKeys().size() > 0);
    assertTrue(getTable(dmm, TestDatamodel.tableName).getRefForeignKeys().size() > 0);
    dmm.changeFkRelations(TestDatamodel.foreignKeyName, TestDatamodel.tableName2, TestDatamodel.tableName,
                          TestDatamodel.indexName, "ColumnName", 1);
    assertEquals(getTable(dmm, TestDatamodel.tableName2).getForeignKeys().get(0).getRefTable(), tt);
    dmm.changeNodeNameFromTable(TestDatamodel.foreignKeyName, TestDatamodel.tableName2, TestDatamodel.fieldName,
                                "ForeignKey");
    dmm.deleteNode(getTable(dmm, TestDatamodel.tableName2).getForeignKeys().get(0));
    assertEquals(0, getTable(dmm, TestDatamodel.tableName2).getForeignKeys().size());
    dmm.insertNewForeignKey(fk);
    assertTrue(getTable(dmm, TestDatamodel.tableName2).getForeignKeys().size() > 0);
    dmm.deleteNode(fk);
    assertEquals(0, getTable(dmm, TestDatamodel.tableName2).getForeignKeys().size());
  }

  @Test
  void testDataModelManagerTableField() {
    final Save dmm = new DataModelManager();
    final Table t = new TableImpl(TestDatamodel.tableName);
    dmm.insertNewTable(t);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIE, t);
    dmm.insertNewIndex(i);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    dmm.insertNewField(f);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype2,
                                   TestDatamodel.fieldDatatypeSize2, TestDatamodel.fieldIsNull, "", t);
    dmm.insertNewField(f2);
    i.clearFieldList();
    i.addField(f2);
    dmm.changeDataFields(i);
    assertTrue(!getTable(dmm, TestDatamodel.tableName).getIndizies().get(0).getFieldList().contains(f));
    assertTrue(getTable(dmm, TestDatamodel.tableName).getIndizies().get(0).getFieldList().contains(f2));
    i.clearFieldList();
    i.addField(f);
    dmm.changeDataFields(i);
    assertTrue(getTable(dmm, TestDatamodel.tableName).getIndizies().get(0).getFieldList().contains(f));
    assertTrue(!getTable(dmm, TestDatamodel.tableName).getIndizies().get(0).getFieldList().contains(f2));
    dmm.changeComment(TestDatamodel.tableName, "Test");
    assertEquals("Test", getTable(dmm, TestDatamodel.tableName).getComment());
    dmm.changeComment(TestDatamodel.fieldName2, TestDatamodel.tableName, "Test2");
    assertEquals("Test2", getTable(dmm, TestDatamodel.tableName).getField(TestDatamodel.fieldName2).getComment());
  }
}
