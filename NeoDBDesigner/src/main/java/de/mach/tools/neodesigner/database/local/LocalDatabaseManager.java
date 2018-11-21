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

package de.mach.tools.neodesigner.database.local;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Relations.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.Strings;


/** Diese Klasse kann als "InMemory" Datenbank verwendet werden.
 *
 * @author cd */
public class LocalDatabaseManager extends DataModelManager implements DatabaseConnection {
  private boolean online;

  public LocalDatabaseManager(final boolean online) {
    this.online = online;
  }

  @Override
  public boolean connectDb(final String addr, final String nutzername, final String pw) {
    online = true;
    return true;
  }

  @Override
  public void createIndexOnDb() {
    // nicht notwendig bei einer In Memory DB, wird aber so in Interface verlangt.
  }

  @Override
  public boolean deleteDatabase() {
    clear();
    return true;
  }

  @Override
  public void disconnectDb() {
    online = false;
    clear();
  }

  @Override
  public Map<String, Integer> getDatabaseStats() {
    final List<Table> lt = super.getTables();
    final int tables = lt.size();
    int column = 0;
    int index = 0;
    int ref = 0;
    int xpk = 0;
    for (final Table t : lt) {
      column += t.getFields().size();
      xpk += t.getXpk().getFieldList().size();
      for (final Index i : t.getIndizies()) {
        index += i.getFieldList().size();
      }
      for (final ForeignKey i : t.getForeignKeys()) {
        ref += i.getIndex().getFieldList().size();
      }
    }
    final Map<String, Integer> res = new LinkedHashMap<>();
    res.put(Strings.EMPTYSTRING, tables + column);
    res.put(Strings.NODE_TABLE, tables);
    res.put(Strings.NODE_COLUMN, column);
    res.put(Strings.RELTEXT, xpk + column + index + ref);
    res.put(Strings.RELTEXT + Type.XPK, xpk);
    res.put(Strings.RELTEXT + Type.DATA, column);
    res.put(Strings.RELTEXT + Type.INDEX, index);
    res.put(Strings.RELTEXT + Type.REFERENCE, ref);
    return res;
  }

  @Override
  public int getForeignKeyNumber(final int lenght) {
    int number;
    int foreignKeyCounter = 0;
    for (final Table t : super.getTables()) {
      for (final ForeignKey fk : t.getForeignKeys()) {
        if (fk.getName().length() >= 2 + lenght) {
          number = Util.tryParseInt(fk.getName().substring(2, 2 + lenght));
          if (number > foreignKeyCounter) {
            foreignKeyCounter = number;
          }
        }
      }
    }
    return foreignKeyCounter;
  }

  @Override
  public List<String> getListWithCategories() {
    final List<String> tablenames = new ArrayList<>();
    for (final Table t : super.getTables()) {
      if (!tablenames.contains(t.getCategory())) {
        tablenames.add(t.getCategory());
      }
    }
    return tablenames;
  }

  @Override
  public List<String> getListWithTableNames() {
    final List<String> tablenames = new ArrayList<>();
    for (final Table t : super.getTables()) {
      tablenames.add(t.getName());
    }
    return tablenames;
  }

  @Override
  public List<Table> getTables() {
    final List<Table> ret = new ArrayList<>();
    final List<Table> dm = super.getTables();
    final List<ForeignKey> fkl = new ArrayList<>();
    for (final Table t : dm) {
      ret.add(new TableImpl(t));
      fkl.addAll(t.getForeignKeys());
    }
    for (final ForeignKey fk : fkl) {
      final Table t = ret.get(ret.indexOf(fk.getTable()));
      final Table reft = ret.get(ret.indexOf(fk.getRefTable()));
      final ForeignKey fknew = new ForeignKeyImpl(fk, t, reft);
      t.addForeignKey(fknew, false);
      reft.addForeignKey(fknew, true);
    }
    return ret;
  }

  @Override
  public void importForeignKey(final ForeignKey i) {
    final Optional<Table> t = getTable(i.getTable().getName());
    final Optional<Table> reft = getTable(i.getRefTable().getName());
    if (t.isPresent() && reft.isPresent()) {
      insertNewForeignKey(new ForeignKeyImpl(i, t.get(), reft.get()));
    }
  }

  @Override
  public void importTable(final Table o) {
    insertNewTable(new TableImpl(o));
  }

  @Override
  public boolean isReady() {
    return online;
  }
}
