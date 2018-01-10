package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.database.DatabaseConnection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Konkrete Umsetzung des Import Task für den Import von Kategorien.
 *
 * @author Chris Deter
 *
 */
public class ImportCategoryTask extends ImportTask {
  public ImportCategoryTask(final DatabaseConnection db, final String in) {
    super(db, in);
  }

  /**
   * Parst eine Liste mit Kommagetrennten Werten in eine Map mit zwei Strings.
   * Dies dient dazu die Tabelle mit ihrer Kategorie zu zu importieren. Die
   * Kategorie enthält ein Komma
   * 
   * @param input
   * @return
   */
  private Map<String, String> parse(final String input) {
    final Map<String, String> map = new HashMap<>();
    final List<String> items = Arrays.asList(input.split("\n"));
    for (final String item : items) {
      final String[] subitems = item.split(",");
      map.put(subitems[0],
          (subitems[subitems.length - 2] + "," + subitems[subitems.length - 1]).replaceAll("\"", "").trim());
    }
    return map;
  }

  @Override
  protected void doImport(final String input) {
    final Map<String, String> map = parse(input);
    setMax(map.size());
    updateProgressMessage(0, "writing Kategories");
    for (final Map.Entry<String, String> entry : map.entrySet()) {
      getDatabaseCon().changeTableCategory(entry.getKey(), entry.getValue());
      updateBar();
    }
  }
}
