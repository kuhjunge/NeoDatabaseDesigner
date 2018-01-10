package de.mach.tools.neodesigner.database;

import de.mach.tools.neodesigner.database.DatabaseConnector;
import de.mach.tools.neodesigner.database.ResultUnit;

import java.util.ArrayList;
import java.util.List;

public class DatabaseMockConnector implements DatabaseConnector {
  public String addr = "";
  public String nutzername = "";
  public String pw = "";
  public String lastQuery = "";
  public List<List<ResultUnit>> list2DResultUnit = new ArrayList<>();
  public boolean databaseConnectionState = false;

  @Override
  public boolean isOnline() {
    return databaseConnectionState;
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    if (this.addr.equals(addr) && this.nutzername.equals(nutzername) && this.pw.equals(pw)) {
      databaseConnectionState = true;
      return true;
    }
    return false;
  }

  @Override
  public void disconnectDb() {
    databaseConnectionState = false;
  }

  @Override
  public List<ResultUnit> runCypher(final String q, final Object[] v) {
    lastQuery = QueryBuilder.queryToString(v, q);
    return list2DResultUnit.size() > 0 ? list2DResultUnit.remove(0) : new ArrayList<>();
  }

  @Override
  public List<ResultUnit> runCypher(final String q) {
    lastQuery = q;
    return list2DResultUnit.size() > 0 ? list2DResultUnit.remove(0) : new ArrayList<>();
  }
}