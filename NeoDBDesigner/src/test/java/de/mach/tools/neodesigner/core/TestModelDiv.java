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

import org.junit.jupiter.api.Test;


class TestModelDiv {
  @Test
  void testConfiguration() {
    final String adrCheck = "bolt://127.0.0.1\\:7688";
    final String startLocCheck = "E:\\Programme\\Neo4J";
    final String pwCheck = "test";
    final String usrCheck = "MaxMustermann";
    final int lengthCheck = 26;
    final Configuration conf = new Configuration();
    ConfigSaver cs = new MockConfigSaver();
    conf.init(cs);
    conf.setNeoDbStarterLocation(startLocCheck);
    conf.save(adrCheck, usrCheck, pwCheck);
    conf.setWordLength(lengthCheck);
    conf.setCheckDuplicateIndizes(true);
    conf.setMikTexPath("C:\\test\\miktex");
    conf.setPathExportSql("C:\\test\\sql");
    conf.setPathExportCsv("C:\\test\\csv");
    conf.setPathExportGeneric("C:\\test\\cql");
    conf.setPathImportCat("C:\\test\\cat");
    conf.setPathImportCsv("C:\\test\\csv2");
    conf.setPathImportSql("C:\\test\\sql2");
    conf.setPdfFile("C:\\test\\pdf");
    conf.setPdfAuthor("cd");
    conf.setPdfTitle("datamodel");
    conf.save();
    // Check ob auslesen klappt
    final Configuration confC = conf;
    assertEquals(adrCheck, confC.getAddrOfDb());
    assertEquals(startLocCheck, confC.getNeoDbStarterLocation());
    assertEquals(pwCheck, confC.getPw());
    assertEquals(usrCheck, confC.getUser());
    assertEquals(lengthCheck, confC.getWordLength());
    assertTrue(conf.getCheckDuplicateIndizes());
    assertEquals("C:\\test\\miktex", confC.getMikTexPath());
    assertEquals("C:\\test\\sql", confC.getPathExportSql());
    assertEquals("C:\\test\\csv", confC.getPathExportCsv());
    assertEquals("C:\\test\\cql", confC.getPathExportGeneric());
    assertEquals("C:\\test\\cat", confC.getPathImportCat());
    assertEquals("C:\\test\\csv2", confC.getPathImportCsv());
    assertEquals("C:\\test\\sql2", confC.getPathImportSql());
    assertEquals("C:\\test\\pdf", confC.getPdfFile());
    assertEquals("cd", confC.getPdfAuthor());
    assertEquals("datamodel", confC.getPdfTitle());
    assertTrue(confC.getConfigPath().contains(Strings.SOFTWARENAME));
  }

  /** Kann fehlschlagen, wenn Software noch nie auf dem PC benutzt wurde */
  @Test
  void testFile() {
    final Configuration confC = new Configuration();
    assertNotNull(Util.getFolder(confC.getConfigPath()));
  }
}
