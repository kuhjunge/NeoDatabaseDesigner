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

package de.mach.tools.neodesigner.inex;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.ui.Strings;


/** Task zum Laden von den Daten aus der Datenbank.
 *
 * @author Chris Deter */
public class LoadFromDbTask extends Task<List<Table>> {
  private static final Logger LOG = Logger.getLogger(LoadFromDbTask.class.getName());
  private final DatabaseConnection dbm;
  private final long start = new Date().getTime();

  public LoadFromDbTask(final DatabaseConnection databaseManager) {
    dbm = databaseManager;
  }

  @Override
  protected List<Table> call() {
    List<Table> lt = new ArrayList<>();
    if (dbm.isReady()) {
      lt = dbm.getTables();
      LoadFromDbTask.LOG.log(Level.INFO, () -> Strings.LOADFROMDB + (new Date().getTime() - start));
    }
    return lt;
  }
}
