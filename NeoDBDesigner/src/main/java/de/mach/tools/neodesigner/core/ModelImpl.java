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


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.nexport.CsvForColsGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForFkeysGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForIdxsGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForMetaGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForPkeysGenerator;
import de.mach.tools.neodesigner.core.nexport.CsvForTblsGenerator;
import de.mach.tools.neodesigner.core.nexport.Generator;
import de.mach.tools.neodesigner.core.nexport.pdf.PdfConf;
import de.mach.tools.neodesigner.core.nimport.ImportBulkCsvTask;
import de.mach.tools.neodesigner.core.nimport.ImportCategoryTask;
import de.mach.tools.neodesigner.core.nimport.ImportCsvTask;
import de.mach.tools.neodesigner.core.nimport.ImportSqlTask;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnector;
import de.mach.tools.neodesigner.database.cypher.DatabaseManagerLean;
import de.mach.tools.neodesigner.database.local.DataModelManager;
import de.mach.tools.neodesigner.database.local.LocalDatabaseManager;
import de.mach.tools.neodesigner.ui.GuiConf;


/** Implementation des Model Interfaces.
 *
 * @author Chris Deter */
public class ModelImpl implements Model {
  private static final Logger LOG = Logger.getLogger(ModelImpl.class.getName());
  private DatabaseConnection db;
  private final DatabaseConnector dc;
  private final DataModelManager dmm;
  private final Configuration config;
  private final CategoryTranslator ct;
  private Validator val;
  private int lastNumber = 100;

  /** Konstruktor Model. */
  public ModelImpl(final DatabaseConnector dc, final CategoryTranslator ctrans) {
    dmm = new DataModelManager();
    config = new Configuration();
    config.init();
    ct = ctrans;
    this.dc = dc;
  }

  @Override
  public void addTableList(final List<Table> lt) {
    dmm.clear();
    dmm.addAll(lt);
  }

  @Override
  public ImportTask bulkimportTask(final File folder) {
    return new ImportBulkCsvTask(db, folder.toPath().toString(), config.getNeoDbStarterLocation());
  }

  @Override
  public boolean connectDb(final String dbadr, final String usr, final String pw) {
    db = new DatabaseManagerLean(dc);
    if (db.connectDb(dbadr, usr, pw)) {
      config.save(dbadr, usr, pw);
      ct.load(getAllCategories());
      val = new Validator(config.getWordLength(), config.getWordLength(), config.getUniqueTableLength(), getSaveObj());
      return true;
    }
    return false;
  }

  @Override
  public void connectLocalDb() {
    config.save();
    connectLocalDb(new LocalDatabaseManager(true));
  }

  protected void connectLocalDb(final LocalDatabaseManager db) {
    this.db = db;
    ct.load(getAllCategories());
    val = new Validator(config.getWordLength(), config.getWordLength(), config.getUniqueTableLength(), getSaveObj());
  }

  @Override
  public Observable dataModelObservable() {
    return dmm;
  }

  @Override
  public boolean deleteDatabase() {
    final boolean b = db.deleteDatabase();
    if (b) {
      dmm.clear();
    }
    return b;
  }

  @Override
  public void disconnectDb() {
    db.disconnectDb();
    dmm.clear();
  }

  private void exportImportCategoryFile(final File srcCatFile, final File targetCatFile) {
    if (srcCatFile.exists()) {
      try {
        Files.copy(srcCatFile.toPath(), targetCatFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        ct.load(getAllCategories());
      }
      catch (final IOException e) {
        ModelImpl.LOG.log(Level.SEVERE, "Could not copy " + Strings.CATEGORYFILE, e);
      }
    }
    else {
      ModelImpl.LOG.log(Level.INFO, "Could not find " + Strings.CATEGORYFILE + ". Skip Category File.");
    }
  }

  @Override
  public String getAddrOfDb() {
    return config.getAddrOfDb();
  }

  @Override
  public List<String> getAllCategories() {
    return db.getListWithCategories();
  }

  @Override
  public List<Table> getAllTables() {
    if (dmm.isEmpty() && db.isReady()) {
      dmm.addAll(db.getTables());
    }
    return dmm.getTables();
  }

  @Override
  public List<CategoryObj> getCategorySelection() {
    final List<CategoryObj> co = new ArrayList<>();
    for (final String cat : ct.getAllCategories()) {
      co.add(new CategoryObj(cat, 0, ct.translateNumberIntoName(cat)));
    }
    Collections.sort(co);
    for (int i = 0; i < co.size(); i++) {
      co.get(i).setSortId(i + 1);
    }
    return co;
  }

  @Override
  public Map<String, Integer> getDatabaseStats() {
    return db.getDatabaseStats();
  }

  @Override
  public GuiConf getGuiConf() {
    return config;
  }

  @Override
  public List<String> getListWithTableNames() {
    return db.getListWithTableNames();
  }

  /** gibt ein File Objekt der Neo4J Start Batch zurück.
   *
   * @return die Start Datei */
  private File getNeoServerStarterFile() {
    return new File(config.getNeoDbStarterLocation());
  }

  @Override
  public Table getnewTable(final String tableName) {
    return new TableImpl(tableName);
  }

  @Override
  public int getNextFkNumber(final int n) {
    // Startnummer setzen
    if (n > lastNumber) {
      lastNumber = n;
    }
    // Datenbank befragen
    final int temp = db.getForeignKeyNumber(String.valueOf(lastNumber).length());
    if (temp > lastNumber) {
      lastNumber = temp;
    }

    if (lastNumber == 999) {
      lastNumber = db.getForeignKeyNumber(4);
    }
    if (lastNumber == 9999) {
      lastNumber = db.getForeignKeyNumber(5);
    }
    return ++lastNumber;
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
    return new SaveImpl(dmm, db);
  }

  @Override
  public String[] getSelectDatatype() {
    return config.getSelectDataType();
  }

  @Override
  public Optional<Table> getTable(final String tablename) {
    return getTable(tablename, true);
  }

  @Override
  public synchronized Optional<Table> getTable(final String tablename, final boolean useLocalDb) {
    Optional<Table> result;
    if (dmm.hasTable(tablename) && useLocalDb) {
      result = dmm.getTable(tablename);
    }
    else {
      result = db.getTable(tablename);
    }
    return result;
  }

  @Override
  public Validator getValidator() {
    return val;
  }

  @Override
  public int getWordLength() {
    return config.getWordLength();
  }

  @Override
  public ImportTask importFolderTask(final File f) {
    final String tbls = Util.readFile(new File(f.getPath() + File.separator + "Tbls.csv"));
    final String cols = Util.readFile(new File(f.getPath() + File.separator + "cols.csv"));
    final String pKeys = Util.readFile(new File(f.getPath() + File.separator + "PKeys.csv"));
    final String idxs = Util.readFile(new File(f.getPath() + File.separator + "Idxs.csv"));
    final String fKeys = Util.readFile(new File(f.getPath() + File.separator + "FKeys.csv"));
    final String metaTbls = Util.readFile(new File(f.getPath() + File.separator + "MetaTbls.csv"));
    final String metaCols = Util.readFile(new File(f.getPath() + File.separator + "MetaCols.csv"));
    exportImportCategoryFile(new File(f.getPath() + File.separator + Strings.CATEGORYFILE),
                             new File(config.getConfigPath() + File.separator + Strings.CATEGORYFILE));
    config.setPathImportCsv(f.getParent());
    config.save();
    return new ImportCsvTask(db, tbls, cols, idxs, pKeys, fKeys, metaTbls, metaCols);
  }

  @Override
  public ImportTask importTask(final File f, final char type) {
    if (f != null) {
      return importTask(Util.readFile(f), type, f.getParent(), db);
    }
    else {
      return null;
    }
  }

  ImportTask importTask(final String input, final char type, final String inputPath, final DatabaseConnection dc) {
    ImportTask it;
    if (type == Strings.IMPORTTYPESQL) {
      it = new ImportSqlTask(dc, input);
      config.setPathImportSql(inputPath);
    }
    else {
      // IMPORT CATEGORY
      it = new ImportCategoryTask(dc, input, true);
      config.setPathImportCat(inputPath);
    }
    return it;
  }

  @Override
  public Boolean isNeoServerStarterFileKnown() {
    return new File(getNeoServerStarterFile().getAbsolutePath() + Strings.PATH_NEO4J).exists();
  }

  @Override
  public boolean isOnline() {
    return db != null && db.isReady();
  }

  @Override
  public LoadFromDbTask loadFromDbTask() {
    return new LoadFromDbTask(db);
  }

  @Override
  public void saveCategoryList(final Map<String, String> categories) {
    ct.save(categories);
  }

  @Override
  public Boolean setNewNeoServerFolder(final File folder) {
    if (folder.isDirectory()) {
      config.setNeoDbStarterLocation(folder.getAbsolutePath());
      config.save();
    }
    return isNeoServerStarterFileKnown();
  }

  @Override
  public Boolean startNeoServer() {
    if (isNeoServerStarterFileKnown()) {
      final File file = getNeoServerStarterFile();
      final File batchFile = new File(file.getAbsolutePath() + Strings.PATH_NEO4J);
      if (batchFile.exists()) {
        final Runtime r = Runtime.getRuntime();
        try {
          final String str = Strings.EXECNEO4J_PRE + file.getAbsolutePath() + Strings.EXECNEO4J_POST;
          r.exec(str);
          config.setNeoDbStarterLocation(file.getAbsolutePath());
          return true;
        }
        catch (final IOException e) {
          ModelImpl.LOG.log(Level.SEVERE, e.toString(), e);
        }
      }
    }
    return false;
  }

  @Override
  public Boolean writeExportFile(final File f, final Generator gen) {
    if (f != null && db.isReady()) {
      Util.writeFile(f, gen, getAllTables());
      config.save();
      return true;
    }
    return false;
  }

  @Override
  public boolean writeToolchainReport(final File f) {
    if (f != null && db.isReady()) {
      final List<Table> allTbl = getAllTables();
      Util.writeFile(new File(f.getPath() + File.separator + "Tbls.csv"), new CsvForTblsGenerator(), allTbl);
      Util.writeFile(new File(f.getPath() + File.separator + "cols.csv"), new CsvForColsGenerator(), allTbl);
      Util.writeFile(new File(f.getPath() + File.separator + "PKeys.csv"), new CsvForPkeysGenerator(), allTbl);
      Util.writeFile(new File(f.getPath() + File.separator + "Idxs.csv"), new CsvForIdxsGenerator(), allTbl);
      Util.writeFile(new File(f.getPath() + File.separator + "FKeys.csv"), new CsvForFkeysGenerator(), allTbl);
      Util.writeFile(new File(f.getPath() + File.separator + "MetaTbls.csv"), new CsvForMetaGenerator('t'), allTbl);
      Util.writeFile(new File(f.getPath() + File.separator + "MetaCols.csv"), new CsvForMetaGenerator('f'), allTbl);
      exportImportCategoryFile(new File(config.getConfigPath() + File.separator + Strings.CATEGORYFILE),
                               new File(f.getPath() + File.separator + Strings.CATEGORYFILE));
      config.setPathExportCsv(f.getParent());
      config.save();
      return true;
    }
    return false;
  }
}
