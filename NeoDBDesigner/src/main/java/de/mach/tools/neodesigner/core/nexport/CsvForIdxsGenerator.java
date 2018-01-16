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

package de.mach.tools.neodesigner.core.nexport;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generator für eine CSV Datei die Tabellennamen und Kategorien enthält.
 *
 * @author Chris Deter
 *
 */
public class CsvForIdxsGenerator implements Generator {

  private static final String DELIMITER = Strings.SEMICOLON;

  @Override
  public String generate(final List<Table> tableList) {
    final StringBuilder export = new StringBuilder();
    final List<Table> sortedList = new ArrayList<>(tableList);
    Collections.sort(sortedList);
    for (final Table t : sortedList) {
      final List<Index> sortedIdxList = new ArrayList<>(t.getIndizies());
      for (final Index i : sortedIdxList) {
        for (final Field f : i.getFieldList()) {
          // Generierte Indizes & und Indizes die identisch mit dem PK sind, filtern
          if (!(t.getXpk().getFieldList().containsAll(i.getFieldList())
              && i.getFieldList().containsAll(t.getXpk().getFieldList())
              && i.getName().substring(i.getName().length() - Strings.IMPORTGENERATEDINDEXMARKER.length())
                  .equals(Strings.IMPORTGENERATEDINDEXMARKER))) {
            // Zusätzliches Filtern von _g Indizes
            if (!i.getName().substring(i.getName().length() - Strings.IMPORTGENERATEDINDEXMARKER.length())
                .equals(Strings.IMPORTGENERATEDINDEXMARKER)) {
              generateRow(export, t, i, f);
            }
          }
        }
      }
    }
    return export.toString();

  }

  /**
   * generiert eine Reihe im CSV.
   *
   * @param export
   *          Stringbuilder für die gesamte Rückgabe
   * @param t
   *          Tabelle des Feldes
   * @param i
   *          der Index dieser Reihe
   * @param f
   *          Feld dieser Reihe
   */
  private void generateRow(final StringBuilder export, final Table t, final Index i, final Field f) {
    // TABLE
    export.append(t.getName().toUpperCase() + CsvForIdxsGenerator.DELIMITER);
    // CONSTRAINT
    export.append(i.getName().toUpperCase() + CsvForIdxsGenerator.DELIMITER);
    // COLUMN
    export.append(f.getName().toUpperCase() + CsvForIdxsGenerator.DELIMITER);
    // POSITION
    export.append(i.getOrder(f.getName()) + CsvForIdxsGenerator.DELIMITER);
    // UNIQUE
    export.append(i.isUnique() ? Strings.Y : Strings.N);

    export.append(Strings.EOL);
  }
}
