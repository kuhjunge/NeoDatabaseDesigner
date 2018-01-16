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

package de.mach.tools.neodesigner.core;

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
import de.mach.tools.neodesigner.core.nexport.CypherGenerator;
import de.mach.tools.neodesigner.core.nexport.Generator;
import de.mach.tools.neodesigner.core.nexport.SqlGenerator;
import de.mach.tools.neodesigner.core.nexport.pdf.LoadPdfConfiguration;
import de.mach.tools.neodesigner.core.nimport.ImportCategoryTask;
import de.mach.tools.neodesigner.core.nimport.ImportCsvTask;
import de.mach.tools.neodesigner.core.nimport.ImportSqlTask;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnector;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.database.cypher.DatabaseManager;
import de.mach.tools.neodesigner.database.local.DataModelManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementation des Model Interfaces.
 *
 * @author Chris Deter
 *
 */
public class ModelImpl implements Model {
  private final DatabaseConnection db;
  private final DataModelManager dmm;
  private final Configuration config;
  private final CategoryTranslator ct;
  private Validator val;
  private int lastNumber = 0;
  private static final Logger LOG = Logger.getLogger(ModelImpl.class.getName());

  /**
   * Konstruktor Model.
   */
  public ModelImpl(final DatabaseConnector dc, final CategoryTranslator ctrans) {
    dmm = new DataModelManager();
    db = new DatabaseManager(dc);
    config = new Configuration();
    config.init();
    ct = ctrans;
  }

  private DatabaseConnection getNewDatabaseConnection(final DatabaseConnector dbcon) {
    return new DatabaseManager(dbcon, config.getAddrOfDb(), config.getUser(), config.getPw());
  }

  private DatabaseConnection getNewDatabaseConnection() {
    return getNewDatabaseConnection(new DatabaseConnectorImpl());
  }

  @Override
  public Observable dataModelObservable() {
    return dmm;
  }

  @Override
  public Save getSaveObj() {
    return new SaveImpl(dmm, db);
  }

  @Override
  public List<String> getListWithTableNames() {
    return db.getListWithTableNames();
  }

  @Override
  public List<String> getAllCategories() {
    return db.getListWithCategories();
  }

  @Override
  public void disconnectDb() {
    db.disconnectDb();
    dmm.clear();
  }

  @Override
  public boolean connectDb(final String dbadr, final String usr, final String pw) {
    if (db.connectDb(dbadr, usr, pw)) {
      config.save(dbadr, usr, pw);
      ct.load(getAllCategories());
      val = new Validator(config.getWordLength(), config.getWordLength(), config.getUniqueTableLength(), getSaveObj());
      return true;
    }
    return false;
  }

  @Override
  public void addTableList(final List<Table> lt) {
    dmm.clear();
    dmm.addAll(lt);
  }

  @Override
  public LoadFromDbTask loadFromDbTask(final DatabaseConnector dbcon) {
    return new LoadFromDbTask(getNewDatabaseConnection(dbcon));
  }

  @Override
  public Table getnewTable(final String tableName) {
    return new TableImpl(tableName);
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
    } else {
      result = db.getTable(tablename);
    }
    return result;
  }

  @Override
  public List<Table> getAllTables() {
    if (dmm.isEmpty() && db.isReady()) {
      dmm.addAll(db.getTables());
    }
    return dmm.getTables();
  }

  @Override
  public synchronized boolean isOnline() {
    return db.isReady();
  }

  @Override
  public int getNextFkNumber() {
    if (lastNumber == 0) {
      lastNumber = db.getForeignKeyNumber(3) + 1;
    }
    if (lastNumber == 999) {
      lastNumber = db.getForeignKeyNumber(4) + 1;
    }
    if (lastNumber == 9999) {
      lastNumber = db.getForeignKeyNumber(5) + 1;
    }
    return lastNumber++;
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
  public Boolean isNeoServerStarterFileKnown() {
    return new File(getNeoServerStarterFile().getAbsolutePath() + Strings.PATH_NEO4J).exists();
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
        } catch (final IOException e) {
          ModelImpl.LOG.log(Level.SEVERE, e.toString(), e);
        }
      }
    }
    return false;
  }

  /**
   * gibt ein File Objekt der Neo4J Start Batch zurück.
   *
   * @return die Start Datei
   */
  private File getNeoServerStarterFile() {
    return new File(config.getNeoDbStarterLocation());
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
  public int getWordLength() {
    return config.getWordLength();
  }

  @Override
  public String getAddrOfDb() {
    return config.getAddrOfDb();
  }

  @Override
  public String getUser() {
    return config.getUser();
  }

  @Override
  public String getPw() {
    return config.getPw();
  }

  @Override
  public String[] getSelectDatatype() {
    return config.getSelectDataType();
  }

  @Override
  public String getPathImportSql() {
    return config.getPathImportSql();
  }

  @Override
  public String getPathImportCat() {
    return config.getPathImportCat();
  }

  @Override
  public String getPathImportCsv() {
    return config.getPathImportCsv();
  }

  @Override
  public String getPathExportSql() {
    return config.getPathExportSql();
  }

  @Override
  public String getPathExportCsv() {
    return config.getPathExportCsv();
  }

  @Override
  public String getPathExportCql() {
    return config.getPathExportCql();
  }

  @Override
  public Boolean writeExportFile(final File f, final char type) {
    if (f != null && db.isReady()) {
      Generator gen;
      if (type == Strings.IMPORTTYPESQL) {
        gen = new SqlGenerator();
        config.setPathExportSql(f.getParent());
      } else {
        gen = new CypherGenerator();
        config.setPathExportCql(f.getParent());
      }
      writeFile(f, gen);
      config.save();
      return true;
    }
    return false;
  }

  private void writeFile(final File f, final Generator gen) {
    try (BufferedWriter writer = Files.newBufferedWriter(f.toPath())) {
      writer.write(gen.generate(getAllTables()));
    } catch (final IOException e) {
      ModelImpl.LOG.log(Level.SEVERE, e.toString(), e);
    }
  }

  @Override
  public boolean writeToolchainReport(final File f) {
    if (f != null && db.isReady()) {
      writeFile(new File(f.getPath() + File.separator + "Tbls.csv"), new CsvForTblsGenerator());
      writeFile(new File(f.getPath() + File.separator + "cols.csv"), new CsvForColsGenerator());
      writeFile(new File(f.getPath() + File.separator + "PKeys.csv"), new CsvForPkeysGenerator());
      writeFile(new File(f.getPath() + File.separator + "Idxs.csv"), new CsvForIdxsGenerator());
      writeFile(new File(f.getPath() + File.separator + "FKeys.csv"), new CsvForFkeysGenerator());
      writeFile(new File(f.getPath() + File.separator + "Meta.csv"), new CsvForMetaGenerator());
      config.setPathExportCsv(f.getParent());
      config.save();
      return true;
    }
    return false;
  }

  @Override
  public ImportTask importFolderTask(final File f) {
    final DatabaseConnection dc = getNewDatabaseConnection();
    final String tbls = readFile(new File(f.getPath() + File.separator + "Tbls.csv"));
    final String cols = readFile(new File(f.getPath() + File.separator + "cols.csv"));
    final String pKeys = readFile(new File(f.getPath() + File.separator + "PKeys.csv"));
    final String idxs = readFile(new File(f.getPath() + File.separator + "Idxs.csv"));
    final String fKeys = readFile(new File(f.getPath() + File.separator + "FKeys.csv"));
    final String meta = readFile(new File(f.getPath() + File.separator + "Meta.csv"));
    config.setPathImportCsv(f.getParent());
    config.save();
    return new ImportCsvTask(dc, tbls, cols, idxs, pKeys, fKeys, meta);
  }

  /**
   * Liest eine Datei ein.
   *
   * @param file
   *          Name der Datei
   * @return der Inhalt der Datei
   */
  private String readFile(final File file) {
    List<String> lines = new ArrayList<>();
    if (file != null && file.exists()) {
      try {
        lines = Files.readAllLines(file.toPath());
      } catch (final IOException e) {
        ModelImpl.LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
    return lines.stream().map(Object::toString).collect(Collectors.joining(Strings.EOL));
  }

  @Override
  public ImportTask importTask(final File f, final char type) {
    if (f != null) {
      return importTask(readFile(f), type, f.getParent(), getNewDatabaseConnection());
    } else {
      return null;
    }
  }

  ImportTask importTask(final String input, final char type, final String inputPath, final DatabaseConnection dc) {
    ImportTask it = null;
    if (type == Strings.IMPORTTYPESQL) {
      it = new ImportSqlTask(dc, input);
      config.setPathImportSql(inputPath);
    } else {
      it = new ImportCategoryTask(dc, input);
      config.setPathImportCat(inputPath);
    }
    return it;
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
  public Validator getValidator() {
    return val;
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
  public void saveCategoryList(final Map<String, String> categories) {
    ct.save(categories);
  }

  @Override
  public LoadPdfConfiguration getPdfConfig() {
    return config;
  }

  @Override
  public Map<String, Integer> getDatabaseStats() {
    return db.getDatabaseStats();
  }
}
