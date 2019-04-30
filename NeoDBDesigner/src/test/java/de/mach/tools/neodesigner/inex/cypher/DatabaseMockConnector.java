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


public class DatabaseMockConnector implements DatabaseConnector {
  public String addr = "";
  public String nutzername = "";
  public String pw = "";
  public String lastQuery = "";
  public List<List<ResultUnit>> list2DResultUnit = new ArrayList<>();
  private boolean databaseConnectionState = false;

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

  @Override
  public boolean reconnect() {
    return false;
  }
}
