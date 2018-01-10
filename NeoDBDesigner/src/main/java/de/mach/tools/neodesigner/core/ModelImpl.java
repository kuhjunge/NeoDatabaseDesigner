package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.nexport.CsvGenerator;
import de.mach.tools.neodesigner.core.nexport.Generator;
import de.mach.tools.neodesigner.core.nexport.SqlGenerator;
import de.mach.tools.neodesigner.core.nimport.ImportCategoryTask;
import de.mach.tools.neodesigner.core.nimport.ImportSqlTask;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.DatabaseConnection;
import de.mach.tools.neodesigner.database.DatabaseConnector;
import de.mach.tools.neodesigner.database.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.database.DatabaseManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementation des Model Interfaces
 *
 * @author Chris Deter
 *
 */
public class ModelImpl implements Model {
  private final DatabaseConnection db;
  private final DataModelManager dmm;
  private final Configuration config;
  private int lastNumber = 0;
  private static final Logger LOG = Logger.getLogger(ModelImpl.class.getName());

  /**
   * Konstruktor Model.
   */
  public ModelImpl(final DatabaseConnector dc) {
    db = new DatabaseManager(dc);
    dmm = new DataModelManager();
    config = new Configuration();
    config.init();
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
  public void disconnectDb() {
    db.disconnectDb();
    dmm.clear();
  }

  @Override
  public boolean connectDb(final String dbadr, final String usr, final String pw) {
    if (db.connectDb(dbadr, usr, pw)) {
      config.save(dbadr, usr, pw);
      return true;
    }
    return false;
  }

  @Override
  public LoadFromDbTask loadFrmDbTask() {
    dmm.clear();
    return new LoadFromDbTask(this);
  }

  @Override
  public Table getnewTable(final String tableName) {
    return new TableImpl(tableName);
  }

  @Override
  public Table getTable(final String name) {
    if (dmm.hasTable(name)) {
      return dmm.getTable(name);
    } else {
      return db.getTable(name);
    }
  }

  @Override
  public List<Table> getAllLocalTables() {
    return dmm.getTables();
  }

  @Override
  public List<Table> getAllTables() {
    if (dmm.isEmpty() && db.isOnline()) {
      dmm.addAll(db.getAllTables());
    }
    return dmm.getTables();
  }

  @Override
  public boolean isOnline() {
    return db.isOnline();
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
   * @return
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
  public Boolean writeExportFile(final File f, final char type) {
    if (f != null && db.isOnline()) {
      Generator gen;
      try (BufferedWriter writer = Files.newBufferedWriter(f.toPath())) {
        if (type == 's') {
          gen = new SqlGenerator();
        } else {
          gen = new CsvGenerator();
        }
        writer.write(gen.generate(getAllTables()));
        return true;
      } catch (final IOException e) {
        ModelImpl.LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
    return false;
  }

  @Override
  public ImportTask importTask(final File file, final char type) {
    List<String> lines = new ArrayList<>();
    if (file != null) {
      try {
        lines = Files.readAllLines(file.toPath());
      } catch (final IOException e) {
        ModelImpl.LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
    return importTask(lines, type,
        new DatabaseManager(new DatabaseConnectorImpl(), config.getAddrOfDb(), config.getUser(), config.getPw()));
  }

  /**
   * Gibt einen Import Task zurück
   *
   * @param lines
   * @param type
   *          's' für SQL Import, ansonsten CSV Import
   * @param dc
   * @return
   */
  ImportTask importTask(final List<String> lines, final char type, final DatabaseConnection dc) {
    ImportTask it = null;
    final String listString = lines.stream().map(Object::toString).collect(Collectors.joining("\r\n"));
    if (type == 's') {
      it = new ImportSqlTask(dc, listString);
    } else {
      it = new ImportCategoryTask(dc, listString);
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

}
