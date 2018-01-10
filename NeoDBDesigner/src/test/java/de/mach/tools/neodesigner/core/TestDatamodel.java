package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Index.Type;
import de.mach.tools.neodesigner.core.datamodel.Relations;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldWrapper;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.database.DatabaseManager;
import de.mach.tools.neodesigner.database.DatabaseMockConnector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestDatamodel {
  public static final String kategory = "1.1";
  public static final String tableName = "Testtabelle";
  public static final String tableName2 = "Testtabelle2";
  public static final String fieldName = "TestFeld";
  public static final String fieldName2 = "TestFeld2";
  public static final String order = "1";
  public static final String order2 = "2";
  public static final String indexName = "XAKTestIndex";
  public static final String indexName2 = "XIETestIndex";
  public static final String indexName3 = "XIFTestIndex";
  public static final String indexName4 = "XIFIndexNeuTest";
  public static final String indexNameShort = "XIF";
  public static final String indexPrimName = "TestPrimIndex";
  public static final String foreignKeyName = "R_TestForeignKey";
  public static final String fieldDatatype = "String";
  public static final String fieldDatatype2 = "Integer";
  public static final Boolean fieldIsNull = true;

  @Test
  public void testConfiguration() {
    final String adrCheckRes = "bolt\\://127.0.0.1\\:7687";
    final String startLocCheckRes = "E\\:\\Programme\\Neo4J";
    final String pwCheckRes = "test";
    final String usrCheckRes = "MaxMustermann";
    final int lengthCheckRes = 26;
    final Configuration conf = new Configuration();
    conf.init();
    final String adr = conf.getAddrOfDb();
    final String startLoc = conf.getNeoDbStarterLocation();
    final String pw = conf.getPw();
    final String usr = conf.getUser();
    final int length = conf.getWordLength();
    conf.setNeoDbStarterLocation(startLocCheckRes);
    conf.save(adrCheckRes, usrCheckRes, pwCheckRes);
    conf.setWordLength(lengthCheckRes);
    conf.save();
    conf.init();
    final String adrCheck = conf.getAddrOfDb();
    final String startLocCheck = conf.getNeoDbStarterLocation();
    final String pwCheck = conf.getPw();
    final String usrCheck = conf.getUser();
    final int lengthCheck = conf.getWordLength();
    conf.setAddrOfDb(adr);
    conf.setNeoDbStarterLocation(startLoc);
    conf.setPw(pw);
    conf.setUser(usr);
    conf.setWordLength(length);
    conf.save();
    Assert.assertTrue(adrCheckRes.contains(adrCheck));
    Assert.assertTrue(startLocCheckRes.contains(startLocCheck));
    Assert.assertTrue(pwCheckRes.contains(pwCheck));
    Assert.assertTrue(usrCheckRes.contains(usrCheck));
    Assert.assertTrue(lengthCheckRes == lengthCheck);
  }

  @Test
  public void testFieldWrapper() {
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final FieldWrapper fw = new FieldWrapper(f, TestDatamodel.fieldName2, 1);
    fw.setAltName(TestDatamodel.tableName2);
    Assert.assertTrue(fw.getAltName().equals(TestDatamodel.tableName2));
    Assert.assertTrue(fw.getOrder() == 1);
    Assert.assertTrue(
        fw.toString().equals(TestDatamodel.fieldName + " (" + TestDatamodel.tableName2 + ") 1"));
  }

  @Test
  public void testRelations() {
    new Relations();
    Assert.assertTrue(Relations.Type.valueOf("CONNECTED").compareTo(Relations.Type.CONNECTED) == 0);
    Assert.assertTrue(Relations.Type.values().length == 7);
    Assert.assertTrue(Relations.getType(Relations.Type.DEFAULT).contains(""));
    Assert.assertTrue(Relations.getType(Relations.Type.CONNECTED).contains("CONNECTED"));
    Assert.assertTrue(Relations.getType(Relations.Type.DATA).contains("DATA"));
    Assert.assertTrue(Relations.getType(Relations.Type.FOREIGNKEY).contains("FOREIGNKEY"));
    Assert.assertTrue(Relations.getType(Relations.Type.INDEX).contains("INDEX"));
    Assert.assertTrue(Relations.getType(Relations.Type.REFERENCE).contains("REFERENCE"));
    Assert.assertTrue(Relations.getType(Relations.Type.XPK).contains("XPK"));

  }

  @Test
  public void testTableImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName2, null);
    final Index xpk = new IndexImpl(TestDatamodel.indexPrimName, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    tableInterfaceTest(t, f, i, i2, xpk, fk);
  }

  @Test
  public void testViewTable2() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Index i = new IndexImpl(TestDatamodel.indexName, t);
    final Index i2 = new IndexImpl(TestDatamodel.indexName2, null);
    final Index xpk = new IndexImpl(TestDatamodel.indexPrimName, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fk.setIndex(i2);
    fk.setRefTable(t);
    tableInterfaceTest(t, f, i, i2, xpk, fk);
    t.setName(TestDatamodel.tableName);
    final ViewTable tt = new ViewTable(t);
    tableInterfaceTest(tt, f, i, i2, xpk, fk);
  }

  @Test
  public void testViewTable() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewField f = new ViewField(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    final ViewIndex xpk = new ViewIndex(TestDatamodel.indexPrimName, t);

    t.setCategory(TestDatamodel.kategory);
    t.addField(f);
    t.setXpk(xpk);

    Assert.assertTrue(!t.equals(null));
    Assert.assertTrue(t.hashCode() != 0);
    Assert.assertTrue(t.toString(), TestDatamodel.tableName.equals(t.getName()));
    Assert.assertTrue("T Kategory", TestDatamodel.kategory.equals(t.getCategory()));
    Assert.assertTrue(t.getNodeOf() == null);
    Assert.assertTrue(t.getData().contains(f));
    Assert.assertTrue(t.getVData().contains(f));
    Assert.assertTrue(t.getXpk().getName().equals(xpk.getName()));

    t.setName(TestDatamodel.tableName2);
    Assert.assertTrue("T Name", TestDatamodel.tableName2.equals(t.getName()));
  }

  private void tableInterfaceTest(final Table t, final Field f, final Index i, final Index i2,
      final Index xpk, final ForeignKey fk) {
    t.setCategory(TestDatamodel.kategory);
    t.addField(f);
    t.getIndizies().add(i);
    t.setXpk(xpk);
    t.getForeignKeys().add(fk);

    Assert.assertTrue(t != null);
    Assert.assertTrue(t.hashCode() != 0);
    Assert.assertTrue(t.toString(), TestDatamodel.tableName.equals(t.getName()));
    Assert.assertTrue("T Kategory", TestDatamodel.kategory.equals(t.getCategory()));
    Assert.assertTrue(t.getNodeOf() == null);
    Assert.assertTrue(t.getData().contains(f));
    Assert.assertTrue(t.getXpk().getName().equals(xpk.getName()));
    Assert.assertTrue(t.getIndizies().contains(i));
    Assert.assertTrue(!t.getIndizies().contains(i2));
    Assert.assertTrue(t.getForeignKeys().contains(fk));

    t.setName(TestDatamodel.tableName2);
    Assert.assertTrue("T Name", TestDatamodel.tableName2.equals(t.getName()));
  }

  @Test
  public void testFieldImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);

    fieldInterfaceTest(t, f);
  }

  @Test
  public void testViewField() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t), t);
    Assert.assertTrue(f.isModifiedName() == false);
    Assert.assertTrue(f.isModifiedPrim() == false);
    Assert.assertTrue(f.isModifiedReq() == false);
    Assert.assertTrue(f.isModifiedType() == false);
    fieldInterfaceTest(t, f);
    Assert.assertTrue(f.nameProperty() != null);
    Assert.assertTrue(f.primProperty() != null);
    Assert.assertTrue(f.requiredProperty() != null);
    Assert.assertTrue(f.typeOfDataProperty() != null);
    Assert.assertTrue(f.isNewCreated() == false);
    Assert.assertTrue(f.isModifiedName() == true);
    Assert.assertTrue(f.isModifiedPrim() == true);
    Assert.assertTrue(f.isModifiedReq() == true);
    Assert.assertTrue(f.isModifiedType() == true);
    Assert.assertTrue(f.getOldName().equals(TestDatamodel.fieldName));
    f.saved();
    Assert.assertTrue(f.isModifiedName() == false);
    Assert.assertTrue(f.isModifiedPrim() == false);
    Assert.assertTrue(f.isModifiedReq() == false);
    Assert.assertTrue(f.isModifiedType() == false);
    Assert.assertTrue(f.getOldName().equals(f.getName()));

    f = new ViewField(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    f.setAsNewCreated();
    fieldInterfaceTest(t, f);
  }

  private void fieldInterfaceTest(final Table t, final Field f) {
    Assert.assertTrue(TestDatamodel.fieldName.equals(f.getName()));
    Assert.assertTrue(f.getNodeOf().equals(t));
    Assert.assertTrue(f.getTypeOfData().equals(TestDatamodel.fieldDatatype));
    Assert.assertTrue(f.isRequired() == TestDatamodel.fieldIsNull);
    // assertTrue(f.getAltName().equals(""));

    f.setName(TestDatamodel.fieldName2);
    // f.setAltName(fieldName);
    f.setPartOfPrimaryKey(true);
    f.setRequired(!TestDatamodel.fieldIsNull);
    f.setTypeOfData(TestDatamodel.fieldDatatype2);

    Assert.assertTrue(TestDatamodel.fieldName2.equals(f.getName()));
    Assert.assertTrue(f.getTypeOfData().equals(TestDatamodel.fieldDatatype2));
    Assert.assertTrue(f.isRequired() == !TestDatamodel.fieldIsNull);
    // assertTrue(f.getAltName().equals(fieldName));
    Assert.assertTrue(f.isPartOfPrimaryKey() == true);
  }

  @Test
  public void testIndexImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    indexInterfaceTest(t, i, f, f2);
  }

  @Test
  public void testViewIndex() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName,
        TestDatamodel.fieldDatatype, TestDatamodel.fieldIsNull, t), t);

    Assert.assertTrue(!i.isModifiedDatafields());
    Assert.assertTrue(!i.isModifiedName());
    Assert.assertTrue(!i.isModifiedUnique());
    Assert.assertTrue(!i.isNewCreated());

    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    indexInterfaceTest(t, i, f, f2);
    Assert.assertTrue(i.isModifiedDatafields());
    Assert.assertTrue(i.isModifiedName());
    Assert.assertTrue(i.isModifiedUnique());
    i.saved();
    Assert.assertTrue(!i.isModifiedDatafields());
    Assert.assertTrue(!i.isModifiedName());
    Assert.assertTrue(!i.isModifiedUnique());
    Assert.assertTrue(i.fieldListStringProperty() != null);
    Assert.assertTrue(i.uniqueProperty() != null);
    i.setName(TestDatamodel.indexName2);
    i.setName(TestDatamodel.indexName);
    Assert.assertTrue(i.isModifiedName());
  }

  @Test
  public void testViewIndex2() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);

    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    t.addField(f);
    t.addField(f2);
    final Index indeximpl = new IndexImpl(TestDatamodel.indexName, Type.XAK, t);
    indeximpl.addField(f);
    indeximpl.addField(f2);
    final ViewIndex i = new ViewIndex(indeximpl, t);
    Assert.assertTrue(i.getFieldList().size() == 2);

  }

  private void indexInterfaceTest(final Table t, final Index i, final Field f, final Field f2) {
    i.addField(f2);
    i.setAltName(f2.getName(), TestDatamodel.fieldName2);
    Assert.assertTrue(i.getAltName(f2.getName()).equals(TestDatamodel.fieldName2));
    Assert.assertTrue(i.getOrder(f2.getName()).equals(TestDatamodel.order));
    i.addField(f);
    Assert.assertTrue(i.getOrder(f.getName()).equals(TestDatamodel.order2));
    i.removeField(0);
    Assert.assertTrue(i.getOrder(f.getName()).equals(TestDatamodel.order));

    Assert.assertTrue(TestDatamodel.indexName.equals(i.getName()));
    Assert.assertTrue(i.getNodeOf().equals(t));
    Assert.assertTrue(i.getType() == Type.XAK);
    Assert.assertTrue(i.isUnique() == true);
    Assert.assertTrue(i.getFieldList().contains(f));

    i.setUnique(false);
    Assert.assertTrue(i.isUnique() == false);
    Assert.assertTrue(i.getType() == Type.XIE);
    Assert.assertTrue(i.getName().equals(TestDatamodel.indexName2));

    i.setUnique(true);
    Assert.assertTrue(i.isUnique() == true);
    Assert.assertTrue(i.getType() == Type.XAK);
    Assert.assertTrue(i.getName().equals(TestDatamodel.indexName));

    i.setType(Type.XIF);
    Assert.assertTrue(i.getType() == Type.XIF);
    Assert.assertTrue(i.isUnique() == false);

    i.setType(Type.XAK);
    Assert.assertTrue(i.getType() == Type.XAK);
    Assert.assertTrue(i.isUnique() == true);

    i.setType(Type.XIE);
    Assert.assertTrue(i.getType() == Type.XIE);
    Assert.assertTrue(i.isUnique() == false);

    final Type p = i.getType();
    i.setName(TestDatamodel.indexNameShort);
    i.setUnique(true);
    Assert.assertTrue(TestDatamodel.indexNameShort.equals(i.getName()));
    Assert.assertTrue(i.getType() == p);
    Assert.assertTrue(i.isUnique() == false);

    i.setName(TestDatamodel.indexName2);
    Assert.assertTrue(TestDatamodel.indexName2.equals(i.getName()));
    Assert.assertTrue(i.getType() == Type.XIE);
    Assert.assertTrue(i.isUnique() == false);

    i.setName(TestDatamodel.indexName3);
    Assert.assertTrue(TestDatamodel.indexName3.equals(i.getName()));
    Assert.assertTrue(i.getType() == Type.XIF);

    i.setName(TestDatamodel.foreignKeyName);
    Assert.assertTrue(TestDatamodel.foreignKeyName.equals(i.getName()));
    Assert.assertTrue(i.getType() == Type.R);
  }

  @Test
  public void testForeignKeyImpl() {
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Table rt = new TableImpl(TestDatamodel.tableName2);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIF, t);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);

    Assert.assertTrue(fk.getFieldList().isEmpty());

    fkInterfaceTest(rt, i, f, fk);
  }

  private void fkInterfaceTest(final Table rt, final Index i, final Field f, final ForeignKey fk) {
    fk.setRefTable(rt);
    fk.setIndex(i);
    fk.addField(f);
    Assert.assertTrue(fk.getRefTable().equals(rt));
    Assert.assertTrue(fk.getIndex().equals(i));
    Assert.assertTrue(fk.getFieldList().contains(f));
    fk.removeField(0);
    Assert.assertTrue(!fk.getFieldList().contains(f));
    fk.addField(f, TestDatamodel.fieldName2);
    Assert.assertTrue(fk.getAltName(f.getName()).equals(TestDatamodel.fieldName2));
    fk.setAltName(f.getName(), TestDatamodel.tableName2);
    Assert.assertTrue(fk.getAltName(f.getName()).equals(TestDatamodel.tableName2));
    Assert.assertTrue(fk.getOrder(f.getName()).equals(TestDatamodel.order));
    Assert.assertTrue(fk.getFieldList().contains(f));
    fk.clearFieldList();
    Assert.assertTrue(!fk.getFieldList().contains(f));
  }

  @Test
  public void testViewForeignKey() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName,
        TestDatamodel.fieldDatatype, TestDatamodel.fieldIsNull, t), t);
    final ForeignKey fkOriginal = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fkOriginal.setRefTable(rt);
    fkOriginal.setIndex(i);

    ViewForeignKey fk = new ViewForeignKey(fkOriginal, t);
    Assert.assertTrue(!fk.isModified());
    fk.addField(f);
    Assert.assertTrue(fk.fieldListStringProperty() != null);
    Assert.assertTrue(fk.getRefTable().equals(rt));
    Assert.assertTrue(fk.getIndex().equals(i));
    Assert.assertTrue(fk.getRefTableName() != null);

    fk = new ViewForeignKey(TestDatamodel.foreignKeyName, t);
    Assert.assertTrue(!fk.isModified());
    fk.setRefTable(rt);
    fk.setIndex(i);
    Assert.assertTrue(fk.getVRefTable().equals(rt));
    Assert.assertTrue(fk.getVIndex().equals(i));
    Assert.assertTrue(fk.isModified());
    fk.saved();
    Assert.assertTrue(!fk.isModified());
    fk.setModified();
    Assert.assertTrue(fk.isModified());
    fk.clearFieldList();
    Assert.assertTrue(fk.equals(fkOriginal));
    fkInterfaceTest(rt, i, f, fk);
  }

  @Test
  public void testDataModelManagerTable() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save save = new SaveImpl(dmm_inside, new DatabaseManager(new DatabaseMockConnector()));
    Assert.assertTrue(dmm_inside.isEmpty());
    Assert.assertTrue(save.getTable(TestDatamodel.tableName) == null);
    Table t = new TableImpl(TestDatamodel.tableName);
    t.setXpk(new IndexImpl(Strings.INDEXTYPE_XPK + TestDatamodel.tableName, t));
    save.insertNewTable(t);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName).equals(t));
    save.getTable(TestDatamodel.tableName);
    dmm_inside.changeTableName(TestDatamodel.tableName, TestDatamodel.tableName2,
        Strings.RELNAME_XPK + TestDatamodel.tableName2);
    save.changeTableCategory(TestDatamodel.tableName2, TestDatamodel.kategory);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).equals(t));
    Assert.assertTrue(
        save.getTable(TestDatamodel.tableName2).getCategory().equals(TestDatamodel.kategory));
    final List<Table> tbl = new ArrayList<>();
    tbl.add(new TableImpl(TestDatamodel.tableName2));
    dmm_inside.addAll(tbl);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).equals(t));
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).getData().size() == 0);
    dmm_inside.clear();
    Assert.assertTrue(dmm_inside.isEmpty());
    t = new TableImpl(TestDatamodel.tableName);
    final Table tt = new TableImpl(TestDatamodel.tableName2);
    final List<Table> lt = new ArrayList<>();
    lt.add(t);
    lt.add(tt);
    dmm_inside.addAll(lt);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).equals(tt));
    Assert.assertTrue(save.getTable(TestDatamodel.tableName).equals(t));
    Assert.assertTrue(dmm_inside.getTables().contains(t));
    Assert.assertTrue(dmm_inside.getTables().contains(tt));

    save.deleteNode(tt);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2) == null);
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
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).getIndizies().size() > 0);
    Assert.assertTrue(
        save.getTable(TestDatamodel.tableName2).getIndizies().get(0).isUnique() == true);
    save.changeIndexUnique(TestDatamodel.indexName, TestDatamodel.tableName2, false);
    Assert.assertTrue(
        save.getTable(TestDatamodel.tableName2).getIndizies().get(0).isUnique() == false);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).getIndizies().size() > 0);
    save.changeNodeNameFromTable(TestDatamodel.indexName2, TestDatamodel.tableName2,
        TestDatamodel.indexName);
    save.deleteNode(save.getTable(TestDatamodel.tableName2).getIndizies().get(0));
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).getIndizies().size() == 0);
    save.insertNewIndex(i);
    save.deleteNode(i);
    Assert.assertTrue(save.getTable(TestDatamodel.tableName2).getIndizies().size() == 0);
  }

  @Test
  public void testDataModelManagerField() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save dmm = new SaveImpl(dmm_inside, new DatabaseManager(new DatabaseMockConnector()));
    final Table t = new TableImpl(TestDatamodel.tableName2);
    dmm.insertNewTable(t);

    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    dmm.insertNewField(f);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getData().size() > 0);
    dmm.changeNodeNameFromTable(TestDatamodel.fieldName, TestDatamodel.tableName2,
        TestDatamodel.fieldName2);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getData().get(0).getName()
        .equals(TestDatamodel.fieldName2));
    dmm.changeFieldTypeOfData(TestDatamodel.fieldName2, TestDatamodel.tableName2,
        TestDatamodel.fieldDatatype2);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getData().get(0).getTypeOfData()
        .equals(TestDatamodel.fieldDatatype2));
    dmm.changeFieldRequired(TestDatamodel.fieldName2, TestDatamodel.tableName2,
        !TestDatamodel.fieldIsNull);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getData().get(0)
        .isRequired() == !TestDatamodel.fieldIsNull);
    dmm.deleteNode(dmm.getTable(TestDatamodel.tableName2).getData().get(0));
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getData().size() == 0);
    dmm.insertNewField(f);
    Assert.assertTrue(!dmm.getTable(TestDatamodel.tableName2).getXpk().getFieldList().contains(f));
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, true);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getXpk().getFieldList().contains(f));
    dmm.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, false);
    Assert.assertTrue(!dmm.getTable(TestDatamodel.tableName2).getXpk().getFieldList().contains(f));
    dmm.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, false);
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, true);
    dmm_inside.changeFieldIsPartOfPrim(TestDatamodel.fieldName2, TestDatamodel.tableName2, true);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getXpk().getFieldList().contains(f));
    dmm.deleteNode(f);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getData().size() == 0);
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
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getForeignKeys().size() > 0);
    dmm.changeFkRelations(TestDatamodel.foreignKeyName, TestDatamodel.tableName2,
        TestDatamodel.tableName, TestDatamodel.indexName);
    Assert.assertTrue(
        dmm.getTable(TestDatamodel.tableName2).getForeignKeys().get(0).getRefTable().equals(tt));
    dmm.changeNodeNameFromTable(TestDatamodel.foreignKeyName, TestDatamodel.tableName2,
        TestDatamodel.fieldName);
    dmm.deleteNode(dmm.getTable(TestDatamodel.tableName2).getForeignKeys().get(0));
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getForeignKeys().size() == 0);
    dmm.insertNewForeignKey(fk);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getForeignKeys().size() > 0);
    dmm.deleteNode(fk);
    Assert.assertTrue(dmm.getTable(TestDatamodel.tableName2).getForeignKeys().size() == 0);
  }

  @Test
  public void testDataModelManagerTableField() {
    final DataModelManager dmm_inside = new DataModelManager();
    final Save dmm = new SaveImpl(dmm_inside, new DatabaseManager(new DatabaseMockConnector()));
    final Table t = new TableImpl(TestDatamodel.tableName);
    dmm.insertNewTable(t);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIE, t);
    dmm.insertNewIndex(i);
    final Field f = new FieldImpl(TestDatamodel.fieldName, TestDatamodel.fieldDatatype,
        TestDatamodel.fieldIsNull, t);
    dmm.insertNewField(f);
    final Field f2 = new FieldImpl(TestDatamodel.fieldName2, TestDatamodel.fieldDatatype2,
        TestDatamodel.fieldIsNull, t);
    dmm.insertNewField(f2);
    new ArrayList<>();
    i.clearFieldList();
    i.addField(f2);
    dmm.changeDataFields(i);
    Assert.assertTrue(
        !dmm.getTable(TestDatamodel.tableName).getIndizies().get(0).getFieldList().contains(f));
    Assert.assertTrue(
        dmm.getTable(TestDatamodel.tableName).getIndizies().get(0).getFieldList().contains(f2));
    i.clearFieldList();
    i.addField(f);
    dmm.changeDataFields(i);
    Assert.assertTrue(
        dmm.getTable(TestDatamodel.tableName).getIndizies().get(0).getFieldList().contains(f));
    Assert.assertTrue(
        !dmm.getTable(TestDatamodel.tableName).getIndizies().get(0).getFieldList().contains(f2));
  }

  @Test
  public void testIndexEnum() {
    Assert.assertTrue(Index.Type.values().length == 5);
    Assert.assertTrue(Index.Type.valueOf("XPK").compareTo(Index.Type.XPK) == 0);
  }

  @Test
  public void testComparing() {
    final Field f = new FieldImpl(TestDatamodel.fieldName);
    final Table t = new TableImpl(TestDatamodel.tableName);
    final Index i = new IndexImpl(TestDatamodel.indexName, Type.XIE, t);
    final ForeignKey fk = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);

    Assert.assertTrue(f.hashCode() != i.hashCode());
    Assert.assertTrue(t.hashCode() != fk.hashCode());
    Assert.assertTrue(!f.equals(t));
    Assert.assertTrue(!i.equals(t));
    Assert.assertTrue(!t.equals(fk));
    Assert.assertTrue(!fk.equals(f));
  }

  @Test
  public void testComparingView() {
    final ViewTable t = new ViewTable(TestDatamodel.tableName);
    final ViewTable rt = new ViewTable(TestDatamodel.tableName2);
    final ViewIndex i = new ViewIndex(new IndexImpl(TestDatamodel.indexName, Type.XAK, t), t);
    final ViewField f = new ViewField(new FieldImpl(TestDatamodel.fieldName,
        TestDatamodel.fieldDatatype, TestDatamodel.fieldIsNull, t), t);
    final ForeignKey fkOriginal = new ForeignKeyImpl(TestDatamodel.foreignKeyName, t);
    fkOriginal.setRefTable(rt);
    fkOriginal.setIndex(i);
    final ViewForeignKey fk = new ViewForeignKey(fkOriginal, t);
    Assert.assertTrue(f.hashCode() != i.hashCode());
    Assert.assertTrue(t.hashCode() != fk.hashCode());
    Assert.assertTrue(!f.equals(t));
    Assert.assertTrue(!i.equals(t));
    Assert.assertTrue(!t.equals(fk));
    Assert.assertTrue(!fk.equals(f));
  }
}
