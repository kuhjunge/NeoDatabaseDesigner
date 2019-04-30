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

package de.mach.tools.neodesigner.inex.nexport;


import java.util.ArrayList;
import java.util.List;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.DatabaseConnection;
import de.mach.tools.neodesigner.inex.Generator;
import de.mach.tools.neodesigner.inex.cypher.DatabaseConnector;
import de.mach.tools.neodesigner.inex.cypher.DatabaseManagerLean;
import de.mach.tools.neodesigner.inex.cypher.QueryBuilder;
import de.mach.tools.neodesigner.inex.cypher.ResultUnit;


public class CypherGenerator implements Generator, DatabaseConnector {
  private final StringBuilder query = new StringBuilder();

  @Override
  public String generate(final List<Table> tableList) {
    final DatabaseConnection dbcon = new DatabaseManagerLean(this);
    final QueryBuilder qb = new QueryBuilder();

    for (final Table table : tableList) {
      dbcon.importTable(table);
    }
    for (final Table table : tableList) {
      for (final ForeignKey fk : table.getForeignKeys()) {
        dbcon.importForeignKey(fk);
        query.append(qb);
      }
    }
    return query.toString().replaceAll("CREATE", Strings.EOL + "CREATE").replaceAll("MATCH", Strings.EOL + "MATCH")
        .replaceAll("MERGE", Strings.EOL + "MERGE");
  }

  @Override
  public boolean isOnline() {
    return true;
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    return true;
  }

  @Override
  public void disconnectDb() {
    // Nur für Import gefaked
  }

  @Override
  public List<ResultUnit> runCypher(final String q, final Object[] v) {
    query.append(QueryBuilder.queryToString(v, q));
    query.append(Strings.SEMICOLON);
    query.append(Strings.EOL);
    return new ArrayList<>();
  }

  @Override
  public List<ResultUnit> runCypher(final String q) {
    query.append(q);
    query.append(Strings.SEMICOLON);
    query.append(Strings.EOL);
    return new ArrayList<>();
  }

  @Override
  public boolean reconnect() {
    return true;
  }
}
