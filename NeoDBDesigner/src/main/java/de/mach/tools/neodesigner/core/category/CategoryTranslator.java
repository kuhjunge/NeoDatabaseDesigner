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

package de.mach.tools.neodesigner.core.category;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mach.tools.neodesigner.core.ConfigSaver;
import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Table;


/** Übersetzt die Kategorie in einen Text.
 *
 * @author Chris Deter */
public class CategoryTranslator {
  private final Map<String, String> categoryToName = new HashMap<>();
  private List<String> categories;
  // die Config Datei
  private ConfigSaver conf;

  public CategoryTranslator(ConfigSaver cs) {
    conf = cs;
    loadTranslation();
  }

  public List<String> getAllCategories() {
    return new ArrayList<>(categoryToName.keySet());
  }

  private List<String> getCategoriesManual(final Iterable<String> lt) {
    categories = getCategories(lt);
    return categories;
  }

  private List<String> getCategories(final Iterable<String> lt) {
    final List<String> cat = new ArrayList<>();
    for (final String t : lt) {
      if (!cat.contains(t)) {
        cat.add(t);
      }
      String title = t.split(",")[0];
      if (t.length() > 0 && !cat.contains(title)) {
        cat.add(title);
      }
    }
    return cat;
  }

  public List<String> getCategoriesFromTable(final List<Table> lt) {
    final List<String> catList = new ArrayList<>();
    for (final Table t : lt) {
      String cat = t.getCategory();
      if (!catList.contains(cat)) {
        catList.add(cat);
      }
      if (cat.length() > 0 && !catList.contains(cat.substring(0, 1))) {
        catList.add(cat.substring(0, 1));
      }
    }
    return getCategoriesManual(catList);
  }

  /** Läd die Kategorieübersetzung.
   *
   * @param c die Kategorien als Zahlenwert */
  public void load(final Iterable<String> c) {
    // ist die Config Datei vorhanden - dann lade die daten
    categories = getCategories(c);
    for (final String category : categories) {
      categoryToName.put(category, category);
      conf.setValue(Strings.SECTION + category, category);
    }
    loadTranslation();
  }

  private void loadTranslation() {
    categoryToName.clear();
    conf.load();
    for (Entry<String, String> entry : conf.getValueMap().entrySet()) {
      categoryToName.put(entry.getKey().substring(Strings.SECTION.length()), entry.getValue());
    }
  }

  /** Speichert eine neue Property Datei.
   *
   * @param catIdAndCatName Map mit allen Werten */
  public void save(Map<String, String> catIdAndCatName) {
    conf.clearValues();
    for (Entry<String, String> e : catIdAndCatName.entrySet()) {
      conf.setValue(Strings.SECTION + e.getKey(), e.getValue());
    }
    conf.save();
    loadTranslation();
  }

  /** Übersetzt die Nummer in den Kategorie Namen.
   *
   * @param number die Nummer
   * @return der Kategorie Name */
  public String translateNumberIntoName(final String number) {
    String ret = number;
    if (categoryToName.containsKey(number)) {
      ret = categoryToName.get(number);
    }
    return ret;
  }

  public Map<String, String> getCategoryTranslation(List<String> categories) {
    Map<String, String> ret = new HashMap<>();
    for (String cat : categories) {
      ret.put(cat, translateNumberIntoName(cat));
    }
    return ret;
  }
}
