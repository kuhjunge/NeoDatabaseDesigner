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

package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.database.DatabaseConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Konkrete Umsetzung des Import Task für den Import von Kategorien.
 *
 * @author Chris Deter
 *
 */
public class ImportCategoryTask extends ImportTask {
  private final String input;

  public ImportCategoryTask(final DatabaseConnection db, final String in) {
    super(db);
    input = in;
  }

  @Override
  protected void doImport() {
    final List<Table> lt = parse();
    setMax(lt.size());
    updateProgressMessage(0, Strings.IMPORT_CATEGORIES);
    for (final Table t : lt) {
      getDatabaseCon().changeTableCategory(t.getName(), t.getCategory());
      updateBar();
    }
  }

  @Override
  protected List<Table> parse() {
    boolean classicImport = true;
    final List<Table> lt = new ArrayList<>();
    final List<String> items = Arrays.asList(input.split("\n"));
    if (items.get(0).contains(Strings.SEMICOLON)) {
      classicImport = false;
    }
    for (final String item : items) {
      if (!classicImport) {
        newImport(item, lt);
      } else {
        classicImport(item, lt);
      }
    }
    return lt;
  }

  private void newImport(final String item, final List<Table> lt) {
    final String[] subitems = item.split(Strings.SEMICOLON);
    if (subitems[0].equals("T")) {
      final Table t = new TableImpl(subitems[1]);
      t.setCategory(subitems[2].trim());
      lt.add(t);
    }
  }

  private void classicImport(final String item, final List<Table> lt) {
    final String[] subitems = item.split(",");
    final Table t = new TableImpl(subitems[0]);
    t.setCategory((subitems[subitems.length - 2] + "," + subitems[subitems.length - 1]).replaceAll("\"", "").trim());
    lt.add(t);
  }
}
