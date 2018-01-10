package de.mach.tools.neodesigner.ui;

/**
 * Strings werden hier hin ausgelagert.
 * 
 * @author Chris Deter
 *
 */
public final class Strings {
  private Strings() {
  }

  static final String RELNAME_XPK = "XPK";

  public static final String SOFTWARENAME = "Neo Database Designer";

  public static final String NAME_NEWTABLE = "newTable";

  public static final String SOFTWAREAUTHOR = "by Chris Deter";

  public static final String VERSION = "Version: 0.9.1";

  private static final String FXML_PATH = "/de/mach/tools/neoDBDesigner/ui/";

  static final String FXML_FKRELEDITOR = Strings.FXML_PATH + "fkRelEditor.fxml";

  static final String FXML_NEDDBDESIGNER = Strings.FXML_PATH + "neoDBDesignerView.fxml";

  static final String FXML_INDEXRELEDITOR = Strings.FXML_PATH + "indexRelEditor.fxml";

  public static final String TITLE_FINDNEO4JFOLDER = "Open Neo4J Folder";

  public static final String TITLE_IMPORTKATEGORIE = "Open Kategory File";

  public static final String TITLE_IMPORTSQL = "Open SQL File";

  public static final String TITLE_EXPORTCSV = "Open CSV File";

  static final String TITLE_RELEDITOR = "Edit Relation";

  public static final String EXPORTSQLDEFAULT = "export.sql";

  public static final String EXPORTCSVDEFAULT = "Print_DB_Report.csv";

  public static final String DBSTATUS_ONLINE = "Online";

  public static final String DBSTATUS_OFFLINE = "Offline";

  public static final String DBBUTTON_CONNECT = "Connect";

  public static final String DBBUTTON_DISCONNECT = "Disconnect";

  public static final String ALTITLE_IMPORTERR = "Import Error";

  public static final String ALTEXT_IMPORTERR = "Import failed!";

  public static final String ALTITLE_EXPORTERR = "Export Error";

  public static final String ALTEXT_EXPORTERR = "Export failed!";

  static final String ALTITLE_RELEDITOR = "Index not editable";

  static final String ALTEXT_RELEDITOR = "You cannot modify " + " Indizies other than XAK and XIE!";

  public static final String ALTITLE_FILEERR = "File Error";

  public static final String ALTEXT_FILEERR = "neo4J.bat File not Found!";

  public static final String ALTITLE_DATABASEERR = "Database Error";

  public static final String ALTEXT_DATABASEERR_CONNECT = "Could not connect";

  public static final String ALTEXT_DATABASEERR_UNEX_INPUT = "There was " + "an unexpected Input from the Database";

  public static final String ALTEXT_DATABASEERR_UNEX_ERR = "There was an " + "unexpected error while importing";

  public static final String INDEXTOOLTIP = "Primärschlüssel: „XPK“\n" + "Fremschlüssel: „R“\n"
      + "Indizes für Fremdschlüssel: „XIF“\n" + "Eindeutige Indizes: „XAK“\n" + "Einfache Indizes: „XIE“\n";

  public static final String NAME_SECONDELEMENT = "_new";

  public static final String LABELNAME_TABLE = "Table: ";

  static final String LABELNAME_NAME = "Name: ";

  public static final String TABLEROW_NAME = "Name";

  public static final String TABLEROW_REFNAME = "Name Ref Field";

  public static final String NAME_TABLES = "Tables";

  static final String NAME_FKS = "Foreign Keys";

  static final String NAME_FK = "Foreign Key";

  static final String ALTITLE_DELTABLE = "Delete Table";

  static final String ALTEXT_DELTABLE = "This Action will " + "delete the following Table permanently: ";

  static final String ALTITLE_SAVEERR = "Save Error";

  static final String ALTEXT_SAVEERR = "Saving failed! This " + "Table is not valid or the Database is offline."
      + " Please solve following Error:";

  static final String NAME_INDEXES = "Indexes"; // Deutsch: indizes

  static final String NAME_PRIMKEY = "Primary Key";

  static final String BUTTONNAME_SAVE = "Save ";

  static final String BUTTONNAME_DELETE = "Delete ";

  static final String BUTTONNAME_ADD = "Add ";

  static final String BUTTONNAME_MODIFY = "Modify ";

  static final String NAME_FIELD = "Field";

  static final String NAME_INDEX = "Index";

  static final String NAME_FIELDS = "Fields";

  static final String LABELNAME_KATEGORY = "Kategory: ";

  static final String NAME_NEWFIELD = "newField";

  static final String NAME_NEWFIELD_DATATYPE = "VARCHAR2(10)";

  static final String NAME_FIELD_ISNULL = "Required";

  static final String NAME_FIELD_ISPRIM = "Primaer";

  static final String NAME_FIELD_DATATYPE = "type";

  static final String NAME_INDEX_ISUNIQUE = "unique";

  static final String NAME_TABLE = "Table";
}
