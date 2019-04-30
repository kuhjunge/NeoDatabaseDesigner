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

package de.mach.tools.neodesigner.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Relations;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.inex.ImportTask;
import de.mach.tools.neodesigner.inex.legacyImport.ImportCategoryTask;
import de.mach.tools.neodesigner.inex.legacyImport.ImportSqlPartialTask;
import de.mach.tools.neodesigner.inex.nexport.pdf.PdfConf;
import de.mach.tools.neodesigner.inex.nimport.ImportCsvTask;
import de.mach.tools.neodesigner.inex.nimport.ImportJsonTask;
import de.mach.tools.neodesigner.inex.nimport.ImportSqlTask;
import de.mach.tools.neodesigner.ui.GuiConf;


/** Implementation des Model Interfaces.
 *
 * @author Chris Deter */
public class ModelImpl implements Model {
  private final DataModelManager data;
  private final Configuration config = new Configuration();
  private final CategoryTranslator categoryTranslator;
  private Validator validator;
  private int lastNumber = 100;

  /** Konstruktor Model. */
  public ModelImpl(ConfigSaver conf, ConfigSaver categories) {
    data = new DataModelManager();
    config.init(conf);
    categoryTranslator = new CategoryTranslator(categories);
  }


  @Override
  public void connect() {
    config.save();
    // Klasse ausgelagert für Tests
    categoryTranslator.load(getAllCategories());
    validator = new Validator(config.getWordLength(), config.getWordLength(), config.getUniqueTableLength(),
                              getSaveObj());
  }

  @Override
  public boolean deleteDatabase() {
    data.clear();
    return true;
  }

  @Override
  public String getAddrOfDb() {
    return config.getAddrOfDb();
  }

  @Override
  public List<String> getAllCategories() {
    return categoryTranslator.getCategoriesFromTable(data.getTables());
  }

  @Override
  public List<Table> getAllTables() {
    return data.getTables();
  }

  @Override
  public List<CategoryObj> getCategorySelection() {
    final List<CategoryObj> co = new ArrayList<>();
    for (final String cat : categoryTranslator.getAllCategories()) {
      co.add(new CategoryObj(cat, 0, categoryTranslator.translateNumberIntoName(cat)));
    }
    Collections.sort(co);
    for (int i = 0; i < co.size(); i++) {
      co.get(i).setSortId(i + 1);
    }
    return co;
  }

  @Override
  public Map<String, Integer> getDatabaseStats() {
    final List<Table> lt = data.getTables();
    final int tables = lt.size();
    int column = 0;
    int index = 0;
    int ref = 0;
    int xpk = 0;
    for (final Table t : lt) {
      column += t.getFields().size();
      xpk += t.getXpk().getFieldList().size();
      for (final Index i : t.getIndizies()) {
        index += i.getFieldList().size();
      }
      for (final ForeignKey i : t.getForeignKeys()) {
        ref += i.getIndex().getFieldList().size();
      }
    }
    final Map<String, Integer> res = new LinkedHashMap<>();
    res.put(de.mach.tools.neodesigner.inex.cypher.Strings.EMPTYSTRING, tables + column);
    res.put(de.mach.tools.neodesigner.inex.cypher.Strings.NODE_TABLE, tables);
    res.put(de.mach.tools.neodesigner.inex.cypher.Strings.NODE_COLUMN, column);
    res.put(de.mach.tools.neodesigner.inex.cypher.Strings.RELTEXT, xpk + column + index + ref);
    res.put(de.mach.tools.neodesigner.inex.cypher.Strings.RELTEXT + Relations.Type.XPK, xpk);
    res.put(de.mach.tools.neodesigner.inex.cypher.Strings.RELTEXT + Relations.Type.DATA, column);
    res.put(de.mach.tools.neodesigner.inex.cypher.Strings.RELTEXT + Relations.Type.INDEX, index);
    res.put(de.mach.tools.neodesigner.inex.cypher.Strings.RELTEXT + Relations.Type.REFERENCE, ref);
    return res;
  }

  @Override
  public GuiConf getGuiConf() {
    return config;
  }

  @Override
  public List<String> getListWithTableNames() {
    final List<String> tablenames = new ArrayList<>();
    for (final Table t : data.getTables()) {
      tablenames.add(t.getName());
    }
    return tablenames;
  }

  @Override
  public Table getNewTable(final String tableName) {
    return new TableImpl(tableName);
  }

  @Override
  public int getNextFkNumber(final int n) {
    // Startnummer setzen
    if (n > lastNumber) {
      lastNumber = n;
    }
    // Datenbank befragen
    final int actualLength = String.valueOf(lastNumber).length();
    final int dbnumber = getForeignKeyNumber(actualLength);
    if (dbnumber > lastNumber) {
      lastNumber = dbnumber;
    }
    // Stelle Wechseln
    final int nextLength = String.valueOf(lastNumber + 10).length();
    if (nextLength > actualLength) {
      lastNumber = getForeignKeyNumber(nextLength);
    }
    return ++lastNumber;
  }

  private int getForeignKeyNumber(final int length) {
    int number;
    // bei Länge 3 mit 100 starten, bei Länge 4 mit 1000 usw...
    int foreignKeyCounter = (int) Math.pow(10, length - 1);
    for (final Table t : data.getTables()) {
      for (final ForeignKey fk : t.getForeignKeys()) {
        if (fk.getName().length() >= 2 + length) {
          number = Util.tryParseInt(fk.getName().substring(2, 2 + length));
          if (number > foreignKeyCounter) {
            foreignKeyCounter = number;
          }
        }
      }
    }
    return foreignKeyCounter;
  }

  @Override
  public Integer getNextNumberForIndex(final List<Index> li) {
    Integer number = 1;
    boolean change = true;
    while (change) {
      change = false;
      for (final Index i : li) { // Prüft alle indizes nach der höchsten Nummer
        if (number < 10 && i.getName().substring(3, 4).contains(number.toString())
            || number >= 10 && i.getName().substring(3, 5).contains(number.toString())) {
          number++;
          change = true;
        }
      }
    }
    return number;
  }

  @Override
  public PdfConf getPdfConfig() {
    return config;
  }

  @Override
  public Save getSaveObj() {
    return data;
  }

  @Override
  public String[] getSelectDatatype() {
    return config.getSelectDataType();
  }

  @Override
  public synchronized Optional<Table> getTable(final String tablename) {
    Optional<Table> result = Optional.empty();
    if (data.hasTable(tablename)) {
      result = data.getTable(tablename);
    }
    return result;
  }

  @Override
  public Validator getValidator() {
    return validator;
  }

  @Override
  public int getWordLength() {
    return config.getWordLength();
  }

  @Override
  public void saveCategoryList(final Map<String, String> categories) {
    categoryTranslator.save(categories);
  }

  @Override
  public ImportTask importTask(final String input, final char type, final String inputPath) {
    ImportTask it;
    if (type == Strings.IMPORTTYPESQL) {
      it = new ImportSqlTask(input);
      config.setPathImportSql(inputPath);
    }
    else if (type == Strings.IMPORTTYPEJSON) {
      it = new ImportJsonTask(categoryTranslator, input);
      config.setPathExportGeneric(inputPath);
    }
    else if (type == Strings.IMPORTTYPECSV) {
      // Legacy IMPORT CATEGORY
      it = new ImportCategoryTask(data, input, true);
      config.setPathImportCat(inputPath);
    }
    else {
      // Legacy IMPORT SQL
      it = new ImportSqlPartialTask(input, data.getTables());
      config.setPathImportCat(inputPath);
    }
    return it;
  }

  @Override
  public ImportTask importFolderTask(final String path, final String tbls, final String cols, final String pKeys,
                                     final String idxs, final String fKeys, final String metaTbls,
                                     final String metaCols) {
    categoryTranslator.load(getAllCategories());
    config.setPathImportCsv(path);
    config.save();
    return new ImportCsvTask(tbls, cols, idxs, pKeys, fKeys, metaTbls, metaCols);
  }

  @Override
  public void reloadCategories() {
    categoryTranslator.load(getAllCategories());
  }

  @Override
  public void importTables(List<Table> tl) {
    data.addAll(tl);
  }

  @Override
  public void addDataModelListener(DatamodelListener listener) {
    data.clearListener();
    data.addListener(listener);
  }

  @Override
  public Map<String, String> getCategoryTranslation() {
    return categoryTranslator.getCategoryTranslation(getAllCategories());
  }
}
