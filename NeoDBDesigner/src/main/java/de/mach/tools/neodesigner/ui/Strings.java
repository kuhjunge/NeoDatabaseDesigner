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

package de.mach.tools.neodesigner.ui;


/** Strings werden hier hin ausgelagert.
 *
 * @author Chris Deter */
public final class Strings {
  public static final String SWVERSION = "1.4.1";
  public static final String EMPTYSTRING = "";
  public static final String RELNAME_XPK = "XPK";
  public static final String EXPORTJSONDEFAULT = "export.json";

  static final String NAME_XIE = "XIE";

  public static final String SOFTWARENAME = "MACH-ER";

  public static final String NAME_NEWTABLE = "newTable";

  public static final String SOFTWAREINFO = "Neo Database Manager" + Strings.EOL
                                            + "Icon: http://www.iconarchive.com/show/blue-bits-icons-by-icojam/database-settings-icon.html"
                                            + Strings.EOL + Strings.EOL
                                            + "Copyright (c) 2018 Chris Deter (MIT-License)";

  public static final String EOL = "\n";

  public static final String VERSION = "This Software uses:" + Strings.EOL + "ANTLR 4.7.1" + Strings.EOL
                                       + "Controlsfx 8.40.14" + Strings.EOL + "Graphstream 1.3" + Strings.EOL
                                       + "Neo4J Java Driver 1.7" + " (Compatible with 3.1 - 3.5 neo4j servers)"
                                       + Strings.EOL + Strings.EOL + "Version:" + SWVERSION;
  private static final String FXML_PATH = "/de/mach/tools/neodesigner/ui/";

  public static final String FXML_ICON = Strings.FXML_PATH + "db.png";

  public static final String FXML_FKRELEDITOR = Strings.FXML_PATH + "fkRelEditor.fxml";

  public static final String FXML_NEOMODULE = Strings.FXML_PATH + "neocon.fxml";

  static final String FXML_NEDDBDESIGNER = Strings.FXML_PATH + "neoDBDesignerView.fxml";

  static final String FXML_TABLEVIEW = Strings.FXML_PATH + "tableTabView.fxml";

  public static final String FXML_INDEXRELEDITOR = Strings.FXML_PATH + "indexRelEditor.fxml";

  public static final String FXML_PDFEDITOR = Strings.FXML_PATH + "modelPrinterView.fxml";

  public static final String TITLE_FINDNEO4JFOLDER = "Open Neo4J Folder";

  public static final String TITLE_IMPORTKATEGORIE = "Open Kategory File";

  public static final String TITLE_IMPORTSQL = "Open SQL File";

  public static final String TITLE_EXPORTCSV = "Open CSV Files";

  public static final String TITLE_RELEDITOR = "Edit Relation";

  public static final String EXPORTSQLDEFAULT = "export.sql";

  public static final String EXPORTCQLDEFAULT = "export.cql";

  public static final String DBSTATUS_ONLINE = "Online";

  public static final String DBSTATUS_OFFLINE = "Offline";

  public static final String DBBUTTON_CONNECT = "Connect";

  public static final String DBBUTTON_DISCONNECT = "Disconnect";

  public static final String ALTITLE_IMPORTERR = "Import Error";

  public static final String ALTEXT_IMPORTERR = "Import failed!";

  public static final String ALTITLE_EXPORTERR = "Export Error";

  public static final String ALTEXT_EXPORTERR = "Export failed!";

  public static final String ALTITLE_RELEDITOR = "Index not editable";

  public static final String ALTEXT_RELEDITOR = "You cannot modify " + " Indizies other than XAK and XIE!";

  public static final String ALTITLE_FILEERR = "File Error";

  public static final String ALTEXT_FILEERR = "neo4J.bat File not Found!";

  public static final String ALTITLE_DATABASEERR = "Database Error";

  public static final String ALTEXT_DATABASEERR_CONNECT = "Connection failed!";

  public static final String ALTEXT_DATABASEERR_UNEX_INPUT = "There was " + "an unexpected Input from the Database";

  public static final String ALTEXT_DATABASEERR_UNEX_ERR = "There was an " + "unexpected error while importing";

  public static final String INDEXTOOLTIP = "Primary Key: „XPK“\n" + "Foreign Key: „R“\n"
                                            + "Index for Foreignkey: „XIF“\n" + "Unique Index: „XAK“\n"
                                            + "Simple Index: „XIE“\n";

  public static final String NAME_SECONDELEMENT = "_new";

  public static final String LABELNAME_TABLE = "Table: ";

  public static final String TABLEROW_NAME = "Name";

  public static final String TABLEROW_REFNAME = "Name Ref Field";

  public static final String NAME_TABLES = "Tables";

  static final String NAME_FKS = "Foreign Keys";

  public static final String ALTITLE_DELTABLE = "Delete Table";

  public static final String ALTEXT_DELTABLE = "This Action will " + "delete the following Table permanently: ";

  public static final String ALTITLE_SAVEERR = "Save Error";

  public static final String ALTEXT_SAVEERR = "Saving failed! This " + "Table is not valid or the Database is offline."
                                              + " Please solve following Error:";

  static final String NAME_INDEXES = "Indexes"; // Deutsch: indizes

  static final String NAME_PRIMKEY = "Primary Key";

  public static final String BUTTONNAME_SAVE = "Save ";

  public static final String BUTTONNAME_DELETE = "Delete ";

  public static final String BUTTONNAME_ADD = "Add ";

  public static final String NAME_FIELDS = "Fields";

  public static final String NAME_NEWFIELD = "newField";

  public static final String NAME_FIELD_ISNULL = "Required";

  public static final String NAME_FIELD_ISPRIM = "Primary";

  public static final String NAME_FIELD_DATATYPE = "Type";

  public static final String NAME_INDEX_ISUNIQUE = "unique";

  public static final String NAME_TABLE = "Table";

  public static final String NAMEEMPTYCATEGORY = "0,0";

  public static final String GRAPHSTREAMUICSSLABEL = "ui.label";

  public static final String GRAPHSTREAMUICSSCLASS = "ui.class";

  public static final String SEMICOLON = ";";

  static final String UNDERSCORE = "_";

  static final String STAR = "*";

  public static final String NAME_REFTABLE = "Ref Table";

  static final String ALTITLE_CLOSETABLE = "Close Table";

  static final String ALTEXT_CLOSETABLE = "Do you really want to close the table %s without saving?";

  public static final String ALTITLE_DATABASENOTEMPTY = "Database is not empty!";

  public static final String ALTEXT_DATABASEERR_NOTEMPTY = "The database already contains information. Importing "
                                                           + "another datamodel could mess up the database.";

  public static final String ALTEXT_CLEANDATABASE = "Do you want to delete the database before importing a new "
                                                    + "datamodel?";

  public static final String TITLE_EXPORTSQL = "Choose Export SQL File";

  public static final String TITLE_EXPORTCQL = "Choose Export CQL File";

  static final String NAME_R = "R";

  public static final Object CLASS_SMALL = "small";

  public static final Object CLASS_TABLE = "table";

  public static final Object CLASS_FIELD = "field";

  public static final Object CLASS_INDEX = "index";

  public static final Object CLASS_PRIMARY = "primary";

  public static final Object CLASS_DOBULEREL = "doubleRel";

  public static final String NOTIFICATION_IMPORT = "Finished Import!";

  public static final String NOTIFICATION_EXPORT = "Finished Export!";

  public static final String TABLEROW_COMMENT = "Comment";

  public static final String TABLEROW_ORDER = "Order";

  public static final String DEFAULT_CATEGORY = "0,0";

  public static final String NAME_CATEGORY = " Category";

  public static final String TITLE_DISPLAYCATGRAPH = "Display Cateogry Graph";

  public static final String ALTITLE_DISPLAYCATGRAPH = "This graph contains all categories that start with the search term. "
                                                       + "You can define more than one category with semicolon.";

  public static final String ALTEXT_DISPLAYCATGRAPH = "Please enter the search term:";

  public static final String DETAILDATABASEERR = "Could not establish connection to neo4j server!";

  public static final String LOG_WARNINGLOADDBMORETHANONCE = "loading failed - loading already in progress!";

  public static final char TYPE_SQL = 's';

  public static final char TYPE_SQL_PART = 'p';

  public static final char TYPE_CSV = 'c';

  public static final Character TYPE_JSON = 'j';

  public static final String TITLE_PDF_EXPORT = "Model Report PDF Printer";

  public static final String PATH_ERROR = "Path Error";

  public static final String PATH_ERROR_DETAIL = "Nicht alle notwendigen Pfade wurden gesetzt!";

  public static final String PATH_ERROR_NOT_VALID = "Dieser Pfad ist nicht gültig!";

  public static final String UPDATETEXT_CREATEPDF = "Erstelle PDF";

  public static final String UPDATETEXT_TEMPLATE = "verarbeite Template";

  public static final String UPDATETEXT_RUN1 = "Lauf 1 PDF Erstellung";

  public static final String UPDATETEXT_CREATEINDEX = "Erstelle Index";

  public static final String UPDATETEXT_RUN2 = "Lauf 2 PDF Erstellung";

  public static final String UPDATETEXT_RUN3 = "Lauf 3 PDF Erstellung";

  public static final String UPDATETEXT_COPYPDF = "PDF erfolgreich erstellt - Kopiere PDF zum Zielort";

  public static final String UPDATETEXT_CLEAN = "Räume Auf!";

  public static final String UPDATETEXT_FINISHED = "PDF erfolgreich erstellt!";

  public static final String UPDATETEXT_ERR = "Verarbeitungsfehler (%s)";

  public static final String ERROR = "Error";

  public static final String ERROR_CANT_REUSE_FIELD = "Cannot reuse a selected Field";

  public static final String ERROR_CANT_REUSE_FIELD_DETAIL = "Type of selected Field is not compatible with ForeignKey!";

  public static final String ALTITLE_OVERWRITEFIELD = "Really overwrite field?";

  public static final String ALTEXT_OVERWRITEFIELD = "Do you really want to use a field that is already there?";

  public static final String ALTEXT_OVERWRITEFIELDDETAIL = "Following Fields will be connected with the Foreignkey? \r\n";

  public static final String NAME_CATEGORIES = "all Categories";

  public static final String TABCATEGORY = ":Category";

  public static final String ALTITLEDBINFO = "Information about this Database";

  public static final String ALLNODES = "All Nodes";

  public static final String SPACERFORINFO = ":\t";

  public static final String PATH_NEO4J = "\\bin\\neo4j.bat";

  public static final String LOADFROMDB = "DB geladen in ms: ";

  private Strings() {}
}
