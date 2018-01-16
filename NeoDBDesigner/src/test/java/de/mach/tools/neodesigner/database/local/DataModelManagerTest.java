package de.mach.tools.neodesigner.database.local;

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
import de.mach.tools.neodesigner.database.cypher.DatabaseManager;
import de.mach.tools.neodesigner.database.cypher.DatabaseMockConnector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DataModelManagerTest {

  @Test
  public void testDataModelManagerTable() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save save = new SaveImpl(dmm_inside, new DatabaseManager(new DatabaseMockConnector()));
    Assert.assertTrue(dmm_inside.isEmpty());
    Assert.assertTrue(!save.getTable(TestDatamodel.tableName).isPresent());
    Table t = new TableImpl(TestDatamodel.tableName);
    t.setXpk(new IndexImpl(Strings.INDEXTYPE_XPK + TestDatamodel.tableName, t));
    save.insertNewTable(t);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName).get().equals(t));
    save.getTable(TestDatamodel.tableName);
    dmm_inside.changeTableName(TestDatamodel.tableName, TestDatamodel.tableName2,
        Strings.RELNAME_XPK + TestDatamodel.tableName2);
    save.changeTableCategory(TestDatamodel.tableName2, TestDatamodel.kategory);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().equals(t));
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().getCategory().equals(TestDatamodel.kategory));
    final List<Table> tbl = new ArrayList<>();
    tbl.add(new TableImpl(TestDatamodel.tableName2));
    dmm_inside.addAll(tbl);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().equals(t));
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().getFields().size() == 0);
    dmm_inside.clear();
    Assert.assertTrue(dmm_inside.isEmpty());
    t = new TableImpl(TestDatamodel.tableName);
    final Table tt = new TableImpl(TestDatamodel.tableName2);
    final List<Table> lt = new ArrayList<>();
    lt.add(t);
    lt.add(tt);
    dmm_inside.addAll(lt);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().equals(tt));
    Assert.assertTrue(save.getTable(TestDatamodel.tableName).get().equals(t));
    Assert.assertTrue(dmm_inside.getTables().contains(t));
    Assert.assertTrue(dmm_inside.getTables().contains(tt));

    save.deleteNode(tt);
    Assert.assertTrue(!save.getTable(TestDatamodel.tableName2).isPresent());
    Assert.assertTrue(!dmm_inside.getTables().contains(tt));
    Assert.assertTrue(dmm_inside.getTables().contains(t));
    save.deleteNode(t);
    Assert.assertTrue(!dmm_inside.getTables().contains(t));
  }

  @Test
  public void testDataModelManagerIndex() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save save = new SaveImpl(dmm_inside, new DatabaseManager(new DatabaseMockConnector()));
    final Table t = new TableImpl(TestDatamodel.tableName2);
    save.insertNewTable(t);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    save.insertNewIndex(i);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().size() > 0);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().get(0).isUnique() == true);
    save.changeIndexUnique(TestDatamodel.indexName, TestDatamodel.tableName2, false);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().get(0).isUnique() == false);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().size() > 0);
    save.changeNodeNameFromTable(TestDatamodel.indexName2, TestDatamodel.tableName2, TestDatamodel.indexName);
    save.deleteNode(save.getTable(TestDatamodel.tableName2).get().getIndizies().get(0));
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().size() == 0);
    save.insertNewIndex(i);
    save.deleteNode(i);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).get().getIndizies().size() == 0);
  }

  @Test
  public void testDataModelManagerField() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save dmm = new SaveImpl(dmm_inside, new DatabaseManager(new DatabaseMockConnector()));
    final Table t = new TableImpl(TestDatamodel.tableName2);
    dmm.insertNewTable(t);

    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype, TestDatamodel.fieldDatatypeSize,
        TestDatamodel.fieldIsNull, "", t);
    dmm.insertNewField(f);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().size() > 0);
    dmm.changeNodeNameFromTable(TestDatamodel.fieldName, TestDatamodel.tableName2, TestDatamodel.fieldName2);
    Assert.assertTrue(
        dmm.getTable(TestDatamodel.tableName2).get().getFields().get(0).getName().equals(TestDatamodel.fieldName2));
    dmm.changeFieldDomain(TestDatamodel.fieldName2, TestDatamodel.tableName2, TestDatamodel.fieldDatatype2,
        TestDatamodel.fieldDatatypeSize2);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().get(0).getDomain()
        .equals(TestDatamodel.fieldDatatype2));
    dmm.changeFieldRequired(TestDatamodel.fieldName2, TestDatamodel.tableName2, !TestDatamodel.fieldIsNull);
    Assert.assertTrue(
        dmm.getTable(TestDatamodel.tableName2).get().getFields().get(0).isRequired() == !TestDatamodel.fieldIsNull);
    dmm.deleteNode(dmm.getTable(TestDatamodel.tableName2).get().getFields().get(0));
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().size() == 0);
    dmm.insertNewField(f);
    Assert.assertTrue(!dmm.getTable(TestDatamodel.tableName2).get().getXpk().getFieldList().contains(f));
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, true);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getXpk().getFieldList().contains(f));
    dmm.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, false);
    Assert.assertTrue(!dmm.getTable(TestDatamodel.tableName2).get().getXpk().getFieldList().contains(f));
    dmm.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, false);
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, true);
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, true);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getXpk().getFieldList().contains(f));
    dmm.deleteNode(f);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getFields().size() == 0);
  }

  @Test
  public void testDataModelManagerForeignKey() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save dmm = new SaveImpl(dmm_inside, new DatabaseManager(new DatabaseMockConnector()));
    final Table t = new TableImpl(TestDatamodel.tableName2);
    final Table tt = new TableImpl(TestDatamodel.tableName);
    dmm.insertNewTable(t);
    dmm.insertNewTable(tt);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIF, t);
    dmm.insertNewIndex(i);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    dmm.insertNewForeignKey(fk);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().size() > 0);
    dmm.changeFkRelations(TestDatamodel.foreignKeyName, TestDatamodel.tableName2, TestDatamodel.tableName,
        TestDatamodel.indexName);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().get(0).getRefTable().equals(tt));
    dmm.changeNodeNameFromTable(TestDatamodel.foreignKeyName, TestDatamodel.tableName2, TestDatamodel.fieldName);
    dmm.deleteNode(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().get(0));
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().size() == 0);
    dmm.insertNewForeignKey(fk);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().size() > 0);
    dmm.deleteNode(fk);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).get().getForeignKeys().size() == 0);
  }

  @Test
  public void testDataModelManagerTableField() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save dmm = new SaveImpl(dmm_inside, new DatabaseManager(new DatabaseMockConnector()));
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
    Assert.assertTrue(!dmm.getTable(TestDatamodel.tableName).get().getIndizies().get(0).getFieldList().contains(f));
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName).get().getIndizies().get(0).getFieldList().contains(f2));
    i.clearFieldList();
    i.addField(f);
    dmm.changeDataFields(i);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName).get().getIndizies().get(0).getFieldList().contains(f));
    Assert.assertTrue(!dmm.getTable(TestDatamodel.tableName).get().getIndizies().get(0).getFieldList().contains(f2));
    dmm.saveComment(TestDatamodel.tableName, "Test");
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName).get().getComment().equals("Test"));
    dmm.saveComment(TestDatamodel.fieldName2, TestDatamodel.tableName, "Test2");
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName).get().getField(TestDatamodel.fieldName2).get().getComment()
        .equals("Test2"));
  }
}
