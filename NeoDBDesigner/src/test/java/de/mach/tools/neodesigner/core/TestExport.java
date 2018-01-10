package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.nexport.CsvGenerator;
import de.mach.tools.neodesigner.core.nexport.Generator;
import de.mach.tools.neodesigner.core.nexport.SqlGenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestExport {

	public static String resultTable1SQL = " -- Table: Table0 Category: 1,0CREATE TABLE Table0 (       Feld0                VARCHAR2(50) NOT NULL,       FeldZwei0            INTEGER NOT NULL,       FeldDrei0            DATE NULL);CREATE INDEX XIE100TestIndex ON Table0(       Feld0                     ASC );CREATE INDEX XIF100BlaBlub ON Table0(       Feld0                     ASC );ALTER TABLE Table0       ADD  ( CONSTRAINT XPKTable0 PRIMARY KEY (FeldZwei0 ) ) ; -- Table: Table1 Category: 1,1CREATE TABLE Table1 (       Feld1                VARCHAR2(50) NOT NULL,       FeldZwei1            INTEGER NOT NULL,       FeldDrei1            DATE NULL);CREATE INDEX XIE101TestIndex ON Table1(       Feld1                     ASC );CREATE INDEX XIF101BlaBlub ON Table1(       Feld1                     ASC );ALTER TABLE Table1       ADD  ( CONSTRAINT XPKTable1 PRIMARY KEY (FeldZwei1 ) ) ; -- Table: Table2 Category: 1,2CREATE TABLE Table2 (       Feld2                VARCHAR2(50) NOT NULL,       FeldZwei2            INTEGER NOT NULL,       FeldDrei2            DATE NULL);CREATE INDEX XIE102TestIndex ON Table2(       Feld2                     ASC );CREATE INDEX XIF102BlaBlub ON Table2(       Feld2                     ASC );ALTER TABLE Table2       ADD  ( CONSTRAINT XPKTable2 PRIMARY KEY (FeldZwei2 ) ) ; -- Table: Table3 Category: 1,3CREATE TABLE Table3 (       Feld3                VARCHAR2(50) NOT NULL,       FeldZwei3            INTEGER NOT NULL,       FeldDrei3            DATE NULL);CREATE INDEX XIE103TestIndex ON Table3(       Feld3                     ASC );CREATE INDEX XIF103BlaBlub ON Table3(       Feld3                     ASC );ALTER TABLE Table3       ADD  ( CONSTRAINT XPKTable3 PRIMARY KEY (FeldZwei3 ) ) ; -- Table: Table4 Category: 1,4CREATE TABLE Table4 (       Feld4                VARCHAR2(50) NOT NULL,       FeldZwei4            INTEGER NOT NULL,       FeldDrei4            DATE NULL);CREATE INDEX XIE104TestIndex ON Table4(       Feld4                     ASC );CREATE INDEX XIF104BlaBlub ON Table4(       Feld4                     ASC );ALTER TABLE Table4       ADD  ( CONSTRAINT XPKTable4 PRIMARY KEY (FeldZwei4 ) ) ;ALTER TABLE Table0       ADD  ( CONSTRAINT R_100              FOREIGN KEY)                             REFERENCES Table0 ) ;ALTER TABLE Table1       ADD  ( CONSTRAINT R_101              FOREIGN KEY)                             REFERENCES Table1 ) ;ALTER TABLE Table2       ADD  ( CONSTRAINT R_102              FOREIGN KEY)                             REFERENCES Table2 ) ;ALTER TABLE Table3       ADD  ( CONSTRAINT R_103              FOREIGN KEY)                             REFERENCES Table3 ) ;ALTER TABLE Table4       ADD  ( CONSTRAINT R_104              FOREIGN KEY)                             REFERENCES Table4 ) ;";
	public static String resultCSVTable1 = "Table1,Feld1,VARCHAR2(50),NOT NULL,No,,,,,,,\"1,1\"\r\n"
			+ "Table1,FeldZwei1,INTEGER,NOT NULL,No,,,,,,,\"1,1\"\r\n"
			+ "Table1,FeldDrei1,DATE,NULL,No,,,,,,,\"1,1\"\r\n";
	public static String resultCSVTable2 = "Table3,Feld3,\"NUMBER(50,50)\",NOT NULL,Yes,,,,,,,"
			+ "\"0,0\"\r\nTable3,FeldZwei3,INTEGER,NOT NULL,No,,,,,,,\"0,0\"\r\nTable3,FeldDrei3,DATE"
			+ ",NULL,No,,,,,,,\"0,0\"";
	public static String resultTableSQLAdv = " -- Table: Table0 Category: 1,0CREATE TABLE Table0 (       Feld0                VARCHAR2(50) NOT NULL,       FeldZwei0            INTEGER NOT NULL,       FeldDrei0            DATE NULL);CREATE INDEX XIE100TestIndex ON Table0(       Feld0                     ASC );CREATE INDEX XIF100BlaBlub ON Table0(       Feld0                     ASC );ALTER TABLE Table0       ADD  ( CONSTRAINT XPKTable0 PRIMARY KEY (FeldZwei0 ) ) ; -- Table: Table1 Category: 1,1CREATE TABLE Table1 (       Feld1                VARCHAR2(50) NOT NULL,       FeldZwei1            INTEGER NOT NULL,       FeldDrei1            DATE NULL);CREATE INDEX XIE101TestIndex ON Table1(       Feld1                     ASC );CREATE INDEX XIF101BlaBlub ON Table1(       Feld1                     ASC );ALTER TABLE Table1       ADD  ( CONSTRAINT XPKTable1 PRIMARY KEY (FeldZwei1 ) ) ; -- Table: Table2 Category: 1,2CREATE TABLE Table2 (       Feld2                VARCHAR2(50) NOT NULL,       FeldZwei2            INTEGER NOT NULL,       FeldDrei2            DATE NULL);CREATE INDEX XIE102TestIndex ON Table2(       Feld2                     ASC );CREATE INDEX XIF102BlaBlub ON Table2(       Feld2                     ASC );ALTER TABLE Table2       ADD  ( CONSTRAINT XPKTable2 PRIMARY KEY (FeldZwei2 ) ) ; -- Table: Table3 Category: 1,3CREATE TABLE Table3 (       Feld3                VARCHAR2(50) NOT NULL,       FeldZwei3            INTEGER NOT NULL,       FeldDrei3            DATE NULL);CREATE INDEX XIE103TestIndex ON Table3(       Feld3                     ASC );CREATE INDEX XIF103BlaBlub ON Table3(       Feld3                     ASC );ALTER TABLE Table3       ADD  ( CONSTRAINT XPKTable3 PRIMARY KEY (FeldZwei3 ) ) ; -- Table: Table4 Category: 1,4CREATE TABLE Table4 (       Feld4                VARCHAR2(50) NOT NULL,       FeldZwei4            INTEGER NOT NULL,       FeldDrei4            DATE NULL);CREATE INDEX XIE104TestIndex ON Table4(       Feld4                     ASC );CREATE INDEX XIF104BlaBlub ON Table4(       Feld4                     ASC );ALTER TABLE Table4       ADD  ( CONSTRAINT XPKTable4 PRIMARY KEY (FeldZwei4 ) ) ;ALTER TABLE Table0       ADD  ( CONSTRAINT R_100              FOREIGN KEY)                             REFERENCES Table0 ()) ;ALTER TABLE Table1       ADD  ( CONSTRAINT R_101              FOREIGN KEY)                             REFERENCES Table1 ()) ;ALTER TABLE Table2       ADD  ( CONSTRAINT R_102              FOREIGN KEY)                             REFERENCES Table2 ()) ;ALTER TABLE Table3       ADD  ( CONSTRAINT R_103              FOREIGN KEY)                             REFERENCES Table3 ()) ;ALTER TABLE Table4       ADD  ( CONSTRAINT R_104              FOREIGN KEY)                             REFERENCES Table4 ()) ;";

	@Test
	public void testSqlGenerator() {
		final List<Table> lt = getTableList();
		final Generator sqlGen = new SqlGenerator();
		final String ret = sqlGen.generate(lt);
		Assert.assertTrue(ret.replaceAll("\r\n", "").contains(TestExport.resultTable1SQL));
	}

	@Test
	public void testAdvSqlGenerator() {
		final List<Table> lt = getTableList();
		final Generator sqlGen = new SqlGenerator(true);
		final String ret = sqlGen.generate(lt);
		Assert.assertTrue(ret.replaceAll("\r\n", "").contains(TestExport.resultTableSQLAdv));
	}

	@Test
	public void testCsvGenerator() {
		final List<Table> lt = getTableList();
		final Generator csvGen = new CsvGenerator();
		final String ret = csvGen.generate(lt);
		Assert.assertTrue(ret.contains(TestExport.resultCSVTable1));
	}

	@Test
	public void testCsvGenerator2() {
		final List<Table> lt = getTableList2();
		final Generator csvGen = new CsvGenerator();
		final String ret = csvGen.generate(lt);
		Assert.assertTrue(ret.contains(TestExport.resultCSVTable2));
	}

	private List<Table> getTableList() {
		final List<Table> lt = new ArrayList<>();
		for (int j = 0; j < 5; j++) {
			final Table t = new TableImpl("Table" + j);
			t.setCategory("1," + j);
			final Field f = new FieldImpl("Feld" + j, "VARCHAR2(50)", true, t);
			final Field f2 = new FieldImpl("FeldZwei" + j, "INTEGER", true, t);
			final Field f3 = new FieldImpl("FeldDrei" + j, "DATE", false, t);
			final Index i = new IndexImpl("XIF10" + j + "BlaBlub", t);
			final Index i2 = new IndexImpl("XIE10" + j + "TestIndex", t);
			final Index xpk = new IndexImpl("XPKTable" + j, t);
			xpk.addField(f2);
			final ForeignKey fk = new ForeignKeyImpl("R_10" + j, t);
			t.addField(f);
			t.addField(f2);
			t.addField(f3);
			i.addField(f);
			i2.addField(f);
			fk.setIndex(i);
			fk.setRefTable(t);
			t.getIndizies().add(i2);
			t.getIndizies().add(i);
			t.getForeignKeys().add(fk);
			t.setXpk(xpk);
			lt.add(t);
		}
		return lt;
	}

	private List<Table> getTableList2() {
		final List<Table> lt = new ArrayList<>();
		for (int j = 0; j < 5; j++) {
			final Table t = new TableImpl("Table" + j);
			t.setCategory("none");
			final Field f = new FieldImpl("Feld" + j, "NUMBER(50,50)", true, t);
			f.setPartOfPrimaryKey(true);
			final Field f2 = new FieldImpl("FeldZwei" + j, "INTEGER", true, t);
			final Field f3 = new FieldImpl("FeldDrei" + j, "DATE", false, t);
			final Index i = new IndexImpl("XIF10" + j + "BlaBlub", t);
			final Index i2 = new IndexImpl("XIE10" + j + "TestIndex", t);
			final Index xpk = new IndexImpl("XPKTable" + j, t);
			xpk.addField(f2);
			final ForeignKey fk = new ForeignKeyImpl("R_10" + j, t);
			t.addField(f);
			t.addField(f2);
			t.addField(f3);
			i.addField(f);
			i2.addField(f);
			fk.setIndex(i);
			fk.setRefTable(t);
			t.getIndizies().add(i2);
			t.getIndizies().add(i);
			t.getForeignKeys().add(fk);
			t.setXpk(xpk);
			lt.add(t);
		}
		return lt;
	}
}
