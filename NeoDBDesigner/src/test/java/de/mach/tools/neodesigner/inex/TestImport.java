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

package de.mach.tools.neodesigner.inex;


import java.util.ArrayList;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.MockConfigSaver;
import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.inex.nimport.ImportCsvTask;
import de.mach.tools.neodesigner.inex.nimport.ImportJsonTask;
import de.mach.tools.neodesigner.inex.nimport.ImportSqlTask;


class TestImport {
  private static String jsonControl = "{\"tables\":[{\"n\":\"Abrechnungsobjekte\",\"cat\":\"3,5\",\"fl\":[{\"n\":\"Mandant\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"Kostenrechnung\",\"dom\":\"String\",\"l\":20,\"isnull\":false},{\"n\":\"Abrechnungsobjekt\",\"dom\":\"String\",\"l\":10,\"isnull\":false},{\"n\":\"Hausbank\",\"dom\":\"Counter\",\"isnull\":true},{\"n\":\"UStBefreiungssatz\",\"dom\":\"Amount\",\"isnull\":false},{\"n\":\"EndeLaufzeit\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKKostenstellen\",\"unique\":true,\"fl\":[\"Mandant\",\"Kostenrechnung\",\"Abrechnungsobjekt\"]},\"il\":[],\"fkl\":[]},{\"n\":\"AObjHieZuo\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"HieKR\",\"dom\":\"String\",\"l\":20,\"isnull\":false},{\"n\":\"HieAObj\",\"dom\":\"String\",\"l\":10,\"isnull\":false},{\"n\":\"HieMandant\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"Hausbank\",\"dom\":\"Counter\",\"isnull\":true},{\"n\":\"Mandant\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"Kostenrechnung\",\"dom\":\"Lookup\",\"isnull\":false},{\"n\":\"Abrechnungsobjekt\",\"dom\":\"String\",\"l\":10,\"isnull\":false}],\"xpk\":{\"n\":\"XPKAObjHieZuo\",\"unique\":true,\"fl\":[]},\"il\":[{\"n\":\"XIF1326AObjHieZuO\",\"unique\":false,\"fl\":[\"HieMandant\",\"HieKR\",\"HieAObj\"]},{\"n\":\"XAK1329AOBJHIEZUO\",\"unique\":true,\"fl\":[\"HieMandant\",\"HieKR\",\"HieAObj\"]},{\"n\":\"XIF1327AObjHieZuO\",\"unique\":false,\"fl\":[\"Mandant\",\"Kostenrechnung\",\"Abrechnungsobjekt\"]},{\"n\":\"XIG1330AObjHieZu\",\"unique\":false,\"fl\":[\"Mandant\",\"Abrechnungsobjekt\",\"Hausbank\"]}],\"fkl\":[{\"n\":\"R_1327\",\"rt\":\"Abrechnungsobjekte\",\"fl\":[\"Mandant\",\"Kostenrechnung\",\"Abrechnungsobjekt\"]},{\"n\":\"R_1330\",\"rt\":\"Abrechnungsobjekte\",\"fl\":[\"Mandant\",\"Abrechnungsobjekt\",\"Hausbank\"]}]}],\"categories\":[]}";
  private static String jsonCsv = "{\"tables\":[{\"n\":\"TABLE0\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD0\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI0\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI0\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE0\",\"unique\":true,\"fl\":[\"FELDZWEI0\"]},\"il\":[{\"n\":\"XIE100TESTINDEX\",\"unique\":false,\"fl\":[\"FELD0\"]},{\"n\":\"XIF100BLABLUB\",\"unique\":false,\"fl\":[\"FELD0\"]}],\"fkl\":[{\"n\":\"R_100\",\"rt\":\"TABLE0\",\"fl\":[\"FELD0\"]}]},{\"n\":\"TABLE1\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD1\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI1\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI1\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE1\",\"unique\":true,\"fl\":[\"FELDZWEI1\"]},\"il\":[{\"n\":\"XIE101TESTINDEX\",\"unique\":false,\"fl\":[\"FELD1\"]},{\"n\":\"XIF101BLABLUB\",\"unique\":false,\"fl\":[\"FELD1\"]}],\"fkl\":[{\"n\":\"R_101\",\"rt\":\"TABLE1\",\"fl\":[\"FELD1\"]}]},{\"n\":\"TABLE2\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD2\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI2\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI2\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE2\",\"unique\":true,\"fl\":[\"FELDZWEI2\"]},\"il\":[{\"n\":\"XIE102TESTINDEX\",\"unique\":false,\"fl\":[\"FELD2\"]},{\"n\":\"XIF102BLABLUB\",\"unique\":false,\"fl\":[\"FELD2\"]}],\"fkl\":[{\"n\":\"R_102\",\"rt\":\"TABLE2\",\"fl\":[\"FELD2\"]}]},{\"n\":\"TABLE3\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD3\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI3\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI3\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE3\",\"unique\":true,\"fl\":[\"FELDZWEI3\"]},\"il\":[{\"n\":\"XIE103TESTINDEX\",\"unique\":false,\"fl\":[\"FELD3\"]},{\"n\":\"XIF103BLABLUB\",\"unique\":false,\"fl\":[\"FELD3\"]}],\"fkl\":[{\"n\":\"R_103\",\"rt\":\"TABLE3\",\"fl\":[\"FELD3\"]}]},{\"n\":\"TABLE4\",\"cat\":\"0,0\",\"fl\":[{\"n\":\"FELD4\",\"dom\":\"String\",\"l\":50,\"isnull\":false},{\"n\":\"FELDZWEI4\",\"dom\":\"Counter\",\"isnull\":false},{\"n\":\"FELDDREI4\",\"dom\":\"Date\",\"isnull\":true}],\"xpk\":{\"n\":\"XPKTABLE4\",\"unique\":true,\"fl\":[\"FELDZWEI4\"]},\"il\":[{\"n\":\"XIE104TESTINDEX\",\"unique\":false,\"fl\":[\"FELD4\"]},{\"n\":\"XIF104BLABLUB\",\"unique\":false,\"fl\":[\"FELD4\"]}],\"fkl\":[{\"n\":\"R_104\",\"rt\":\"TABLE4\",\"fl\":[\"FELD4\"]}]}],\"categories\":[]}";
  private static String csvForColsRes = "TABLE0;FELD0;VARCHAR2;50;0;N\r\n" + "TABLE0;FELDZWEI0;NUMBER;0;0;N\r\n"
                                        + "TABLE0;FELDDREI0;DATE;0;0;Y\r\n" + "TABLE1;FELD1;VARCHAR2;50;0;N\r\n"
                                        + "TABLE1;FELDZWEI1;NUMBER;0;0;N\r\n" + "TABLE1;FELDDREI1;DATE;0;0;Y\r\n"
                                        + "TABLE2;FELD2;VARCHAR2;50;0;N\r\n" + "TABLE2;FELDZWEI2;NUMBER;0;0;N\r\n"
                                        + "TABLE2;FELDDREI2;DATE;0;0;Y\r\n" + "TABLE3;FELD3;VARCHAR2;50;0;N\r\n"
                                        + "TABLE3;FELDZWEI3;NUMBER;0;0;N\r\n" + "TABLE3;FELDDREI3;DATE;0;0;Y\r\n"
                                        + "TABLE4;FELD4;VARCHAR2;50;0;N\r\n" + "TABLE4;FELDZWEI4;NUMBER;0;0;N\r\n"
                                        + "TABLE4;FELDDREI4;DATE;0;0;Y";

  private static String csvForIdxsRes = "TABLE0;XIE100TESTINDEX;FELD0;1;N\r\n" + "TABLE0;XIF100BLABLUB;FELD0;1;N\r\n"
                                        + "TABLE1;XIE101TESTINDEX;FELD1;1;N\r\n" + "TABLE1;XIF101BLABLUB;FELD1;1;N\r\n"
                                        + "TABLE2;XIE102TESTINDEX;FELD2;1;N\r\n" + "TABLE2;XIF102BLABLUB;FELD2;1;N\r\n"
                                        + "TABLE3;XIE103TESTINDEX;FELD3;1;N\r\n" + "TABLE3;XIF103BLABLUB;FELD3;1;N\r\n"
                                        + "TABLE4;XIE104TESTINDEX;FELD4;1;N\r\n" + "TABLE4;XIF104BLABLUB;FELD4;1;N";
  private static String csvForPkeysRes = "TABLE0;XPKTABLE0;FELDZWEI0;1\r\n" + "TABLE1;XPKTABLE1;FELDZWEI1;1\r\n"
                                         + "TABLE2;XPKTABLE2;FELDZWEI2;1\r\n" + "TABLE3;XPKTABLE3;FELDZWEI3;1\r\n"
                                         + "TABLE4;XPKTABLE4;FELDZWEI4;1";
  private static String csvforFKeysRes = "TABLE0;TABLE0;R_100;FELD0;1;N\r\n" + "TABLE1;TABLE1;R_101;FELD1;1;N\r\n"
                                         + "TABLE2;TABLE2;R_102;FELD2;1;N\r\n" + "TABLE3;TABLE3;R_103;FELD3;1;N\r\n"
                                         + "TABLE4;TABLE4;R_104;FELD4;1;N";
  private static String csvForTbls = "TABLE0\r\nTABLE1\r\nTABLE2\r\nTABLE3\r\nTABLE4";

  @Test
  void testImportCsv() {
    checkWithJsonCompare(new ImportCsvTask(TestImport.csvForTbls, TestImport.csvForColsRes, TestImport.csvForIdxsRes,
                                           TestImport.csvForPkeysRes, TestImport.csvforFKeysRes, "", ""),
                         jsonCsv);
  }

  @Test
  void testImportSql() {
    String inputSql = "-- Table: Abrechnungsobjekte Category: 3,5 \n CREATE TABLE Abrechnungsobjekte ("
                      + "       Mandant              INTEGER NOT NULL,"
                      + "       Kostenrechnung       VARCHAR2(20) NOT NULL,"
                      + "       Abrechnungsobjekt    VARCHAR2(10) NOT NULL,"
                      + "       Hausbank             INTEGER NULL,"
                      + "       UStBefreiungssatz    NUMBER(18,5) NOT NULL," + "       EndeLaufzeit         DATE NULL"
                      + ");" + "" + "ALTER TABLE Abrechnungsobjekte"
                      + "       ADD  ( CONSTRAINT XPKKostenstellen PRIMARY KEY (Mandant, "
                      + "              Kostenrechnung, Abrechnungsobjekt) ) ;" + "			  "
                      + "CREATE TABLE AObjHieZuo (" + "       HieKR                VARCHAR2(20) NOT NULL,"
                      + "       HieAObj              VARCHAR2(10) NOT NULL,"
                      + "       HieMandant           INTEGER NOT NULL," + "       Hausbank             INTEGER NULL,"
                      + "       Mandant              INTEGER NOT NULL,"
                      + "       Kostenrechnung       VARCHAR2(20) NOT NULL,"
                      + "       Abrechnungsobjekt    VARCHAR2(10) NOT NULL" + ");" + ""
                      + "CREATE INDEX XIF1326AObjHieZuO ON AObjHieZuo" + "("
                      + "       HieMandant                     ASC," + "       HieKR                          ASC,"
                      + "       HieAObj                        ASC" + ");" + ""
                      + "CREATE UNIQUE INDEX XAK1329AObjHieZuO ON AObjHieZuo" + "("
                      + "       HieMandant                     ASC," + "       HieKR                          ASC,"
                      + "       HieAObj                        ASC" + ");"
                      + "CREATE INDEX XIF1327AObjHieZuO ON AObjHieZuo" + "("
                      + "       Mandant                        ASC," + "       Kostenrechnung                 ASC,"
                      + "       Abrechnungsobjekt              ASC" + ");" + "" + "ALTER TABLE AObjHieZuo"
                      + "       ADD  ( CONSTRAINT R_1327"
                      + "              FOREIGN KEY (Mandant, Kostenrechnung, Abrechnungsobjekt)"
                      + "                             REFERENCES Abrechnungsobjekte ) ;" + "ALTER TABLE AObjHieZuo"
                      + "       ADD  ( CONSTRAINT R_1330"
                      + "              FOREIGN KEY (Mandant, Abrechnungsobjekt, Hausbank)"
                      + "                             REFERENCES Abrechnungsobjekte ) ;";
    checkWithJsonCompare(new ImportSqlTask(inputSql), jsonControl);
  }

  private void checkWithJsonCompare(ImportTask it, String jsonControl) {
    Assert.assertTrue(it.startImport());
    JsonDatamodel json = new JsonDatamodel(new ArrayList<>());
    json.generate(it.getList());
    Assert.assertEquals(jsonControl, json.toString());
  }

  @Test
  void testImportJson() {
    testImportJson(jsonCsv);
    testImportJson(jsonControl);
  }

  private void testImportJson(String compare) {
    checkWithJsonCompare(new ImportJsonTask(new CategoryTranslator(new MockConfigSaver()), compare), compare);
  }
}
