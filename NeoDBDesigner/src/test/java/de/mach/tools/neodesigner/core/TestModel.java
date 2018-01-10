package de.mach.tools.neodesigner.core;

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
import de.mach.tools.neodesigner.database.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.database.DatabaseManager;
import de.mach.tools.neodesigner.database.DatabaseMockConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import javafx.concurrent.Task;
import org.junit.Assert;
import org.junit.Test;

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
	public void testModelGeneral() {
		final DatabaseMockConnector dmc = new DatabaseMockConnector();
		final Model m = new ModelImpl(dmc);
		dmc.addr = m.getAddrOfDb();
		dmc.nutzername = m.getUser();
		dmc.pw = m.getPw();
		Assert.assertTrue(m.getAllLocalTables().size() == 0);
		Assert.assertTrue(!m.isOnline());
		m.connectDb(m.getAddrOfDb(), m.getPw(), m.getUser());
		Assert.assertTrue(!m.isOnline());
		m.connectDb(m.getAddrOfDb(), m.getUser(), m.getPw());
		Assert.assertTrue(m.isOnline());
		final Observable o = m.dataModelObservable();
		Assert.assertTrue(o != null);
		final Table t = m.getnewTable("Name");
		Assert.assertTrue("Name".equals(t.getName()));
		final Save s = m.getSaveObj();
		Assert.assertTrue(s != null);
		s.insertNewTable(t);
		Table res = m.getTable("Name");
		Assert.assertTrue(res.equals(t));
		m.getListWithTableNames();
		Assert.assertTrue(
				"MATCH(t:Table) RETURN t.name AS name, t.category AS category ORDER BY name".equals(dmc.lastQuery));
		Assert.assertTrue(m.getNextFkNumber() == 101);
		m.disconnectDb();
		Assert.assertTrue(!m.isOnline());
		res = m.getTable("Name");
		Assert.assertTrue(res == null);
		m.connectDb(m.getAddrOfDb(), m.getUser(), m.getPw());
		m.deleteDatabase();
		res = m.getTable("Name");
		Assert.assertTrue(res == null);

	}

	@Test
	public void testLoadFromDb() {
		final DatabaseMockConnector dmc = new DatabaseMockConnector();
		final Model m = new ModelImpl(dmc);
		try {
			final Task<Boolean> t = m.loadFrmDbTask();
			final Thread th = new Thread(t);
			th.start();
			th.join();
			Assert.assertTrue(t.isDone());
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail("task failed");
		}
	}

	@Test
	public void testModelGetConfig() {
		final Model m = new ModelImpl(new DatabaseConnectorImpl());
		final Configuration config = new Configuration();
		config.init();
		Assert.assertTrue(m.getAddrOfDb().equals(config.getAddrOfDb()));
		Assert.assertTrue(m.getPw().equals(config.getPw()));
		Assert.assertTrue(m.getUser().equals(config.getUser()));
		Assert.assertTrue(Arrays.equals(m.getSelectDatatype(), config.getSelectDataType()));
		Assert.assertTrue(m.getWordLength() == config.getWordLength());

	}

	@Test
	public void testModelGetIndexNumber() {
		final Table t = new TableImpl(TestModel.tableName);
		final List<Index> li = new ArrayList<>();
		li.add(new IndexImpl(TestModel.indexName, Type.XAK, t));
		li.add(new IndexImpl(TestModel.indexName2, Type.XAK, t));
		final Model m = new ModelImpl(new DatabaseConnectorImpl());
		Assert.assertTrue(m.getNextNumberForIndex(li) == 2);
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
		final Model m = new ModelImpl(new DatabaseConnectorImpl());
		Assert.assertTrue(m.getNextNumberForIndex(li) == 11);
	}

	@Test
	public void testNameValidation() {
		final Save save = new DataModelManager();
		final Validator v = new Validator(18, 18, save);
		Assert.assertTrue(v.validateName("TestName"));
		Assert.assertTrue(v.getLastError().equals(""));
		Assert.assertTrue(!v.validateName("CONSTRAINT"));
		Assert.assertTrue(!v.getLastError().equals(""));
		Assert.assertTrue(v.validateName("666HellOfATable"));
		Assert.assertTrue(!v.validateName("???"));
		Assert.assertTrue(!v.getLastError().equals(""));
		Assert.assertTrue(!v.validateName("ThisIsAVeryLongTableNameThatShouldNeverBeUsed"));
		Assert.assertTrue(!v.getLastError().equals(""));
	}

	@Test
	public void testTableValidation() {
		final Save save = new DataModelManager();
		final Validator v = new Validator(18, 18, save);
		Table t = new TableImpl("Table");
		t.setCategory("1,");
		final Field f = new FieldImpl("Feld", "VARCHAR2(50)", true, t);
		final Field f2 = new FieldImpl("FeldZwei", "INTEGER", true, t);
		final Field f3 = new FieldImpl("FeldDrei", "DATE", false, t);
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
		Assert.assertTrue(v.validateTable(t, false));
		Assert.assertTrue(v.getLastError().equals(""));
		t = new TableImpl("Tab!le");
		fk = new ForeignKeyImpl("R_100", t);
		fk.setRefTable(null);
		fk.setIndex(i2);
		fk.clearFieldList();
		t.getForeignKeys().add(fk);
		Assert.assertTrue(!v.validateTable(t, true));
		Assert.assertTrue(!v.getLastError().equals(""));
	}

	@Test
	public void testCsvImportTask() {
		final List<String> lines = new ArrayList<>();
		lines.add("\"test\", \"1,2\"");
		final ModelImpl m = new ModelImpl(new DatabaseMockConnector());
		final ImportTask it = m.importTask(lines, 'c', new DatabaseManager(new DatabaseMockConnector()));
		final Thread t = new Thread(it);
		t.start();
		try {
			t.join();
			Assert.assertTrue(!t.isAlive());
			Assert.assertTrue(it.isDone());
		} catch (final InterruptedException e) {
			Assert.fail("couldn't wait for Process");
			e.printStackTrace();
		}
	}

	@Test
	public void testSqlImportTask() {
		final List<String> lines = new ArrayList<>();
		lines.add("");
		final ModelImpl m = new ModelImpl(new DatabaseMockConnector());
		final ImportTask it = m.importTask(lines, 's', new DatabaseManager(new DatabaseMockConnector()));
		final Thread t = new Thread(it);
		t.start();
		try {
			t.join();
			Assert.assertTrue(!t.isAlive());
			Assert.assertTrue(it.isDone());
		} catch (final InterruptedException e) {
			Assert.fail("couldn't wait for Process");
			e.printStackTrace();
		}
	}
}
