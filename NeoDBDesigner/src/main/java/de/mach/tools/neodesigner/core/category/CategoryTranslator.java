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

package de.mach.tools.neodesigner.core.category;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Übersetzt die Kategorie in einen Text.
 *
 * @author Chris Deter
 *
 */
public class CategoryTranslator {
  private static final Logger LOG = Logger.getLogger(CategoryTranslator.class.getName());
  private final File configPath = new File(System.getenv(Strings.CONF_LOC) + File.separatorChar + Strings.SOFTWARENAME);
  protected final Map<String, String> categoryToName = new HashMap<>();
  private List<String> categories;
  // die Config Datei
  private final File configFile = new File(configPath.getAbsolutePath() + File.separatorChar + Strings.CATEGORYFILE);

  /**
   * Erstellt einen Translator für die Kategorien.
   *
   * @param lt
   *          die Liste aller Tabellen, dessen Kategorien übersetzt werden sollen
   */
  public CategoryTranslator(final List<Table> lt) {
    // die Config Datei
    final File confFile = new File(configPath.getAbsolutePath() + File.separatorChar + Strings.CATEGORYFILE);
    // ist die Config Datei vorhanden - dann lade die daten
    if (confFile.exists()) {
      try (FileInputStream fis = new FileInputStream(confFile)) {
        loadProperties(fis, getCategoriesFromTable(lt));
      } catch (final IOException e) {
        CategoryTranslator.LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
  }

  public CategoryTranslator() {
  }

  /**
   * Läd die Kategorieübersetzung.
   *
   * @param c
   *          die Kategorien als Zahlenwert
   */
  public void load(final Iterable<String> c) {
    // ist die Config Datei vorhanden - dann lade die daten
    if (configFile.exists()) {
      try (FileInputStream fis = new FileInputStream(configFile)) {
        loadProperties(fis, getCategories(c));
      } catch (final IOException e) {
        CategoryTranslator.LOG.log(Level.SEVERE, e.toString(), e);
      }
    } else {
      for (final String category : categories) {
        categoryToName.put(category, category);
      }
    }

  }

  public List<String> getAllCategories() {
    return new ArrayList<>(categoryToName.keySet());
  }

  protected List<String> getCategoriesFromTable(final List<Table> lt) {
    final List<String> cat = new ArrayList<>();
    for (final Table t : lt) {
      if (!cat.contains(t.getCategory())) {
        cat.add(t.getCategory());
      }
    }
    return getCategories(cat);
  }

  protected List<String> getCategories(final Iterable<String> lt) {
    final List<String> cat = new ArrayList<>();
    for (final String t : lt) {
      if (!cat.contains(t)) {
        cat.add(t);
      }
      if (t.length() > 0 && !cat.contains(t.substring(0, 1))) {
        cat.add(t.substring(0, 1));
      }
    }
    categories = cat;
    return cat;
  }

  /**
   * Speichert eine neue Property Datei.
   *
   * @param catIdAndCatName
   *          Map mit allen Werten
   */
  public void save(final Map<String, String> catIdAndCatName) {
    try (FileOutputStream fos = new FileOutputStream(configFile)) {
      saveProperties(fos, catIdAndCatName);
    } catch (final IOException e) {
      CategoryTranslator.LOG.log(Level.SEVERE, e.toString(), e);
    }
  }

  private void saveProperties(final FileOutputStream fos, final Map<String, String> catIdAndCatName)
      throws IOException {
    final java.util.Properties prop = new java.util.Properties();
    for (final Entry<String, String> c : catIdAndCatName.entrySet()) {
      prop.put(Strings.SECTION + c.getKey(), c.getValue());
    }
    prop.store(fos, Strings.CATEGORYFILE);
    fos.flush();
  }

  private void loadProperties(final FileInputStream fis, final List<String> categories) throws IOException {
    final java.util.Properties prop = new java.util.Properties();
    prop.load(fis);
    for (final String category : categories) {
      final String catName = prop.getProperty(Strings.SECTION + category);
      if (catName != null) {
        categoryToName.put(category, catName);
      }
    }
    for (final Entry<Object, Object> obj : prop.entrySet()) {
      final String key = obj.getKey().toString().replace(Strings.SECTION, "");
      if (!categoryToName.containsKey(key)) {
        categoryToName.put(key, obj.getValue().toString());
      }
    }
  }

  /**
   * Übersetzt die Nummer in den Kategorie Namen.
   *
   * @param number
   *          die Nummer
   * @return der Kategorie Name
   */
  public String translateNumberIntoName(final String number) {
    if (categoryToName.containsKey(number)) {
      return categoryToName.get(number);
    }
    return number;
  }
}
