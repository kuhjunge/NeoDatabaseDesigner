package de.mach.tools.neodesigner.database;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;

/**
 * SaveDatabase Interface f�r Speicheroperationen in der Datenbank
 * 
 * @author Chris Deter
 *
 */
public interface SaveDatabase {
  /**
   * gibt eine Tabelle aus der Datenbank zur�ck.
   *
   * @param tableName
   *          Name der Tabelle
   * @return Eine Tabelle
   */
  Table getTable(String tableName);

  /**
   * �ndert einen Tabellennamen in der Datenbank.
   *
   * @param tableName
   *          der Tabellenname
   * @param newName
   *          der neue Tabellenname
   * @param xpkName
   *          der Name des Prim�rschl�ssels
   */
  void changeTableName(String tableName, String newName, final String xpkName);

  /**
   * �ndert den Namen eines Elementes, welches einer Tabelle �ber Node Of
   * zugeordnet ist.
   *
   * @param nodeName
   *          das zu ver�ndernde Node
   * @param tableName
   *          Name der Tabelle
   * @param newNodeName
   *          neue
   */
  void changeNodeNameFromTable(String nodeName, String tableName, String newNodeName);

  /**
   * �ndert die Kategorie einer Tabelle.
   *
   * @param tableName
   *          Name der Tabelle
   * @param newKategory
   *          Die neue Kategorie
   */
  void changeTableCategory(String tableName, String newKategory);

  /**
   * �ndert die Required Angabe eines Feldes.
   *
   * @param nodeName
   *          das zu ver�ndernde Element
   * @param tableName
   *          Name der Tabelle
   * @param value
   *          der neue Required Wert
   */
  void changeFieldRequired(String nodeName, String tableName, Boolean value);

  /**
   * �ndert die TypeOfData Information eines Feldes.
   *
   * @param nodeName
   *          Das zu �ndernde Element
   * @param tableName
   *          Name der Tabelle
   * @param type
   *          ner neue Feldtyp
   */
  void changeFieldTypeOfData(String nodeName, String tableName, String type);

  /**
   * f�gt oder entfernt ein Feld vom Prim�rschl�ssel.
   *
   * @param nodeName
   *          Das zu �ndernde Element
   * @param tableName
   *          Name der Tabelle
   * @param partOfPrimaryKey
   *          der neue Wert f�r die partOfPrimaryKey Eigenschaft
   */
  void changeFieldIsPartOfPrim(String nodeName, String tableName, Boolean partOfPrimaryKey);

  /**
   * �ndert die Unique Eigenschaft von einem Index.
   *
   * @param nodeName
   *          Das zu �ndernde Element
   * @param tableName
   *          Name der Tabelle
   * @param value
   *          der neue unique Wert des indexes
   */
  void changeIndexUnique(String nodeName, String tableName, Boolean value);

  /**
   * F�gt eine neue Tabelle ein.
   *
   * @param table
   *          die neu einzuf�gende Tabelle
   */
  void insertNewTable(Table table);

  /**
   * f�gt ein neues Feld ein.
   *
   * @param field
   *          das neu einzuf�gende Feld
   */
  void insertNewField(Field field);

  /**
   * f�gt einen neuen Index ein.
   *
   * @param index
   *          der neu einzuf�gende Index
   */
  void insertNewIndex(Index index);

  /**
   * f�gt einen neuen ForeignKey ein.
   *
   * @param foreignKey
   *          der neu einzuf�gende Foreginkey
   */
  void insertNewForeignKey(ForeignKey foreignKey);

  /**
   * Entfernt ein Node.
   *
   * @param node
   *          das zu l�schende Node
   */
  void deleteNode(Node node);

  /**
   * �ndert die Felder von einem Index. Die alten Felderreferenzen werden
   * entfernt und die neuen aus der Liste werden erstellt und verkn�pft.
   *
   * @param index
   *          der Index
   */
  void changeDataFields(Index index);

  /**
   * �ndert die Beziehung eines Fremdschl�ssels.
   *
   * @param foreignKeyName
   *          der FK name
   * @param tableName
   *          Name der Tabelle
   * @param refTableName
   *          die referenzierte Tabelle
   * @param indexName
   *          der FK Indexname
   */
  void changeFkRelations(String foreignKeyName, String tableName, String refTableName, String indexName);
}
