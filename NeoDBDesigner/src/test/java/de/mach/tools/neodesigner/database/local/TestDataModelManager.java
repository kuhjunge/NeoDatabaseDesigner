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

package de.mach.tools.neodesigner.database.local;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.Save;
import de.mach.tools.neodesigner.core.SaveImpl;
import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.TestDatamodel;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Index.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.cypher.DatabaseManagerLean;
import de.mach.tools.neodesigner.database.cypher.DatabaseMockConnector;


public class TestDataModelManager {

  @Test
  public void testDataModelManagerTable() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save save = new SaveImpl(dmm_inside, new DatabaseManagerLean(new DatabaseMockConnector()));
    assertTrue(dmm_inside.isEmpty());
    assertTrue(!save.getTable(TestDatamodel.tableName).isPresent());
    Table t = new TableImpl(TestDatamodel.tableName);
    t.setXpk(new IndexImpl(Strings.INDEXTYPE_XPK + TestDatamodel.tableName, t));
    save.insertNewTable(t);
    assertTrue(save.getTable(TestDatamodel.tableName).get().equals(t));
    save.getTable(TestDatamodel.tableName);
    dmm_inside.changeTableName(TestDatamodel.tableName, TestDatamodel.tableName2,
                               Strings.RELNAME_XPK + TestDatamodel.tableName2);
    save.changeTableCategory(TestDatamodel.tableName2, TestDatamodel.kategory);
    assertTrue(save.getTable(TestDatamodel.tableName2).get().equals(t));
    assertTrue(save.getTable(TestDatamodel.tableName2).get().getCategory().equals(TestDatamodel.kategory));
    final List<Table> tbl = new ArrayList<>();
    tbl.add(new TableImpl(TestDatamodel.tableName2));
    dmm_inside.addAll(tbl);
    assertTrue(save.getTable(TestDatamodel.tableName2).get().equals(t));
    assertTrue(save.getTable(TestDatamodel.tableName2).get().getFields().size() == 0);
    dmm_inside.clear();
    assertTrue(dmm_inside.isEmpty());
    t = new TableImpl(TestDatamodel.tableName);
    final Table tt = new TableImpl(TestDatamodel.tableName2);
    final List<Table> lt = new ArrayList<>();
    lt.add(t);
    lt.add(tt);
    dmm_inside.addAll(lt);
    assertTrue(save.getTable(TestDatamodel.tableName2).get().equals(tt));
    assertTrue(save.getTable(TestDatamodel.tableName).get().equals(t));
    assertTrue(dmm_inside.getTables().contains(t));
    assertTrue(dmm_inside.getTables().contains(tt));

    save.deleteNode(tt);
    assertTrue(!save.getTable(TestDatamodel.tableName2).isPresent());
    assertTrue(!dmm_inside.getTables().contains(tt));
    assertTrue(dmm_inside.getTables().contains(t));
    save.deleteNode(t);
    assertTrue(!dmm_inside.getTables().contains(t));
  }

  @Test
  public void testDataModelManagerIndex() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save save = new SaveImpl(dmm_inside, new DatabaseManagerLean(new DatabaseMockConnector()));
    final Table t = new TableImpl(TestDatamodel.tableName2);
    save.insertNewTable(t);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    save.insertNewIndex(i);
    assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().size() > 0);
    assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().get(0).isUnique() == true);
    save.changeIndexUnique(TestDatamodel.indexName, TestDatamodel.tableName2, false);
    assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().get(0).isUnique() == false);
    assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().size() > 0);
    save.changeNodeNameFromTable(TestDatamodel.indexName2, TestDatamodel.tableName2, TestDatamodel.indexName, "Index");
    save.deleteNode(save.getTable(TestDatamodel.tableName2).get().getIndizies().get(0));
    assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().size() == 0);
    save.insertNewIndex(i);
    save.deleteNode(i);
    assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().size() == 0);
  }

  @Test
  public void testDataModelManagerField() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save dmm = new SaveImpl(dmm_inside, new DatabaseManagerLean(new DatabaseMockConnector()));
    final Table t = new TableImpl(TestDatamodel.tableName2);
    dmm.insertNewTable(t);

    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
                                  TestDatamodel.fieldIsNull, "", t);
    dmm.insertNewField(f);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().size() > 0);
    dmm.changeNodeNameFromTable(TestDatamodel.fieldName, TestDatamodel.tableName2, TestDatamodel.fieldName2, "Column");
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().get(0).getName()
        .equals(TestDatamodel.fieldName2));
    dmm.changeFieldDomain(TestDatamodel.fieldName2, TestDatamodel.tableName2, TestDatamodel.fieldDatatype2,
                          TestDatamodel.fieldDatatypeSize2);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().get(0).getDomain()
        .equals(TestDatamodel.fieldDatatype2));
    dmm.changeFieldRequired(TestDatamodel.fieldName2, TestDatamodel.tableName2, !TestDatamodel.fieldIsNull);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().get(0)
        .isRequired() == !TestDatamodel.fieldIsNull);
    dmm.deleteNode(dmm.getTable(TestDatamodel.tableName2).get().getFields().get(0));
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().size() == 0);
    dmm.insertNewField(f);
    assertTrue(!dmm.getTable(TestDatamodel.tableName2).get().getXpk().getFieldList().contains(f));
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.tableName2, TestDatamodel.xpkName2, TestDatamodel.fieldName2, true,
                                       0);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getXpk().getFieldList().contains(f));
    dmm.changeFieldIsPartOfPrim(TestDatamodel.tableName2, TestDatamodel.fieldName2, TestDatamodel.fieldName2, false, 0);
    assertTrue(!dmm.getTable(TestDatamodel.tableName2).get().getXpk().getFieldList().contains(f));
    dmm.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.xpkName2, TestDatamodel.tableName2, false, 0);
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.tableName2, TestDatamodel.xpkName2, TestDatamodel.fieldName2, true,
                                       0);
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.tableName2, TestDatamodel.xpkName2, TestDatamodel.fieldName2, true,
                                       0);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getXpk().getFieldList().contains(f));
    dmm.deleteNode(f);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().size() == 0);
  }

  @Test
  public void testDataModelManagerForeignKey() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save dmm = new SaveImpl(dmm_inside, new DatabaseManagerLean(new DatabaseMockConnector()));
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
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().size() > 0);
    assertTrue(dmm.getTable(TestDatamodel.tableName).get().getRefForeignKeys().size() > 0);
    dmm.changeFkRelations(TestDatamodel.foreignKeyName, TestDatamodel.tableName2, TestDatamodel.tableName,
                          TestDatamodel.indexName, "ColumnName", 1);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().get(0).getRefTable().equals(tt));
    dmm.changeNodeNameFromTable(TestDatamodel.foreignKeyName, TestDatamodel.tableName2, TestDatamodel.fieldName,
                                "ForeignKey");
    dmm.deleteNode(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().get(0));
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().size() == 0);
    dmm.insertNewForeignKey(fk);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().size() > 0);
    dmm.deleteNode(fk);
    assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().size() == 0);
  }

  @Test
  public void testDataModelManagerTableField() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save dmm = new SaveImpl(dmm_inside, new DatabaseManagerLean(new DatabaseMockConnector()));
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
    assertTrue(!dmm.getTable(TestDatamodel.tableName).get().getIndizies().get(0).getFieldList().contains(f));
    assertTrue(dmm.getTable(TestDatamodel.tableName).get().getIndizies().get(0).getFieldList().contains(f2));
    i.clearFieldList();
    i.addField(f);
    dmm.changeDataFields(i);
    assertTrue(dmm.getTable(TestDatamodel.tableName).get().getIndizies().get(0).getFieldList().contains(f));
    assertTrue(!dmm.getTable(TestDatamodel.tableName).get().getIndizies().get(0).getFieldList().contains(f2));
    dmm.saveComment(TestDatamodel.tableName, "Test");
    assertTrue(dmm.getTable(TestDatamodel.tableName).get().getComment().equals("Test"));
    dmm.saveComment(TestDatamodel.fieldName2, TestDatamodel.tableName, "Test2");
    assertTrue(dmm.getTable(TestDatamodel.tableName).get().getField(TestDatamodel.fieldName2).get().getComment()
        .equals("Test2"));
  }
}
