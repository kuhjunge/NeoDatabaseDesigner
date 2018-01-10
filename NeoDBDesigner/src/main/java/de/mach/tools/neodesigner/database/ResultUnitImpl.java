package de.mach.tools.neodesigner.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ist die Abbildung eines Records aus der Neo4J Datenbank.
 *
 * @author Chris Deter
 *
 */
class ResultUnitImpl implements ResultUnit {

  private final Map<String, String> data = new HashMap<>();

  /**
   * Konstruktor der eine Map umwandelt.
   *
   * @param r
   *          Map Representation mit Werten aus der Datenbank (Record.asMap())
   */
  ResultUnitImpl(final Map<String, Object> r) {
    for (final Entry<String, Object> bez : r.entrySet()) {
      if (bez.getKey().equals(Strings.IDENT_ISREQ) || bez.getKey().equals(Strings.IDENT_UNIQUE)) {
        data.put(bez.getKey(), Boolean.toString((Boolean) bez.getValue()));
      } else {
        if (bez.getValue() != null) {
          data.put(bez.getKey(), bez.getValue().toString());
        } else {
          data.put(bez.getKey(), Strings.NULL);
        }
      }
    }
  }

  @Override
  public String get(final String bez) {
    return data.get(bez);
  }

  @Override
  public Boolean getBoolean(final String bez) {
    return Boolean.valueOf(data.get(bez));
  }

}
