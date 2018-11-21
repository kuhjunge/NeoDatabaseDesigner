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

package de.mach.tools.neodesigner.database.cypher;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mach.tools.neodesigner.database.Strings;


/** ist die Abbildung eines Records aus der Neo4J Datenbank.
 *
 * @author Chris Deter */
public class ResultUnitImpl implements ResultUnit {

  private final Map<String, String> data = new HashMap<>();

  /** Konstruktor der eine Map umwandelt.
   *
   * @param r Map Representation mit Werten aus der Datenbank (Record.asMap()) */
  public ResultUnitImpl(final Map<String, Object> r) {
    for (final Entry<String, Object> bez : r.entrySet()) {
      if (bez.getKey().equals(Strings.IDENT_ISREQ) || bez.getKey().equals(Strings.IDENT_UNIQUE)) {
        try {
          data.put(bez.getKey(), Boolean.toString((Boolean) bez.getValue()));
        }
        catch (final ClassCastException e) {
          // Falls Daten von außen in die DB nachgetragen werden, ist der boolean evtl ein
          // String
          data.put(bez.getKey(), bez.getValue().toString());
        }
      }
      else {
        if (bez.getValue() != null) {
          data.put(bez.getKey(), bez.getValue().toString());
        }
        else {
          data.put(bez.getKey(), Strings.EMPTYSTRING);
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

  @Override
  public String toString() {
    return data.toString();
  }
}
