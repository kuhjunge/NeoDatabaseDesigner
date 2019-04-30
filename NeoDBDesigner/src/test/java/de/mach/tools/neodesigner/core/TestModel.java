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
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Index.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.inex.cypher.DatabaseMockConnector;


class TestModel {
  private static final String tableName = "Testtabelle";
  private static final String indexName = "XAK1TestIndex";
  private static final String indexName2 = "XAK3TestIndex";
  private static final String indexName3 = "XAK2TestIndex";
  private static final String indexName4 = "XAK4TestIndex";
  private static final String indexName5 = "XAK5TestIndex";
  private static final String indexName6 = "XAK6TestIndex";
  private static final String indexName7 = "XAK7TestIndex";
  private static final String indexName8 = "XAK8TestIndex";
  private static final String indexName9 = "XAK9TestIndex";
  private static final String indexName10 = "XAK10TestIndex";

  @Test
  void testModelGeneral() {
    final Model m = new ModelImpl(new MockConfigSaver(), new MockConfigSaver());
    assertEquals(0, m.getSaveObj().getTables().size());
    m.connect();
    final Table t = m.getNewTable("Name");
    assertEquals("Name", t.getName());
    final Save s = m.getSaveObj();
    assertNotNull(s);
    s.insertNewTable(t);
    final Table res = m.getTable("Name").get();
    assertEquals(res, t);
    m.getListWithTableNames();
    assertEquals(101, m.getNextFkNumber(0));
    m.deleteDatabase();
    assertTrue(!m.getTable("Name").isPresent());
    m.deleteDatabase();
    assertTrue(!m.getTable("Name").isPresent());
    m.importTables(TestUtil.getTableList());
    assertTrue(m.getTable("Table1").isPresent());
    assertEquals(5, m.getAllTables().size());
    assertNotNull(m.getValidator());
    m.saveCategoryList(TestUtil.getCatList());
    assertEquals("[(0,0) Cat0(id: 0 sid: 1), (1) Cat1(id: 0 sid: 2), (1,1) Cat1a(id: 0 sid: 3), (1,2) Cat1b(id: 0 sid: 4), (1,3) "
                 + "Cat1c(id: 0 sid: 5), (1,4) Cat1d(id: 0 sid: 6), (1,5) Cate(id: 0 sid: 7)]",
                 m.getCategorySelection().toString());
  }

  @Test
  void testFkNumberGenerator() {
    final Model m = new ModelImpl(new MockConfigSaver(), new MockConfigSaver());
    assertEquals(101, m.getNextFkNumber(0));
    assertEquals(103, m.getNextFkNumber(102));
    // Testtabelle aufbauen mit einen FK
    Table t = new TableImpl("test");
    t.addField(new FieldImpl("reffield"));
    t.addIndex(new IndexImpl("XIF123", t));
    t.addForeignKey(new ForeignKeyImpl("R_222", t), false);
    Table rt = new TableImpl("testRefTable");
    rt.addField(new FieldImpl("reffield"));
    t.getForeignKeys().get(0).setIndex(t.getIndizies().get(0));
    t.getForeignKeys().get(0).setRefTable(rt);
    m.importTables(Arrays.asList(t, rt));
    assertEquals(223, m.getNextFkNumber(220));
    assertEquals(224, m.getNextFkNumber(223));
    assertEquals(1001, m.getNextFkNumber(991));
    assertEquals(10001, m.getNextFkNumber(9991));
  }

  @Test
  void testModelGeneral2() {
    final DatabaseMockConnector dmc = new DatabaseMockConnector();
    final Model m = new ModelImpl(new MockConfigSaver(), new MockConfigSaver());
    dmc.addr = m.getAddrOfDb();
    dmc.nutzername = m.getGuiConf().getUser();
    dmc.pw = m.getGuiConf().getPw();
    m.connect();
    assertNotNull(m.getGuiConf().getPathImportSql());
    assertNotNull(m.getGuiConf().getPathImportCat());
    assertNotNull(m.getGuiConf().getPathImportCsv());
    assertNotNull(m.getGuiConf().getPathExportSql());
    assertNotNull(m.getGuiConf().getPathExportCsv());
    assertNotNull(m.getGuiConf().getPathExportGeneric());
  }

  @Test
  void testModelGetConfig() {
    final Model m = new ModelImpl(new MockConfigSaver(), new MockConfigSaver());
    final Configuration config = new Configuration();
    config.init(new MockConfigSaver());
    assertEquals(m.getAddrOfDb(), config.getAddrOfDb());
    assertEquals(m.getGuiConf().getPw(), config.getPw());
    assertEquals(m.getGuiConf().getUser(), config.getUser());
    assertArrayEquals(m.getSelectDatatype(), config.getSelectDataType());
    assertEquals(m.getWordLength(), config.getWordLength());
  }

  @Test
  void testModelGetIndexNumber() {
    final Table t = new TableImpl(TestModel.tableName);
    final List<Index> li = new ArrayList<>();
    li.add(new IndexImpl(TestModel.indexName, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName2, Type.XAK, t));
    final Model m = new ModelImpl(new MockConfigSaver(), new MockConfigSaver());
    assertEquals(2, (int) m.getNextNumberForIndex(li));
  }

  @Test
  void testModelGetIndexNumber2() {
    final Table t = new TableImpl(TestModel.tableName);
    final List<Index> li = new ArrayList<>();
    li.add(new IndexImpl(TestModel.indexName, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName2, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName3, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName4, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName5, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName6, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName7, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName8, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName9, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName10, Type.XAK, t));
    final Model m = new ModelImpl(new MockConfigSaver(), new MockConfigSaver());
    assertEquals(11, (int) m.getNextNumberForIndex(li));
  }

  @Test
  void testNameValidation() {
    final Save save = new DataModelManager();
    final Validator v = new Validator(18, 18, 15, save);
    assertTrue(v.validateName("TestName"));
    assertEquals("", v.getLastError());
    assertTrue(!v.validateName("CONSTRAINT"));
    assertTrue(!v.getLastError().equals(""));
    assertTrue(v.validateName("666HellOfATable"));
    assertTrue(!v.validateName("???"));
    assertTrue(!v.getLastError().equals(""));
    assertTrue(!v.validateName("ThisIsAVeryLongTableNameThatShouldNeverBeUsed"));
    assertTrue(!v.getLastError().equals(""));
  }
  /* @Test public void testSqlImportTask() { final ModelImpl m = new ModelImpl(new DatabaseMockConnector(), new
   * CategoryTranslator()); final ImportTask it = m.importTask("", 's', "", new DatabaseManagerLean(new
   * DatabaseMockConnector())); final Thread t = new Thread(it); t.start(); try { t.join(); assertTrue(!t.isAlive());
   * assertTrue(it.isDone()); } catch (final InterruptedException e) { fail("couldn't wait for Process");
   * e.printStackTrace(); } } */

  @Test
  void testTableValidation() {
    final Save save = new DataModelManager();
    final Validator v = new Validator(18, 18, 15, save);
    Table t = new TableImpl("Table");
    t.setCategory("1,");
    final Field f = new FieldImpl("Feld", DomainId.STRING, 20, true, "", t);
    final Field f2 = new FieldImpl("FeldZwei", DomainId.AMOUNT, 0, true, "", t);
    final Field f3 = new FieldImpl("FeldDrei", DomainId.DATE, 0, false, "", t);
    final Index i2 = new IndexImpl("XIE100TestIndex", t);
    final Index xpk = new IndexImpl("XPKTable", t);
    xpk.addField(f2);
    t.addField(f);
    t.addField(f2);
    t.addField(f3);
    final Index i = new IndexImpl("XIF100BlaBlub", t);
    i.addField(f);
    i2.addField(f);
    ForeignKey fk = new ForeignKeyImpl("R_100", t);
    fk.setIndex(i);
    fk.setRefTable(t);
    t.getIndizies().add(i2);
    t.getIndizies().add(i);
    t.getForeignKeys().add(fk);
    t.setXpk(xpk);
    assertTrue(v.validateTable(t, false));
    assertEquals("", v.getLastError());
    t = new TableImpl("Tab!le");
    fk = new ForeignKeyImpl("R_100", t);
    fk.setRefTable(null);
    fk.setIndex(i2);
    fk.getIndex().clearFieldList();
    t.getForeignKeys().add(fk);
    assertTrue(!v.validateTable(t, true));
    assertTrue(!v.getLastError().equals(""));
  }

  @Test
  void testUtilIsInteger() {
    assertTrue(Util.isInteger("0"));
    assertTrue(Util.isInteger("-1"));
    assertFalse(Util.isInteger("-"));
    assertTrue(Util.isInteger("-999"));
    assertTrue(Util.isInteger("999"));
    assertFalse(Util.isInteger("a"));
    assertFalse(Util.isInteger("1,"));
    assertFalse(Util.isInteger(".6"));
    assertFalse(Util.isInteger("99A99"));
    assertFalse(Util.isInteger(""));
    assertFalse(Util.isInteger(null));
  }

  @Test
  void testUtilTryToParseInt() {
    assertEquals(0, Util.tryParseInt("0"));
    assertEquals(Util.tryParseInt("-1"), -1);
    assertEquals(0, Util.tryParseInt("-"));
    assertEquals(Util.tryParseInt("-999"), -999);
    assertEquals(999, Util.tryParseInt("999"));
    assertEquals(0, Util.tryParseInt("a"));
    assertEquals(0, Util.tryParseInt("1,"));
    assertEquals(0, Util.tryParseInt(".6"));
    assertEquals(0, Util.tryParseInt("99A99"));
    assertEquals(0, Util.tryParseInt(""));
    assertEquals(0, Util.tryParseInt(null));
  }
}
