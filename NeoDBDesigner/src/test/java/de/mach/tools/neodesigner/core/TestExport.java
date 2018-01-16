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

import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nexport.CsvForColsGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForFkeysGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForIdxsGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForMetaGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForPkeysGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForTblsGenerator;
import de.mach.tools.neodesigner.core.nexport.CypherGenerator;
import de.mach.tools.neodesigner.core.nexport.Generator;
import de.mach.tools.neodesigner.core.nexport.SqlGenerator;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestExport {
  public static final String resultTable1SQL = " -- Table: Table0 Category: 1,0 Comment:CREATE TABLE Table0 (       Feld0                VARCHAR2(10) NOT NULL,       FeldZwei0            NUMBER(18,5) NOT NULL,       FeldDrei0            DATE NULL);CREATE INDEX XIE100TestIndex ON Table0(       Feld0                     ASC );CREATE INDEX XIF100BlaBlub ON Table0(       Feld0                     ASC );ALTER TABLE Table0       ADD  ( CONSTRAINT XPKTable0 PRIMARY KEY (FeldZwei0 ) ) ; -- Table: Table1 Category: 1,1 Comment:CREATE TABLE Table1 (       Feld1                VARCHAR2(10) NOT NULL,       FeldZwei1            NUMBER(18,5) NOT NULL,       FeldDrei1            DATE NULL);CREATE INDEX XIE101TestIndex ON Table1(       Feld1                     ASC );CREATE INDEX XIF101BlaBlub ON Table1(       Feld1                     ASC );ALTER TABLE Table1       ADD  ( CONSTRAINT XPKTable1 PRIMARY KEY (FeldZwei1 ) ) ; -- Table: Table2 Category: 1,2 Comment:CREATE TABLE Table2 (       Feld2                VARCHAR2(10) NOT NULL,       FeldZwei2            NUMBER(18,5) NOT NULL,       FeldDrei2            DATE NULL);CREATE INDEX XIE102TestIndex ON Table2(       Feld2                     ASC );CREATE INDEX XIF102BlaBlub ON Table2(       Feld2                     ASC );ALTER TABLE Table2       ADD  ( CONSTRAINT XPKTable2 PRIMARY KEY (FeldZwei2 ) ) ; -- Table: Table3 Category: 1,3 Comment:CREATE TABLE Table3 (       Feld3                VARCHAR2(10) NOT NULL,       FeldZwei3            NUMBER(18,5) NOT NULL,       FeldDrei3            DATE NULL);CREATE INDEX XIE103TestIndex ON Table3(       Feld3                     ASC );CREATE INDEX XIF103BlaBlub ON Table3(       Feld3                     ASC );ALTER TABLE Table3       ADD  ( CONSTRAINT XPKTable3 PRIMARY KEY (FeldZwei3 ) ) ; -- Table: Table4 Category: 1,4 Comment:CREATE TABLE Table4 (       Feld4                VARCHAR2(10) NOT NULL,       FeldZwei4            NUMBER(18,5) NOT NULL,       FeldDrei4            DATE NULL);CREATE INDEX XIE104TestIndex ON Table4(       Feld4                     ASC );CREATE INDEX XIF104BlaBlub ON Table4(       Feld4                     ASC );ALTER TABLE Table4       ADD  ( CONSTRAINT XPKTable4 PRIMARY KEY (FeldZwei4 ) ) ;ALTER TABLE Table0       ADD  ( CONSTRAINT R_100              FOREIGN KEY (Feld0)                             REFERENCES Table0 ) ;ALTER TABLE Table1       ADD  ( CONSTRAINT R_101              FOREIGN KEY (Feld1)                             REFERENCES Table1 ) ;ALTER TABLE Table2       ADD  ( CONSTRAINT R_102              FOREIGN KEY (Feld2)                             REFERENCES Table2 ) ;ALTER TABLE Table3       ADD  ( CONSTRAINT R_103              FOREIGN KEY (Feld3)                             REFERENCES Table3 ) ;ALTER TABLE Table4       ADD  ( CONSTRAINT R_104              FOREIGN KEY (Feld4)                             REFERENCES Table4 ) ;";
  public static final String resultCSVTable1 = "Table1,Feld1,VARCHAR2(50),NOT NULL,No,,,,,,,\"1,1\"\r\n"
      + "Table1,FeldZwei1,INTEGER,NOT NULL,No,,,,,,,\"1,1\"\r\n" + "Table1,FeldDrei1,DATE,NULL,No,,,,,,,\"1,1\"\r\n";
  public static final String resultCSVTable2 = "Table3,Feld3,\"NUMBER(50,50)\",NOT NULL,Yes,,,,,,,"
      + "\"0,0\"\r\nTable3,FeldZwei3,Amount,NOT NULL,No,,,,,,,\"0,0\"\r\nTable3,FeldDrei3,Date"
      + ",NULL,No,,,,,,,\"0,0\"";
  private static final String resultcql = "CREATE(t:Table{name:'Table0',category:'1,0'}) CREATE(f1:Field{name:'Feld0',type:'String:10',isRequired:'true'})<-[:DATA]-(t) CREATE(f2:Field{name:'FeldZwei0',type:'Amount',isRequired:'true'})<-[:DATA]-(t) CREATE(f3:Field{name:'FeldDrei0',type:'Date',isRequired:'false'})<-[:DATA]-(t) CREATE(p:Index{name:'XPKTable0',type:'XPK'})<-[:XPK]-(t) CREATE(p)-[:XPK{order:'1'}]->(f2) CREATE(i1:Index{name:'XIE100TestIndex',type:'XIE',unique:'false'})<-[:INDEX]-(t) CREATE(i1)-[:INDEX{order:'1'}]->(f1) CREATE(i2:Index{name:'XIF100BlaBlub',type:'XIF',unique:'false'})<-[:INDEX]-(t) CREATE(i2)-[:INDEX{refname:'FeldZwei0',order:'1'}]->(f1) CREATE(t)-[:FOREIGNKEY]->(c1:ForeignKey{name:'R_100',type:'R'})-[:FOREIGNKEY]->(i2);CREATE(t:Table{name:'Table1',category:'1,1'}) CREATE(f1:Field{name:'Feld1',type:'String:10',isRequired:'true'})<-[:DATA]-(t) CREATE(f2:Field{name:'FeldZwei1',type:'Amount',isRequired:'true'})<-[:DATA]-(t) CREATE(f3:Field{name:'FeldDrei1',type:'Date',isRequired:'false'})<-[:DATA]-(t) CREATE(p:Index{name:'XPKTable1',type:'XPK'})<-[:XPK]-(t) CREATE(p)-[:XPK{order:'1'}]->(f2) CREATE(i1:Index{name:'XIE101TestIndex',type:'XIE',unique:'false'})<-[:INDEX]-(t) CREATE(i1)-[:INDEX{order:'1'}]->(f1) CREATE(i2:Index{name:'XIF101BlaBlub',type:'XIF',unique:'false'})<-[:INDEX]-(t) CREATE(i2)-[:INDEX{refname:'FeldZwei1',order:'1'}]->(f1) CREATE(t)-[:FOREIGNKEY]->(c1:ForeignKey{name:'R_101',type:'R'})-[:FOREIGNKEY]->(i2);CREATE(t:Table{name:'Table2',category:'1,2'}) CREATE(f1:Field{name:'Feld2',type:'String:10',isRequired:'true'})<-[:DATA]-(t) CREATE(f2:Field{name:'FeldZwei2',type:'Amount',isRequired:'true'})<-[:DATA]-(t) CREATE(f3:Field{name:'FeldDrei2',type:'Date',isRequired:'false'})<-[:DATA]-(t) CREATE(p:Index{name:'XPKTable2',type:'XPK'})<-[:XPK]-(t) CREATE(p)-[:XPK{order:'1'}]->(f2) CREATE(i1:Index{name:'XIE102TestIndex',type:'XIE',unique:'false'})<-[:INDEX]-(t) CREATE(i1)-[:INDEX{order:'1'}]->(f1) CREATE(i2:Index{name:'XIF102BlaBlub',type:'XIF',unique:'false'})<-[:INDEX]-(t) CREATE(i2)-[:INDEX{refname:'FeldZwei2',order:'1'}]->(f1) CREATE(t)-[:FOREIGNKEY]->(c1:ForeignKey{name:'R_102',type:'R'})-[:FOREIGNKEY]->(i2);CREATE(t:Table{name:'Table3',category:'1,3'}) CREATE(f1:Field{name:'Feld3',type:'String:10',isRequired:'true'})<-[:DATA]-(t) CREATE(f2:Field{name:'FeldZwei3',type:'Amount',isRequired:'true'})<-[:DATA]-(t) CREATE(f3:Field{name:'FeldDrei3',type:'Date',isRequired:'false'})<-[:DATA]-(t) CREATE(p:Index{name:'XPKTable3',type:'XPK'})<-[:XPK]-(t) CREATE(p)-[:XPK{order:'1'}]->(f2) CREATE(i1:Index{name:'XIE103TestIndex',type:'XIE',unique:'false'})<-[:INDEX]-(t) CREATE(i1)-[:INDEX{order:'1'}]->(f1) CREATE(i2:Index{name:'XIF103BlaBlub',type:'XIF',unique:'false'})<-[:INDEX]-(t) CREATE(i2)-[:INDEX{refname:'FeldZwei3',order:'1'}]->(f1) CREATE(t)-[:FOREIGNKEY]->(c1:ForeignKey{name:'R_103',type:'R'})-[:FOREIGNKEY]->(i2);CREATE(t:Table{name:'Table4',category:'1,4'}) CREATE(f1:Field{name:'Feld4',type:'String:10',isRequired:'true'})<-[:DATA]-(t) CREATE(f2:Field{name:'FeldZwei4',type:'Amount',isRequired:'true'})<-[:DATA]-(t) CREATE(f3:Field{name:'FeldDrei4',type:'Date',isRequired:'false'})<-[:DATA]-(t) CREATE(p:Index{name:'XPKTable4',type:'XPK'})<-[:XPK]-(t) CREATE(p)-[:XPK{order:'1'}]->(f2) CREATE(i1:Index{name:'XIE104TestIndex',type:'XIE',unique:'false'})<-[:INDEX]-(t) CREATE(i1)-[:INDEX{order:'1'}]->(f1) CREATE(i2:Index{name:'XIF104BlaBlub',type:'XIF',unique:'false'})<-[:INDEX]-(t) CREATE(i2)-[:INDEX{refname:'FeldZwei4',order:'1'}]->(f1) CREATE(t)-[:FOREIGNKEY]->(c1:ForeignKey{name:'R_104',type:'R'})-[:FOREIGNKEY]->(i2);MATCH(t:Table{name:'Table0'})-[:FOREIGNKEY]->(c:ForeignKey{name:'R_100'}), (b:Table{name:'Table0'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'R_100'}]->(b)  CREATE(c)-[:REFERENCE]->(p);MATCH(t:Table{name:'Table1'})-[:FOREIGNKEY]->(c:ForeignKey{name:'R_101'}), (b:Table{name:'Table1'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'R_101'}]->(b)  CREATE(c)-[:REFERENCE]->(p);MATCH(t:Table{name:'Table2'})-[:FOREIGNKEY]->(c:ForeignKey{name:'R_102'}), (b:Table{name:'Table2'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'R_102'}]->(b)  CREATE(c)-[:REFERENCE]->(p);MATCH(t:Table{name:'Table3'})-[:FOREIGNKEY]->(c:ForeignKey{name:'R_103'}), (b:Table{name:'Table3'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'R_103'}]->(b)  CREATE(c)-[:REFERENCE]->(p);MATCH(t:Table{name:'Table4'})-[:FOREIGNKEY]->(c:ForeignKey{name:'R_104'}), (b:Table{name:'Table4'})-[:XPK]->(p:Index) CREATE(t)-[:CONNECTED{key:'R_104'}]->(b)  CREATE(c)-[:REFERENCE]->(p);";
  private static final String csvForColsRes = "TABLE0;FELD0;VARCHAR2;10;0;N" + "TABLE0;FELDDREI0;DATE;0;0;Y"
      + "TABLE0;FELDZWEI0;NUMBER;0;18;N" + "TABLE1;FELD1;VARCHAR2;10;0;N" + "TABLE1;FELDDREI1;DATE;0;0;Y"
      + "TABLE1;FELDZWEI1;NUMBER;0;18;N" + "TABLE2;FELD2;VARCHAR2;10;0;N" + "TABLE2;FELDDREI2;DATE;0;0;Y"
      + "TABLE2;FELDZWEI2;NUMBER;0;18;N" + "TABLE3;FELD3;VARCHAR2;10;0;N" + "TABLE3;FELDDREI3;DATE;0;0;Y"
      + "TABLE3;FELDZWEI3;NUMBER;0;18;N" + "TABLE4;FELD4;VARCHAR2;10;0;N" + "TABLE4;FELDDREI4;DATE;0;0;Y"
      + "TABLE4;FELDZWEI4;NUMBER;0;18;N" + "";
  private static final String csvForIdxsRes = "TABLE0;XIE100TESTINDEX;FELD0;1;N\r\n"
      + "TABLE0;XIF100BLABLUB;FELD0;1;N\r\n" + "TABLE1;XIE101TESTINDEX;FELD1;1;N\r\n"
      + "TABLE1;XIF101BLABLUB;FELD1;1;N\r\n" + "TABLE2;XIE102TESTINDEX;FELD2;1;N\r\n"
      + "TABLE2;XIF102BLABLUB;FELD2;1;N\r\n" + "TABLE3;XIE103TESTINDEX;FELD3;1;N\r\n"
      + "TABLE3;XIF103BLABLUB;FELD3;1;N\r\n" + "TABLE4;XIE104TESTINDEX;FELD4;1;N\r\n"
      + "TABLE4;XIF104BLABLUB;FELD4;1;N";
  private static final String csvForPkeysRes = "TABLE0;XPKTABLE0;FELDZWEI0;1\r\n" + "TABLE1;XPKTABLE1;FELDZWEI1;1\r\n"
      + "TABLE2;XPKTABLE2;FELDZWEI2;1\r\n" + "TABLE3;XPKTABLE3;FELDZWEI3;1\r\n" + "TABLE4;XPKTABLE4;FELDZWEI4;1";
  private static final String csvforFKeysRes = "TABLE0;TABLE0;R_100;FELD0;1;NTABLE1;TABLE1;R_101;FELD1;1;NTABLE2;TABLE2;"
      + "R_102;FELD2;1;NTABLE3;TABLE3;R_103;FELD3;1;NTABLE4;TABLE4;R_104;FELD4;1;N";
  private static final String csvForTbls = "TABLE0\r\nTABLE1\r\nTABLE2\r\nTABLE3\r\nTABLE4";
  private static final String csvForMeta = "T;Table0;1,0; \r\n" + "T;Table1;1,1; \r\n" + "T;Table2;1,2; \r\n"
      + "T;Table3;1,3; \r\n" + "T;Table4;1,4; \r\n" + "\r\n" + "R;Table0;Feld0; \r\n" + "R;Table0;FeldZwei0; \r\n"
      + "R;Table0;FeldDrei0; \r\n" + "R;Table1;Feld1; \r\n" + "R;Table1;FeldZwei1; \r\n" + "R;Table1;FeldDrei1; \r\n"
      + "R;Table2;Feld2; \r\n" + "R;Table2;FeldZwei2; \r\n" + "R;Table2;FeldDrei2; \r\n" + "R;Table3;Feld3; \r\n"
      + "R;Table3;FeldZwei3; \r\n" + "R;Table3;FeldDrei3; \r\n" + "R;Table4;Feld4; \r\n" + "R;Table4;FeldZwei4; \r\n"
      + "R;Table4;FeldDrei4; ";

  @Test
  public void testCqlGenerator() {
    final List<Table> lt = TestUtil.getTableList();
    final Generator cqlGen = new CypherGenerator();
    final String ret = cqlGen.generate(lt);
    Assert.assertTrue(ret.replaceAll("\r\n", "").contains(TestExport.resultcql));
  }

  @Test
  public void testSqlGenerator() {
    final List<Table> lt = TestUtil.getTableList();
    final Generator sqlGen = new SqlGenerator();
    final String ret = sqlGen.generate(lt);
    Assert.assertTrue(ret.replaceAll("\r\n", "").contains(TestExport.resultTable1SQL));
  }

  @Test
  public void testCsvForColsGenerator() {
    final List<Table> lt = TestUtil.getTableList();
    final Generator csvGen = new CsvForColsGenerator();
    final String ret = csvGen.generate(lt);
    Assert.assertTrue(ret.replaceAll("\r\n", "").contains(TestExport.csvForColsRes));
  }

  @Test
  public void testCsvForFkeysGenerator() {
    final List<Table> lt = TestUtil.getTableList();
    final Generator csvGen = new CsvForFkeysGenerator();
    final String ret = csvGen.generate(lt);
    Assert.assertTrue(ret.replaceAll("\r\n", "").contains(TestExport.csvforFKeysRes));
  }

  @Test
  public void testCsvForIdxsGenerator() {
    final List<Table> lt = TestUtil.getTableList();
    final Generator csvGen = new CsvForIdxsGenerator();
    final String ret = csvGen.generate(lt);
    Assert.assertTrue(ret.contains(TestExport.csvForIdxsRes));
  }

  @Test
  public void testCsvForPkeysGenerator() {
    final List<Table> lt = TestUtil.getTableList();
    final Generator csvGen = new CsvForPkeysGenerator();
    final String ret = csvGen.generate(lt);
    Assert.assertTrue(ret.contains(TestExport.csvForPkeysRes));
  }

  @Test
  public void testCsvForTblsGenerator() {
    final List<Table> lt = TestUtil.getTableList();
    final Generator csvGen = new CsvForTblsGenerator();
    final String ret = csvGen.generate(lt);
    Assert.assertTrue(ret.contains(TestExport.csvForTbls));
  }

  @Test
  public void testCsvForMetaGenerator() {
    final List<Table> lt = TestUtil.getTableList();
    final Generator csvGen = new CsvForMetaGenerator();
    final String ret = csvGen.generate(lt);
    Assert.assertTrue(ret.contains(TestExport.csvForMeta));
  }

}
