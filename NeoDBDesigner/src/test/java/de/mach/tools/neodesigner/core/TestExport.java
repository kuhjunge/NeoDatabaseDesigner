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

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.category.CategoryTranslator;
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
import de.mach.tools.neodesigner.core.nexport.TexGenerator;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnectorImpl;


class TestExport {
  private static final String resultTex = "\\documentclass{scrartcl}\\usepackage[a4paper, total={190mm, 260mm}]{geometry}\\usepackage[utf8]{inputenc}% Schoenere Schriftart laden\\usepackage[T1]{fontenc}\\usepackage{lmodern}% Deutsche Silbentrennung verwenden\\usepackage[ngerman]{babel}\\usepackage[table]{xcolor}\\usepackage{multicol}\\usepackage{tabularx}\\usepackage{tikz}\\usepackage{imakeidx}% Bessere Unterstuetzung fuer PDF-Features\\usepackage[breaklinks=true,hidelinks]{hyperref}\\usepackage{longtable}% Abmessungen der Seite\\geometry{  left=1cm,  right=1cm,  top=1cm,  bottom=2.5cm,}";
  private static final String resultTable1SQL = " -- Table: Table0 Category: 1,0 Comment:CREATE TABLE Table0 (       Feld0                VARCHAR2(10) NOT NULL,       FeldDrei0            DATE NULL,       FeldZwei0            NUMBER(18,5) NOT NULL);CREATE INDEX XIE100TestIndex ON Table0(       Feld0                     ASC );CREATE INDEX XIF100BlaBlub ON Table0(       Feld0                     ASC );ALTER TABLE Table0       ADD  ( CONSTRAINT XPKTable0 PRIMARY KEY (FeldZwei0 ) ) ; -- Table: Table1 Category: 1,1 Comment:CREATE TABLE Table1 (       Feld1                VARCHAR2(10) NOT NULL,       FeldDrei1            DATE NULL,       FeldZwei1            NUMBER(18,5) NOT NULL);CREATE INDEX XIE101TestIndex ON Table1(       Feld1                     ASC );CREATE INDEX XIF101BlaBlub ON Table1(       Feld1                     ASC );ALTER TABLE Table1       ADD  ( CONSTRAINT XPKTable1 PRIMARY KEY (FeldZwei1 ) ) ; -- Table: Table2 Category: 1,2 Comment:CREATE TABLE Table2 (       Feld2                VARCHAR2(10) NOT NULL,       FeldDrei2            DATE NULL,       FeldZwei2            NUMBER(18,5) NOT NULL);CREATE INDEX XIE102TestIndex ON Table2(       Feld2                     ASC );CREATE INDEX XIF102BlaBlub ON Table2(       Feld2                     ASC );ALTER TABLE Table2       ADD  ( CONSTRAINT XPKTable2 PRIMARY KEY (FeldZwei2 ) ) ; -- Table: Table3 Category: 1,3 Comment:CREATE TABLE Table3 (       Feld3                VARCHAR2(10) NOT NULL,       FeldDrei3            DATE NULL,       FeldZwei3            NUMBER(18,5) NOT NULL);CREATE INDEX XIE103TestIndex ON Table3(       Feld3                     ASC );CREATE INDEX XIF103BlaBlub ON Table3(       Feld3                     ASC );ALTER TABLE Table3       ADD  ( CONSTRAINT XPKTable3 PRIMARY KEY (FeldZwei3 ) ) ; -- Table: Table4 Category: 1,4 Comment:CREATE TABLE Table4 (       Feld4                VARCHAR2(10) NOT NULL,       FeldDrei4            DATE NULL,       FeldZwei4            NUMBER(18,5) NOT NULL);CREATE INDEX XIE104TestIndex ON Table4(       Feld4                     ASC );CREATE INDEX XIF104BlaBlub ON Table4(       Feld4                     ASC );ALTER TABLE Table4       ADD  ( CONSTRAINT XPKTable4 PRIMARY KEY (FeldZwei4 ) ) ;ALTER TABLE Table0       ADD  ( CONSTRAINT R_100              FOREIGN KEY (Feld0)                             REFERENCES Table0 ) ;ALTER TABLE Table1       ADD  ( CONSTRAINT R_101              FOREIGN KEY (Feld1)                             REFERENCES Table1 ) ;ALTER TABLE Table2       ADD  ( CONSTRAINT R_102              FOREIGN KEY (Feld2)                             REFERENCES Table2 ) ;ALTER TABLE Table3       ADD  ( CONSTRAINT R_103              FOREIGN KEY (Feld3)                             REFERENCES Table3 ) ;ALTER TABLE Table4       ADD  ( CONSTRAINT R_104              FOREIGN KEY (Feld4)                             REFERENCES Table4 ) ;";
  private static final String resultcql = "CREATE(t:Table{name:'Table0',category:'1,0'}) CREATE(f0:Column{name:'Feld0',type:'VARCHAR2',accuracy:'0',length:'10',nullable:'false',table:'Table0'})<-[:has_column]-(t) CREATE(f1:Column{name:'FeldDrei0',type:'DATE',accuracy:'0',length:'0',nullable:'true',table:'Table0'})<-[:has_column]-(t) CREATE(f2:Column{name:'FeldZwei0',type:'NUMBER',accuracy:'18',length:'0',nullable:'false',table:'Table0'})<-[:has_column]-(t)CREATE(t)-[:identified_by{name:'XPKTable0',order:'1',unique:'true'}]->(f2)CREATE(t)-[:indexed_by{name:'XIE100TestIndex',order:'1',unique:'false'}]->(f0)CREATE(t)-[:indexed_by{name:'XIF100BlaBlub',order:'1',unique:'false'}]->(f0);CREATE(t:Table{name:'Table1',category:'1,1'}) CREATE(f0:Column{name:'Feld1',type:'VARCHAR2',accuracy:'0',length:'10',nullable:'false',table:'Table1'})<-[:has_column]-(t) CREATE(f1:Column{name:'FeldDrei1',type:'DATE',accuracy:'0',length:'0',nullable:'true',table:'Table1'})<-[:has_column]-(t) CREATE(f2:Column{name:'FeldZwei1',type:'NUMBER',accuracy:'18',length:'0',nullable:'false',table:'Table1'})<-[:has_column]-(t)CREATE(t)-[:identified_by{name:'XPKTable1',order:'1',unique:'true'}]->(f2)CREATE(t)-[:indexed_by{name:'XIE101TestIndex',order:'1',unique:'false'}]->(f0)CREATE(t)-[:indexed_by{name:'XIF101BlaBlub',order:'1',unique:'false'}]->(f0);CREATE(t:Table{name:'Table2',category:'1,2'}) CREATE(f0:Column{name:'Feld2',type:'VARCHAR2',accuracy:'0',length:'10',nullable:'false',table:'Table2'})<-[:has_column]-(t) CREATE(f1:Column{name:'FeldDrei2',type:'DATE',accuracy:'0',length:'0',nullable:'true',table:'Table2'})<-[:has_column]-(t) CREATE(f2:Column{name:'FeldZwei2',type:'NUMBER',accuracy:'18',length:'0',nullable:'false',table:'Table2'})<-[:has_column]-(t)CREATE(t)-[:identified_by{name:'XPKTable2',order:'1',unique:'true'}]->(f2)CREATE(t)-[:indexed_by{name:'XIE102TestIndex',order:'1',unique:'false'}]->(f0)CREATE(t)-[:indexed_by{name:'XIF102BlaBlub',order:'1',unique:'false'}]->(f0);CREATE(t:Table{name:'Table3',category:'1,3'}) CREATE(f0:Column{name:'Feld3',type:'VARCHAR2',accuracy:'0',length:'10',nullable:'false',table:'Table3'})<-[:has_column]-(t) CREATE(f1:Column{name:'FeldDrei3',type:'DATE',accuracy:'0',length:'0',nullable:'true',table:'Table3'})<-[:has_column]-(t) CREATE(f2:Column{name:'FeldZwei3',type:'NUMBER',accuracy:'18',length:'0',nullable:'false',table:'Table3'})<-[:has_column]-(t)CREATE(t)-[:identified_by{name:'XPKTable3',order:'1',unique:'true'}]->(f2)CREATE(t)-[:indexed_by{name:'XIE103TestIndex',order:'1',unique:'false'}]->(f0)CREATE(t)-[:indexed_by{name:'XIF103BlaBlub',order:'1',unique:'false'}]->(f0);CREATE(t:Table{name:'Table4',category:'1,4'}) CREATE(f0:Column{name:'Feld4',type:'VARCHAR2',accuracy:'0',length:'10',nullable:'false',table:'Table4'})<-[:has_column]-(t) CREATE(f1:Column{name:'FeldDrei4',type:'DATE',accuracy:'0',length:'0',nullable:'true',table:'Table4'})<-[:has_column]-(t) CREATE(f2:Column{name:'FeldZwei4',type:'NUMBER',accuracy:'18',length:'0',nullable:'false',table:'Table4'})<-[:has_column]-(t)CREATE(t)-[:identified_by{name:'XPKTable4',order:'1',unique:'true'}]->(f2)CREATE(t)-[:indexed_by{name:'XIE104TestIndex',order:'1',unique:'false'}]->(f0)CREATE(t)-[:indexed_by{name:'XIF104BlaBlub',order:'1',unique:'false'}]->(f0);MATCH(t:Table{name:'Table0'})-[:has_column]->(f{name:'Feld0'}),(b:Table{name:'Table0'}) CREATE (f)-[:references{name:'R_100', order:'1'}]->(b);MATCH(t:Table{name:'Table1'})-[:has_column]->(f{name:'Feld1'}),(b:Table{name:'Table1'}) CREATE (f)-[:references{name:'R_101', order:'1'}]->(b);MATCH(t:Table{name:'Table2'})-[:has_column]->(f{name:'Feld2'}),(b:Table{name:'Table2'}) CREATE (f)-[:references{name:'R_102', order:'1'}]->(b);MATCH(t:Table{name:'Table3'})-[:has_column]->(f{name:'Feld3'}),(b:Table{name:'Table3'}) CREATE (f)-[:references{name:'R_103', order:'1'}]->(b);MATCH(t:Table{name:'Table4'})-[:has_column]->(f{name:'Feld4'}),(b:Table{name:'Table4'}) CREATE (f)-[:references{name:'R_104', order:'1'}]->(b);";
  private static final String csvForColsRes = "TABLE0;FELD0;VARCHAR2;10;0;N" + "TABLE0;FELDDREI0;DATE;0;0;Y"
                                              + "TABLE0;FELDZWEI0;NUMBER;0;18;N" + "TABLE1;FELD1;VARCHAR2;10;0;N"
                                              + "TABLE1;FELDDREI1;DATE;0;0;Y" + "TABLE1;FELDZWEI1;NUMBER;0;18;N"
                                              + "TABLE2;FELD2;VARCHAR2;10;0;N" + "TABLE2;FELDDREI2;DATE;0;0;Y"
                                              + "TABLE2;FELDZWEI2;NUMBER;0;18;N" + "TABLE3;FELD3;VARCHAR2;10;0;N"
                                              + "TABLE3;FELDDREI3;DATE;0;0;Y" + "TABLE3;FELDZWEI3;NUMBER;0;18;N"
                                              + "TABLE4;FELD4;VARCHAR2;10;0;N" + "TABLE4;FELDDREI4;DATE;0;0;Y"
                                              + "TABLE4;FELDZWEI4;NUMBER;0;18;N" + "";
  private static final String csvForIdxsRes = "TABLE0;XIE100TESTINDEX;FELD0;1;N" + "TABLE0;XIF100BLABLUB;FELD0;1;N"
                                              + "TABLE1;XIE101TESTINDEX;FELD1;1;N" + "TABLE1;XIF101BLABLUB;FELD1;1;N"
                                              + "TABLE2;XIE102TESTINDEX;FELD2;1;N" + "TABLE2;XIF102BLABLUB;FELD2;1;N"
                                              + "TABLE3;XIE103TESTINDEX;FELD3;1;N" + "TABLE3;XIF103BLABLUB;FELD3;1;N"
                                              + "TABLE4;XIE104TESTINDEX;FELD4;1;N" + "TABLE4;XIF104BLABLUB;FELD4;1;N";
  private static final String csvForPkeysRes = "TABLE0;XPKTABLE0;FELDZWEI0;1TABLE1;XPKTABLE1;FELDZWEI1;1TABLE2;XPKTABLE2;FELDZWEI2;1TABLE3;XPKTABLE3;FELDZWEI3;1TABLE4;XPKTABLE4;FELDZWEI4;1";
  private static final String csvforFKeysRes = "TABLE0;TABLE0;R_100;FELD0;1;NTABLE1;TABLE1;R_101;FELD1;1;NTABLE2;TABLE2;"
                                               + "R_102;FELD2;1;NTABLE3;TABLE3;R_103;FELD3;1;NTABLE4;TABLE4;R_104;FELD4;1;N";
  private static final String csvForTbls = "TABLE0TABLE1TABLE2TABLE3TABLE4";
  private static final String csvForMeta = "T;Table0;1,0;T;Table1;1,1;T;Table2;1,2;T;Table3;1,3;T;Table4;1,4;R;Table0;Feld0;R;Table0;FeldDrei0;R;Table0;FeldZwei0;R;Table1;Feld1;R;Table1;FeldDrei1;R;Table1;FeldZwei1;R;Table2;Feld2;R;Table2;FeldDrei2;R;Table2;FeldZwei2;R;Table3;Feld3;R;Table3;FeldDrei3;R;Table3;FeldZwei3;R;Table4;Feld4;R;Table4;FeldDrei4;R;Table4;FeldZwei4;";

  private String generateRes(Generator gen) {
    final List<Table> lt = TestUtil.getTableList();
    final String ret = gen.generate(lt);
    return ret.replaceAll("\r", "").replaceAll("\n", "");
  }

  @Test
  void testCqlGenerator() {
    assertTrue(generateRes(new CypherGenerator()).contains(TestExport.resultcql));
  }

  @Test
  void testCsvForColsGenerator() {
    assertTrue(generateRes(new CsvForColsGenerator()).contains(TestExport.csvForColsRes));
  }

  @Test
  void testCsvForFkeysGenerator() {
    assertTrue(generateRes(new CsvForFkeysGenerator()).contains(TestExport.csvforFKeysRes));
  }

  @Test
  void testCsvForIdxsGenerator() {
    assertTrue(generateRes(new CsvForIdxsGenerator()).contains(TestExport.csvForIdxsRes));
  }

  @Test
  void testCsvForMetaGenerator() {
    assertTrue(generateRes(new CsvForMetaGenerator(' ')).contains(TestExport.csvForMeta));
  }

  @Test
  void testCsvForPkeysGenerator() {
    assertTrue(generateRes(new CsvForPkeysGenerator()).contains(TestExport.csvForPkeysRes));
  }

  @Test
  void testCsvForTblsGenerator() {
    assertTrue(generateRes(new CsvForTblsGenerator()).contains(TestExport.csvForTbls));
  }

  @Test
  void testSqlGenerator() {
    assertTrue(generateRes(new SqlGenerator()).contains(TestExport.resultTable1SQL));
  }

  @Test
  void testTexGenerator() {
    Model ndbm = new ModelImpl(new DatabaseConnectorImpl(), new CategoryTranslator());
    final Generator texGen = new TexGenerator("Titel", "Autor",
                                              new File(ndbm.getPdfConfig().getConfigPath()
                                                       + de.mach.tools.neodesigner.core.Strings.CATEGORYFILE));
    assertTrue(generateRes(texGen).contains(TestExport.resultTex));
  }
}
