package de.mach.tools.neodesigner.database;

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
import org.neo4j.driver.v1.exceptions.ClientException;

/**
 * Implementation des Database Connector Interface um das Neo4J Framework zu
 * kapseln.
 *
 * @author Chris Deter
 *
 */
public class DatabaseConnectorImpl implements DatabaseConnector {
  private Session session;
  private Driver driver;
  private static final Logger LOG = Logger.getLogger(DatabaseConnectorImpl.class.getName());

  @Override
  public boolean isOnline() {
    if (session != null) {
      return session.isOpen();
    } else {
      return false;
    }
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    driver = GraphDatabase.driver(addr, AuthTokens.basic(nutzername, pw));
    try {
      session = driver.session();
      return true;
    } catch (final ClientException e) {
      DatabaseConnectorImpl.LOG.log(Level.SEVERE, e.toString(), e);
      return false;
    }
  }

  @Override
  public void disconnectDb() {
    session.close();
    driver.close();
  }

  @Override
  public List<ResultUnit> runCypher(final String q, final Object[] v) {
    return createDatabaseWrapperUnit(session.run(q, Values.parameters(v)));
  }

  @Override
  public List<ResultUnit> runCypher(final String q) {
    return createDatabaseWrapperUnit(session.run(q));
  }

  /**
   * Verarbeitet das Ergebnis einer Datenbankanfrage und wandelt das
   * StatementReslut in eine Liste von ResultUnits um.
   *
   * @param sr
   *          das Statementresult der Datenbank
   * @return eine Liste mit ResultUnits welche die Datenbankergebnisse enthalten
   */
  private List<ResultUnit> createDatabaseWrapperUnit(final StatementResult sr) {
    final List<ResultUnit> ret = new ArrayList<>();
    for (final Record r : sr.list()) {
      ret.add(new ResultUnitImpl(r.asMap()));
    }
    return ret;
  }
}
