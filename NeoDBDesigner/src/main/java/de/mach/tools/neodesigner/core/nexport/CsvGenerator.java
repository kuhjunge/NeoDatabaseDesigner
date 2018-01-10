package de.mach.tools.neodesigner.core.nexport;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.List;

/**
 * Generator für eine CSV Datei die Tabellennamen und Kategorien enthält.
 *
 * @author Chris Deter
 *
 */
public class CsvGenerator implements Generator {

  @Override
  public String generate(final List<Table> tableList) {
    final StringBuilder export = new StringBuilder();
    for (final Table t : tableList) {
      for (final Field f : t.getData()) {
        generateRow(export, t, f);
      }
    }
    return export.toString();
  }

  /**
   * generiert eine Reihe im CSV
   *
   * @param export
   *          Stringbuilder für die gesamte Rückgabe
   * @param t
   *          Tabelle des Feldes
   * @param f
   *          Feld dieser Reihe
   */
  private void generateRow(final StringBuilder export, final Table t, final Field f) {
    // Tabellenname
    export.append(t.getName() + ",");
    // Feldname
    export.append(f.getName() + ",");
    // FeldTyp
    if (f.getTypeOfData().contains(",")) {
      export.append("\"" + f.getTypeOfData() + "\",");
    } else {
      export.append(f.getTypeOfData() + ",");
    }
    // Required NULL / NOT NULL
    export.append((f.isRequired() ? "NOT NULL" : "NULL") + ",");
    // Primär (Yes / No)
    export.append((f.isPartOfPrimaryKey() ? "Yes" : "No") + ",");
    // (Yes / No)
    export.append(",");
    // (Counter String40 Datetime?)
    export.append(",");
    // XIE | RefTable
    export.append(",");
    // (Field / RefTable)
    export.append(",");
    // RefTable
    export.append(",");
    // Field of RefTable
    export.append(",");
    // Kategorie 1, Kategorie 2
    if (t.getCategory().equals(Strings.CATEGORYNONE)) {
      export.append("\"0,0\"");
    } else {
      export.append("\"" + t.getCategory().replaceAll("\r", "").replaceAll("\n", "") + "\"");
    }
    export.append(Strings.EOL);
  }

}
