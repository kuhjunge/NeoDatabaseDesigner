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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.Generator;


/** Generator für eine CSV Datei die Tabellennamen und Kategorien enthält.
 *
 * @author Chris Deter */
public class CsvForFkeysGenerator implements Generator {

  private static final String DELIMITER = ";";
  private static final Logger LOG = Logger.getLogger(CsvForFkeysGenerator.class.getName());

  @Override
  public String generate(final List<Table> tableList) {
    final StringBuilder export = new StringBuilder();
    final List<Table> sortedList = new ArrayList<>(tableList);
    Collections.sort(sortedList);
    for (final Table t : sortedList) {
      final List<ForeignKey> sortedFkList = new ArrayList<>(t.getForeignKeys());
      Collections.sort(sortedFkList);
      for (final ForeignKey fk : sortedFkList) {
        // Sonderfall: Wir müssen die Felder nach PK Reihenfolge der referenzierten
        // Tabelle sortieren
        for (final Field fRefXpk : fk.getRefTable().getXpk().getFieldList()) {
          String str = fk.getNameFromAltName(fRefXpk.getName());
          final Field fOfTbl = fk.getTable().getField(str);
          if (fOfTbl != null) {
            generateRow(export, t, fk, fOfTbl);
          }
          else {
            CsvForFkeysGenerator.LOG
                .log(Level.SEVERE, "Could not Link " + fRefXpk.getName() + ":" + t.getName() + "->" + fk.getName());
          }
        }
      }
    }
    return export.toString();
  }

  /** generiert eine Reihe im CSV.
   *
   * @param export Stringbuilder für die gesamte Rückgabe
   * @param t Tabelle des Feldes
   * @param fk der Index dieser Reihe
   * @param f Feld dieser Reihe */
  private void generateRow(final StringBuilder export, final Table t, final ForeignKey fk, final Field f) {
    // TABLE
    export.append(t.getName().toUpperCase());
    export.append(CsvForFkeysGenerator.DELIMITER);
    // REFTABLE
    export.append(fk.getRefTable().getName().toUpperCase());
    export.append(CsvForFkeysGenerator.DELIMITER);
    // CONSTRAINT
    export.append(fk.getName().toUpperCase());
    export.append(CsvForFkeysGenerator.DELIMITER);
    // COLUMN
    export.append(f.getName().toUpperCase());
    export.append(CsvForFkeysGenerator.DELIMITER);
    // POSITION
    export.append(fk.getRefTable().getXpk().getOrder(fk.getAltName(f.getName()), false));
    export.append(CsvForFkeysGenerator.DELIMITER);
    // CASCADE
    export.append("N");

    export.append(Strings.EOL);
  }
}
