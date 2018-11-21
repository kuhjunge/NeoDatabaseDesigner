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
import java.util.Observable;
import javafx.concurrent.Task;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.category.CategoryTranslator;
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
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.database.cypher.DatabaseManagerLean;
import de.mach.tools.neodesigner.database.cypher.DatabaseMockConnector;
import de.mach.tools.neodesigner.database.local.DataModelManager;


public class TestModel {
  public static final String tableName = "Testtabelle";
  public static final String indexName = "XAK1TestIndex";
  public static final String indexName2 = "XAK3TestIndex";
  public static final String indexName3 = "XAK2TestIndex";
  public static final String indexName4 = "XAK4TestIndex";
  public static final String indexName5 = "XAK5TestIndex";
  public static final String indexName6 = "XAK6TestIndex";
  public static final String indexName7 = "XAK7TestIndex";
  public static final String indexName8 = "XAK8TestIndex";
  public static final String indexName9 = "XAK9TestIndex";
  public static final String indexName10 = "XAK10TestIndex";

  @Test
  public void testCsvImportTask() {
    final ModelImpl m = new ModelImpl(new DatabaseMockConnector(), new CategoryTranslator());
    final ImportTask it = m.importTask("\"test\", \"1,2\"", 'c', "",
                                       new DatabaseManagerLean(new DatabaseMockConnector()));
    final Thread t = new Thread(it);
    t.start();
    try {
      t.join();
      assertTrue(!t.isAlive());
      assertTrue(it.isDone());
    }
    catch (final InterruptedException e) {
      fail("couldn't wait for Process");
      e.printStackTrace();
    }
  }

  @Test
  public void testLoadFromDb() {
    final DatabaseMockConnector dmc = new DatabaseMockConnector();
    final Model m = new ModelImpl(dmc, new CategoryTranslator());
    try {
      final Task<List<Table>> t = m.loadFromDbTask();
      final Thread th = new Thread(t);
      th.start();
      th.join();
      assertTrue(t.isDone());
    }
    catch (final Exception e) {
      e.printStackTrace();
      fail("task failed");
    }
  }

  @Test
  public void testModelGeneral() {
    final DatabaseMockConnector dmc = new DatabaseMockConnector();
    final Model m = new ModelImpl(dmc, new MockCategoryTranslator());
    dmc.addr = m.getAddrOfDb();
    dmc.nutzername = m.getGuiConf().getUser();
    dmc.pw = m.getGuiConf().getPw();
    assertTrue(m.getSaveObj().getTables().size() == 0);
    assertTrue(!m.isOnline());
    m.connectDb(m.getAddrOfDb(), m.getGuiConf().getPw(), m.getGuiConf().getUser() + m.getGuiConf().getPw());
    assertTrue(!m.isOnline());
    m.connectDb(m.getAddrOfDb(), m.getGuiConf().getUser(), m.getGuiConf().getPw());
    assertTrue(m.isOnline());
    final Observable o = m.dataModelObservable();
    assertTrue(o != null);
    final Table t = m.getnewTable("Name");
    assertTrue("Name".equals(t.getName()));
    final Save s = m.getSaveObj();
    assertTrue(s != null);
    s.insertNewTable(t);
    final Table res = m.getTable("Name").get();
    assertTrue(res.equals(t));
    m.getListWithTableNames();
    assertTrue("MATCH(t:Table) RETURN t.name AS name ORDER BY name".equals(dmc.lastQuery));
    assertTrue(m.getNextFkNumber(0) == 101);
    m.disconnectDb();
    assertTrue(!m.isOnline());
    assertTrue(!m.getTable("Name").isPresent());
    m.connectDb(m.getAddrOfDb(), m.getGuiConf().getUser(), m.getGuiConf().getPw());
    m.deleteDatabase();
    assertTrue(!m.getTable("Name").isPresent());
    m.addTableList(TestUtil.getTableList());
    assertTrue(m.getTable("Table1").isPresent());
    assertTrue(m.getAllTables().size() == 5);
    assertTrue(m.getValidator() != null);
    m.saveCategoryList(TestUtil.getCatList());
    assertTrue(m.getCategorySelection().toString()
        .equals("[(0,0) Cat0(id: 0 sid: 1), (1) Cat1(id: 0 sid: 2), (1,1) Cat1a(id: 0 sid: 3), (1,2) Cat1b(id: 0 sid: 4), (1,3) "
                + "Cat1c(id: 0 sid: 5), (1,4) Cat1d(id: 0 sid: 6), (1,5) Cate(id: 0 sid: 7)]"));
  }

  @Test
  public void testModelGeneral2() {
    final DatabaseMockConnector dmc = new DatabaseMockConnector();
    final Model m = new ModelImpl(dmc, new MockCategoryTranslator());
    dmc.addr = m.getAddrOfDb();
    dmc.nutzername = m.getGuiConf().getUser();
    dmc.pw = m.getGuiConf().getPw();
    m.connectDb(m.getAddrOfDb(), m.getGuiConf().getUser(), m.getGuiConf().getPw());
    assertTrue(m.isOnline());
    assertTrue(null != m.getGuiConf().getPathImportSql());
    assertTrue(null != m.getGuiConf().getPathImportCat());
    assertTrue(null != m.getGuiConf().getPathImportCsv());
    assertTrue(null != m.getGuiConf().getPathExportSql());
    assertTrue(null != m.getGuiConf().getPathExportCsv());
    assertTrue(null != m.getGuiConf().getPathExportCql());
  }

  @Test
  public void testModelGetConfig() {
    final Model m = new ModelImpl(new DatabaseConnectorImpl(), new CategoryTranslator());
    final Configuration config = new Configuration();
    config.init();
    assertTrue(m.getAddrOfDb().equals(config.getAddrOfDb()));
    assertTrue(m.getGuiConf().getPw().equals(config.getPw()));
    assertTrue(m.getGuiConf().getUser().equals(config.getUser()));
    assertTrue(Arrays.equals(m.getSelectDatatype(), config.getSelectDataType()));
    assertTrue(m.getWordLength() == config.getWordLength());
  }

  @Test
  public void testModelGetIndexNumber() {
    final Table t = new TableImpl(TestModel.tableName);
    final List<Index> li = new ArrayList<>();
    li.add(new IndexImpl(TestModel.indexName, Type.XAK, t));
    li.add(new IndexImpl(TestModel.indexName2, Type.XAK, t));
    final Model m = new ModelImpl(new DatabaseConnectorImpl(), new CategoryTranslator());
    assertTrue(m.getNextNumberForIndex(li) == 2);
  }

  @Test
  public void testModelGetIndexNumber2() {
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
    final Model m = new ModelImpl(new DatabaseConnectorImpl(), new CategoryTranslator());
    assertTrue(m.getNextNumberForIndex(li) == 11);
  }

  @Test
  public void testNameValidation() {
    final Save save = new DataModelManager();
    final Validator v = new Validator(18, 18, 15, save);
    assertTrue(v.validateName("TestName"));
    assertTrue(v.getLastError().equals(""));
    assertTrue(!v.validateName("CONSTRAINT"));
    assertTrue(!v.getLastError().equals(""));
    assertTrue(v.validateName("666HellOfATable"));
    assertTrue(!v.validateName("???"));
    assertTrue(!v.getLastError().equals(""));
    assertTrue(!v.validateName("ThisIsAVeryLongTableNameThatShouldNeverBeUsed"));
    assertTrue(!v.getLastError().equals(""));
  }

  @Test
  public void testSqlImportTask() {
    final ModelImpl m = new ModelImpl(new DatabaseMockConnector(), new CategoryTranslator());
    final ImportTask it = m.importTask("", 's', "", new DatabaseManagerLean(new DatabaseMockConnector()));
    final Thread t = new Thread(it);
    t.start();
    try {
      t.join();
      assertTrue(!t.isAlive());
      assertTrue(it.isDone());
    }
    catch (final InterruptedException e) {
      fail("couldn't wait for Process");
      e.printStackTrace();
    }
  }

  @Test
  public void testTableValidation() {
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
    assertTrue(v.getLastError().equals(""));
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
  public void testUtilIsInteger() {
    assertTrue(Util.isInteger("0"));
    assertTrue(Util.isInteger("-1"));
    assertTrue(!Util.isInteger("-"));
    assertTrue(Util.isInteger("-999"));
    assertTrue(Util.isInteger("999"));
    assertTrue(!Util.isInteger("a"));
    assertTrue(!Util.isInteger("1,"));
    assertTrue(!Util.isInteger(".6"));
    assertTrue(!Util.isInteger("99A99"));
    assertTrue(!Util.isInteger(""));
    assertTrue(!Util.isInteger(null));
  }

  @Test
  public void testUtilTryToParseInt() {
    assertTrue(Util.tryParseInt("0") == 0);
    assertTrue(Util.tryParseInt("-1") == -1);
    assertTrue(Util.tryParseInt("-") == 0);
    assertTrue(Util.tryParseInt("-999") == -999);
    assertTrue(Util.tryParseInt("999") == 999);
    assertTrue(Util.tryParseInt("a") == 0);
    assertTrue(Util.tryParseInt("1,") == 0);
    assertTrue(Util.tryParseInt(".6") == 0);
    assertTrue(Util.tryParseInt("99A99") == 0);
    assertTrue(Util.tryParseInt("") == 0);
    assertTrue(Util.tryParseInt(null) == 0);
  }
}
