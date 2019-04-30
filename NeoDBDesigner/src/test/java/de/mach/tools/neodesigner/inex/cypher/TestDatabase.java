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

package de.mach.tools.neodesigner.inex.cypher;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.DatabaseConnection;


class TestDatabase {
  DatabaseMockConnector dc = new DatabaseMockConnector();
  private String test1 = "TEST1";
  private String test2 = "TEST2";
  private String name = "name";

  @Test
  void getTable() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    dbm.getTable(name);
    assertEquals("MATCH(t:Table{name:'name'}) RETURN t.name AS name, t.category AS category, t.comment AS comment LIMIT 1",
                 dc.lastQuery);
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    final Map<String, Object> map = new HashMap<>();
    map.put(name, name);
    String category = "test";
    map.put("category", category);
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    final Optional<Table> t = dbm.getTable(name);
    assertTrue(t.isPresent());
    assertEquals(t.get().getName(), name);
    assertEquals(t.get().getCategory(), category);
    assertEquals("MATCH(t:Table{name:'name'})-[r:identified_by|indexed_by]-(c) RETURN type(r) as type, r.order as order, r.name as indexname, c.name as name ORDER BY type, order",
                 dc.lastQuery);
  }

  @Test
  void testDatabaseManager() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    dc.addr = test1;
    dc.nutzername = test2;
    String test3 = "TEST3";
    dc.pw = test3;
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    assertTrue(!dbm.connectDb("", test3, test3));
    assertTrue(!dbm.isReady());
    assertTrue(dbm.connectDb(test1, test2, test3));
    assertTrue(dbm.isReady());
    dbm.createIndexOnDb();
    final QueryBuilder qb = new QueryBuilder();
    qb.insertIndexOnDb(Strings.NODE_TABLE);
    assertEquals(dc.lastQuery, qb.toString());
    final Map<String, Object> map = new HashMap<>();
    map.put(name, test1);
    map.put("category", test2);
    final ArrayList<ResultUnit> lruTable = new ArrayList<>();
    lruTable.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruTable);
    assertTrue(dbm.getListWithTableNames().contains(test1));
    // Tabellenquery
    dc.list2DResultUnit.add(lruTable);
    // FeldQuery
    final ArrayList<ResultUnit> lruField = new ArrayList<>();
    map.clear();
    map.put("tablename", test1);
    map.put("name", test2);
    map.put("altname", test3);
    map.put("type", "VARCHAR");
    map.put("length", 20);
    map.put("accuracy", 0);
    map.put("comment", "CommToMe");
    map.put("nullable", false);
    lruField.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruField);
    // Index
    final ArrayList<ResultUnit> lruIndex = new ArrayList<>();
    map.clear();
    map.put(name, test2);
    map.put("indexname", "XAK" + test3);
    map.put("tablename", test1);
    map.put("order", 1);
    map.put("type", "indexed_by");
    map.put("unique", true);
    lruIndex.add(new ResultUnitImpl(map));
    map.clear();
    map.put(name, test2);
    map.put("indexname", "XPK" + test1);
    map.put("tablename", test1);
    map.put("order", 1);
    map.put("type", "identified_by");
    map.put("unique", true);
    lruIndex.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndex);
    final ArrayList<ResultUnit> lruIndexFields = new ArrayList<>();
    map.clear();
    map.put("origin", test1);
    map.put("tablename", test1);
    map.put(name, test2);
    map.put("indexname", "R_Test4");
    map.put("order", 1);
    map.put("altname", test3);
    lruIndexFields.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruIndexFields);
    // Auswertung
    final Table t = dbm.getTables().get(0);
    assertEquals(t.getName(), test1);
    assertEquals(t.getCategory(), test2);
    assertEquals(t.getXpk().getName(), "XPK" + test1);
    assertEquals(t.getFields().get(0).getName(), test2);
    // assertTrue(t.getData().get(0).getAltName().equals(test3));
    assertEquals(t.getFields().get(0).getTable().getName(), test1);
    assertEquals(t.getFields().get(0).getDomain(), DomainId.STRING);
    assertTrue(t.getFields().get(0).isRequired());
    assertTrue(t.getFields().get(0).isPartOfPrimaryKey());
    assertEquals(t.getIndizies().get(0).getName(), "XAK" + test3);
    assertEquals(t.getIndizies().get(0).getType(), Index.Type.XAK);
    assertEquals(t.getIndizies().get(0).getFieldList().get(0).getName(), test2);
    dbm.disconnectDb();
    assertTrue(!dbm.isReady());
  }

  @Test
  void testListWithCategories() {
    final DatabaseMockConnector dc = new DatabaseMockConnector();
    final DatabaseConnection dbm = new DatabaseManagerLean(dc);
    dbm.connectDb("", "", "");
    assertTrue(dbm.isReady());
    final Map<String, Object> map = new HashMap<>();
    map.put(name, test1);
    map.put("category", test2);
    final ArrayList<ResultUnit> lruTable = new ArrayList<>();
    lruTable.add(new ResultUnitImpl(map));
    dc.list2DResultUnit.add(lruTable);
    assertTrue(dbm.getListWithCategories().contains(test2));
  }

  @Test
  void testResultUnit() {
    final Map<String, Object> map = new HashMap<>();
    map.put(Strings.IDENT_NAME, test1);
    map.put(Strings.IDENT_UNIQUE, true);
    final ResultUnit ru = new ResultUnitImpl(map);
    assertEquals(ru.get(Strings.IDENT_NAME), test1);
    assertTrue(ru.getBoolean(Strings.IDENT_UNIQUE));
  }
}
