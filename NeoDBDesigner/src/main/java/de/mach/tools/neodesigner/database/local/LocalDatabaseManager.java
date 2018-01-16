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

package de.mach.tools.neodesigner.database.local;

import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.database.DatabaseConnection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// TODO: Work in Progress, hat noch einige Bugs beim verwenden!
/**
 * Diese Klasse kann als "InMemory" Datenbank verwendet werden.
 *
 * @author cd
 *
 */
public class LocalDatabaseManager extends DataModelManager implements DatabaseConnection {
  private boolean online = false;

  public LocalDatabaseManager(final boolean b) {
    online = b;
  }

  @Override
  public boolean deleteDatabase() {
    clear();
    return true;
  }

  @Override
  public boolean isReady() {
    return online;
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    online = true;
    return true;
  }

  @Override
  public void disconnectDb() {
    online = false;
    clear();
  }

  @Override
  public void createIndexOnDb() {
    // nicht notwendig bei einer In Memory DB, wird aber so in Interface verlangt.
  }

  @Override
  public void importTable(final Table t) {
    insertNewTable(t);
  }

  @Override
  public void importForeignKey(final ForeignKey i) {
    insertNewForeignKey(i);
  }

  @Override
  public Map<String, Integer> getDatabaseStats() {
    final Map<String, Integer> res = new LinkedHashMap<>();
    // TODO: muss noch implementiert werden
    return res;
  }

  @Override
  public List<String> getListWithTableNames() {
    final List<String> tablenames = new ArrayList<>();
    for (final Table t : getTables()) {
      tablenames.add(t.getName());
    }
    return tablenames;
  }

  @Override
  public int getForeignKeyNumber(final int lenght) {
    int maxNum = 0;
    int number;
    for (final Table t : getTables()) {
      for (final ForeignKey fk : t.getForeignKeys()) {
        number = Integer.parseInt(fk.getName().substring(2, 2 + lenght));
        if (number > maxNum) {
          maxNum = number;
        }
      }
    }
    return ++maxNum;
  }

  @Override
  public List<String> getListWithCategories() {
    final List<String> tablenames = new ArrayList<>();
    for (final Table t : getTables()) {
      if (!tablenames.contains(t.getCategory())) {
        tablenames.add(t.getCategory());
      }
    }
    return tablenames;
  }
}
