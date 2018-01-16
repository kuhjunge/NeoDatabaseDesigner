/*******************************************************************************
 * Copyright (C) 2017 Chris Deter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.mach.tools.neodesigner.core;

import org.junit.Assert;
import org.junit.Test;

import de.mach.tools.neodesigner.core.nimport.ImportCategoryTask;
import de.mach.tools.neodesigner.core.nimport.ImportCsvTask;
import de.mach.tools.neodesigner.core.nimport.ImportSqlTask;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.cypher.DatabaseManager;

public class TestImport {
  String inputSql = "-- Table: Abrechnungsobjekte Category: 3,5 \n CREATE TABLE Abrechnungsobjekte ("
      + "       Mandant              INTEGER NOT NULL," + "       Kostenrechnung       VARCHAR2(20) NOT NULL,"
      + "       Abrechnungsobjekt    VARCHAR2(10) NOT NULL," + "       Hausbank             INTEGER NULL,"
      + "       UStBefreiungssatz    NUMBER(18,5) NOT NULL," + "       EndeLaufzeit         DATE NULL" + ");" + ""
      + "ALTER TABLE Abrechnungsobjekte" + "       ADD  ( CONSTRAINT XPKKostenstellen PRIMARY KEY (Mandant, "
      + "              Kostenrechnung, Abrechnungsobjekt) ) ;" + "			  " + "CREATE TABLE AObjHieZuo ("
      + "       HieKR                VARCHAR2(20) NOT NULL," + "       HieAObj              VARCHAR2(10) NOT NULL,"
      + "       HieMandant           INTEGER NOT NULL," + "       Hausbank             INTEGER NULL,"
      + "       Mandant              INTEGER NOT NULL," + "       Kostenrechnung       VARCHAR2(20) NOT NULL,"
      + "       Abrechnungsobjekt    VARCHAR2(10) NOT NULL" + ");" + "" + "CREATE INDEX XIF1326AObjHieZuO ON AObjHieZuo"
      + "(" + "       HieMandant                     ASC," + "       HieKR                          ASC,"
      + "       HieAObj                        ASC" + ");" + "" + "CREATE UNIQUE INDEX XAK1329AObjHieZuO ON AObjHieZuo"
      + "(" + "       HieMandant                     ASC," + "       HieKR                          ASC,"
      + "       HieAObj                        ASC" + ");" + "CREATE INDEX XIF1327AObjHieZuO ON AObjHieZuo" + "("
      + "       Mandant                        ASC," + "       Kostenrechnung                 ASC,"
      + "       Abrechnungsobjekt              ASC" + ");" + "" + "ALTER TABLE AObjHieZuo"
      + "       ADD  ( CONSTRAINT R_1327" + "              FOREIGN KEY (Mandant, Kostenrechnung, Abrechnungsobjekt)"
      + "                             REFERENCES Abrechnungsobjekte ) ;" + "ALTER TABLE AObjHieZuo"
      + "       ADD  ( CONSTRAINT R_1330" + "              FOREIGN KEY (Mandant, Abrechnungsobjekt, Hausbank)"
      + "                             REFERENCES Abrechnungsobjekte ) ;";

  private final String result = "MATCH(t:Table{name:'AObjHieZuo'})-[:FOREIGNKEY]->(c:ForeignKey{name:'R_1330'}), (b:Table{name:'Abrechnungsobjekte'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'R_1330'}]->(b)  CREATE(c)-[:REFERENCE]->(p)";
  private static String csvForColsRes = "TABLE0;FELD0;VARCHAR2;50;0;N\r\n" + "TABLE0;FELDZWEI0;NUMBER;0;0;N\r\n"
      + "TABLE0;FELDDREI0;DATE;0;0;Y\r\n" + "TABLE1;FELD1;VARCHAR2;50;0;N\r\n" + "TABLE1;FELDZWEI1;NUMBER;0;0;N\r\n"
      + "TABLE1;FELDDREI1;DATE;0;0;Y\r\n" + "TABLE2;FELD2;VARCHAR2;50;0;N\r\n" + "TABLE2;FELDZWEI2;NUMBER;0;0;N\r\n"
      + "TABLE2;FELDDREI2;DATE;0;0;Y\r\n" + "TABLE3;FELD3;VARCHAR2;50;0;N\r\n" + "TABLE3;FELDZWEI3;NUMBER;0;0;N\r\n"
      + "TABLE3;FELDDREI3;DATE;0;0;Y\r\n" + "TABLE4;FELD4;VARCHAR2;50;0;N\r\n" + "TABLE4;FELDZWEI4;NUMBER;0;0;N\r\n"
      + "TABLE4;FELDDREI4;DATE;0;0;Y";
  private static String csvForIdxsRes = "TABLE0;XIE100TESTINDEX;FELD0;1;N\r\n" + "TABLE0;XIF100BLABLUB;FELD0;1;N\r\n"
      + "TABLE1;XIE101TESTINDEX;FELD1;1;N\r\n" + "TABLE1;XIF101BLABLUB;FELD1;1;N\r\n"
      + "TABLE2;XIE102TESTINDEX;FELD2;1;N\r\n" + "TABLE2;XIF102BLABLUB;FELD2;1;N\r\n"
      + "TABLE3;XIE103TESTINDEX;FELD3;1;N\r\n" + "TABLE3;XIF103BLABLUB;FELD3;1;N\r\n"
      + "TABLE4;XIE104TESTINDEX;FELD4;1;N\r\n" + "TABLE4;XIF104BLABLUB;FELD4;1;N";
  private static String csvForPkeysRes = "TABLE0;XPKTABLE0;FELDZWEI0;1\r\n" + "TABLE1;XPKTABLE1;FELDZWEI1;1\r\n"
      + "TABLE2;XPKTABLE2;FELDZWEI2;1\r\n" + "TABLE3;XPKTABLE3;FELDZWEI3;1\r\n" + "TABLE4;XPKTABLE4;FELDZWEI4;1";
  private static String csvforFKeysRes = "TABLE0;TABLE0;R_100;FELD0;0;N\r\n" + "TABLE1;TABLE1;R_101;FELD1;0;N\r\n"
      + "TABLE2;TABLE2;R_102;FELD2;0;N\r\n" + "TABLE3;TABLE3;R_103;FELD3;0;N\r\n" + "TABLE4;TABLE4;R_104;FELD4;0;N";
  private static String csvForTbls = "TABLE0\r\nTABLE1\r\nTABLE2\r\nTABLE3\r\nTABLE4";

  de.mach.tools.neodesigner.database.cypher.DatabaseMockConnector dc = new de.mach.tools.neodesigner.database.cypher.DatabaseMockConnector();

  @Test
  public void testImportSql() {
    final ImportTask it = new ImportSqlTask(new DatabaseManager(dc, dc.addr, dc.nutzername, dc.pw), inputSql);
    Assert.assertTrue(it.startImport());
    Assert.assertTrue(dc.lastQuery.equals(result));
  }

  @Test
  public void testImportCategory() {
    final ImportTask it = new ImportCategoryTask(new DatabaseManager(dc, dc.addr, dc.nutzername, dc.pw),
        "\"test\", \"1,2\"");
    Assert.assertTrue(it.startImport());
    Assert.assertTrue(dc.lastQuery.equals("MATCH(t:Table{name:'\"test\"'}) SET t.category='1,2'"));
  }

  @Test
  public void testImportCsv() {
    final ImportTask it = new ImportCsvTask(new DatabaseManager(dc, dc.addr, dc.nutzername, dc.pw),
        TestImport.csvForTbls, TestImport.csvForColsRes, TestImport.csvForIdxsRes, TestImport.csvForPkeysRes,
        TestImport.csvforFKeysRes, "");
    Assert.assertTrue(it.startImport());
    Assert.assertTrue(dc.lastQuery.equals(
        "MATCH(t:Table{name:'TABLE4'})-[:FOREIGNKEY]->(c:ForeignKey{name:'R_104'}), (b:Table{name:'TABLE4'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'R_104'}]->(b)  CREATE(c)-[:REFERENCE]->(p)"));
  }
}
