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

package de.mach.tools.neodesigner.database;

import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.List;
import java.util.Optional;

/**
 * SaveDatabase Interface für Speicheroperationen in der Datenbank.
 *
 * @author Chris Deter
 *
 */
public interface SaveDatabase {
  /**
   * gibt eine Tabelle aus der Datenbank zurück.
   *
   * @param tableName
   *          Name der Tabelle
   * @return Eine Tabelle
   */
  Optional<Table> getTable(String tableName);

  /**
   * Ändert einen Tabellennamen in der Datenbank.
   *
   * @param tableName
   *          der Tabellenname
   * @param newName
   *          der neue Tabellenname
   * @param xpkName
   *          der Name des Primärschlüssels
   */
  void changeTableName(String tableName, String newName, final String xpkName);

  /**
   * Ändert den Namen eines Elementes, welches einer Tabelle über Node Of
   * zugeordnet ist.
   *
   * @param nodeName
   *          das zu verändernde Node
   * @param tableName
   *          Name der Tabelle
   * @param newNodeName
   *          neue
   */
  void changeNodeNameFromTable(String nodeName, String tableName, String newNodeName);

  /**
   * Ändert die Kategorie einer Tabelle.
   *
   * @param tableName
   *          Name der Tabelle
   * @param newKategory
   *          Die neue Kategorie
   */
  void changeTableCategory(String tableName, String newKategory);

  /**
   * Ändert die Required Angabe eines Feldes.
   *
   * @param nodeName
   *          das zu verändernde Element
   * @param tableName
   *          Name der Tabelle
   * @param value
   *          der neue Required Wert
   */
  void changeFieldRequired(String nodeName, String tableName, Boolean value);

  /**
   * Ändert die TypeOfData Information eines Feldes.
   *
   * @param nodeName
   *          Das zu ändernde Element
   * @param tableName
   *          Name der Tabelle
   * @param type
   *          ner neue Feldtyp
   */
  void changeFieldDomain(String nodeName, String tableName, DomainId type, int length);

  /**
   * fügt oder entfernt ein Feld vom Primärschlüssel.
   *
   * @param nodeName
   *          Das zu ändernde Element
   * @param tableName
   *          Name der Tabelle
   * @param partOfPrimaryKey
   *          der neue Wert für die partOfPrimaryKey Eigenschaft
   */
  void changeFieldIsPartOfPrim(String nodeName, String tableName, Boolean partOfPrimaryKey);

  /**
   * ändert die Unique Eigenschaft von einem Index.
   *
   * @param nodeName
   *          Das zu ändernde Element
   * @param tableName
   *          Name der Tabelle
   * @param value
   *          der neue unique Wert des indexes
   */
  void changeIndexUnique(String nodeName, String tableName, Boolean value);

  /**
   * Fügt eine neue Tabelle ein.
   *
   * @param table
   *          die neu einzufügende Tabelle
   */
  void insertNewTable(Table table);

  /**
   * fügt ein neues Feld ein.
   *
   * @param field
   *          das neu einzufügende Feld
   */
  void insertNewField(Field field);

  /**
   * fügt einen neuen Index ein.
   *
   * @param index
   *          der neu einzufügende Index
   */
  void insertNewIndex(Index index);

  /**
   * fügt einen neuen ForeignKey ein.
   *
   * @param foreignKey
   *          der neu einzufügende Foreginkey
   */
  void insertNewForeignKey(ForeignKey foreignKey);

  /**
   * Entfernt ein Node.
   *
   * @param node
   *          das zu löschende Node
   */
  void deleteNode(Node node);

  /**
   * ändert die Felder von einem Index. Die alten Felderreferenzen werden entfernt
   * und die neuen aus der Liste werden erstellt und verknüpft.
   *
   * @param index
   *          der Index
   */
  void changeDataFields(Index index);

  /**
   * Ändert die Beziehung eines Fremdschlüssels.
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

  /**
   * Fragt eine Liste von Case Insensitive Namen aus der Datenbank ab.
   *
   * @param name
   *          der zu prüfende Name
   */
  List<String> getFieldNameCase(final String name);

  /**
   * Speichert ein Kommentar.
   *
   * @param name
   *          der Name des Elementes
   * @param tableName
   *          der Tabellenname
   * @param comment
   *          das Kommentar
   */
  void saveComment(String name, String tableName, String comment);

  /**
   * Speichert ein Kommentar.
   *
   * @param tableName
   *          der Tabellenname
   * @param comment
   *          das Kommentar
   */
  void saveComment(String tableName, String comment);

  /**
   * Ändert den Namen der Verknüpfung von allen Feldern die mit dem Feld des
   * Primärschlüssels verbunden sind.
   *
   * @param tableName
   *          der Tabellenname
   * @param newNodeName
   *          der Neue Feldname
   * @param oldNodeName
   *          der Alte Feldname
   */
  void changePrimFieldNameRelation(String tableName, String newNodeName, String oldNodeName);
}
