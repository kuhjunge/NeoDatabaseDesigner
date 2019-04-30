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


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Values;


/** Implementation des Database Connector Interface um das Neo4J Framework zu kapseln.
 *
 * @author Chris Deter */
public class DatabaseConnectorImpl implements DatabaseConnector {
  private Session session;
  private Driver driver;
  private String addr;
  private String user;
  private String pw;

  private static final Logger LOG = Logger.getLogger(DatabaseConnectorImpl.class.getName());

  @Override
  public boolean isOnline() {
    return session != null && session.isOpen();
  }

  @Override
  public boolean reconnect() {
    return connectDb(addr, user, pw);
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    this.addr = addr;
    user = nutzername;
    this.pw = pw;
    try {
      driver = GraphDatabase.driver(addr, AuthTokens.basic(nutzername, pw));
      session = driver.session();
      if (session.isOpen()) {
        return true;
      }
    }
    catch (final Exception e) {
      DatabaseConnectorImpl.LOG.log(Level.SEVERE, e.toString(), e);
    }
    return false;
  }

  @Override
  public void disconnectDb() {
    session.close();
    driver.close();
  }

  @Override
  public List<ResultUnit> runCypher(final String q, final Object[] v) {
    return createDatabaseWrapperUnit(q, v);
  }

  @Override
  public List<ResultUnit> runCypher(final String q) {
    return createDatabaseWrapperUnit(q, null);
  }

  /** Verarbeitet das Ergebnis einer Datenbankanfrage und wandelt das StatementReslut in eine Liste von ResultUnits um.
   *
   * @param q Die Query
   * @param v Die Queryparameter
   * @return eine Liste mit ResultUnits welche die Datenbankergebnisse enthalten */
  private synchronized List<ResultUnit> createDatabaseWrapperUnit(final String q, final Object[] v) {
    StatementResult sr;
    if (v == null) {
      sr = session.run(q);
    }
    else {
      sr = session.run(q, Values.parameters(v));
    }
    final List<ResultUnit> ret = new ArrayList<>();
    for (final Record r : sr.list()) {
      ret.add(new ResultUnitImpl(r.asMap()));
    }
    return ret;
  }
}
