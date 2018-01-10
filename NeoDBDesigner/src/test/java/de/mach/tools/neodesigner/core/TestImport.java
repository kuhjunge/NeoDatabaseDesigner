package de.mach.tools.neodesigner.core;

import org.junit.Assert;
import org.junit.Test;

import de.mach.tools.neodesigner.core.nimport.ImportCategoryTask;
import de.mach.tools.neodesigner.core.nimport.ImportSqlTask;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.DatabaseManager;
import de.mach.tools.neodesigner.database.DatabaseMockConnector;

public class TestImport {
	String inputSql = "CREATE TABLE Abrechnungsobjekte (" + "       Mandant              INTEGER NOT NULL,"
			+ "       Kostenrechnung       VARCHAR2(20) NOT NULL,"
			+ "       Abrechnungsobjekt    VARCHAR2(10) NOT NULL," + "       Hausbank             INTEGER NULL,"
			+ "       UStBefreiungssatz    NUMBER(18,5) NOT NULL," + "       EndeLaufzeit         DATE NULL" + ");" + ""
			+ "ALTER TABLE Abrechnungsobjekte" + "       ADD  ( CONSTRAINT XPKKostenstellen PRIMARY KEY (Mandant, "
			+ "              Kostenrechnung, Abrechnungsobjekt) ) ;" + "			  " + "CREATE TABLE AObjHieZuo ("
			+ "       HieKR                VARCHAR2(20) NOT NULL,"
			+ "       HieAObj              VARCHAR2(10) NOT NULL," + "       HieMandant           INTEGER NOT NULL,"
			+ "       Hausbank             INTEGER NULL," + "       Mandant              INTEGER NOT NULL,"
			+ "       Kostenrechnung       VARCHAR2(20) NOT NULL," + "       Abrechnungsobjekt    VARCHAR2(10) NOT NULL"
			+ ");" + "" + "CREATE INDEX XIF1326AObjHieZuO ON AObjHieZuo" + "("
			+ "       HieMandant                     ASC," + "       HieKR                          ASC,"
			+ "       HieAObj                        ASC" + ");" + ""
			+ "CREATE UNIQUE INDEX XAK1329AObjHieZuO ON AObjHieZuo" + "(" + "       HieMandant                     ASC,"
			+ "       HieKR                          ASC," + "       HieAObj                        ASC" + ");"
			+ "CREATE INDEX XIF1327AObjHieZuO ON AObjHieZuo" + "(" + "       Mandant                        ASC,"
			+ "       Kostenrechnung                 ASC," + "       Abrechnungsobjekt              ASC" + ");" + ""
			+ "ALTER TABLE AObjHieZuo" + "       ADD  ( CONSTRAINT R_1327"
			+ "              FOREIGN KEY (Mandant, Kostenrechnung, Abrechnungsobjekt)"
			+ "                             REFERENCES Abrechnungsobjekte ) ;" + "ALTER TABLE AObjHieZuo"
			+ "       ADD  ( CONSTRAINT R_1330" + "              FOREIGN KEY (Mandant, Abrechnungsobjekt, Hausbank)"
			+ "                             REFERENCES Abrechnungsobjekte ) ;";

	private final String result = "MATCH(t:Table{name:'AObjHieZuo'}) MATCH(t_R_1330:Index{name:'R_1330'})--(t) MATCH(b:Table{name:'Abrechnungsobjekte'}) MATCH(b_XPKKostenstellen:Index{name:'XPKKostenstellen'})--(b) MATCH(t)-[:FOREIGNKEY]->(R_1330 {name:'R_1330'}) MATCH(b)-[:XPK]->(xpk) CREATE(t)-[:CONNECTED{key:'R_1330'}]->(b)  MERGE(R_1330)-[:REFERENCE]->(xpk)";

	DatabaseMockConnector dc = new DatabaseMockConnector();

	@Test
	public void testImportSql() {
		final ImportTask it = new ImportSqlTask(new DatabaseManager(dc, dc.addr, dc.nutzername, dc.pw), inputSql);
		Assert.assertTrue(it.startImport());
		Assert.assertTrue(dc.lastQuery.equals(result));
	}

	@Test
	public void testImportCsv() {
		final ImportTask it = new ImportCategoryTask(new DatabaseManager(dc, dc.addr, dc.nutzername, dc.pw),
				"\"test\", \"1,2\"");
		Assert.assertTrue(it.startImport());
		Assert.assertTrue(dc.lastQuery.equals("MATCH(t:Table{name:'\"test\"'}) SET t.category='1,2'"));
	}
}
