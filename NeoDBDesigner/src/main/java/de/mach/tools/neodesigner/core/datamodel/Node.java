package de.mach.tools.neodesigner.core.datamodel;

/**
 * Representiert einen Knoten aus der Graphendatenbank von welchen die anderen
 * Relationalen Datenbankmodel Abbildungen von abgeleitet sind.
 *
 * @author Chris Deter
 *
 */
public interface Node {
  /**
   * gibt den Namen zur�ck.
   *
   * @return String mit Namen
   */
  public String getName();

  /**
   * setzt den Namen.
   *
   * @param name
   *          Der Name des Nodes
   */
  public void setName(String name);

  /**
   * gibt das �bergeordnete Node zur�ck (Meistens eine Tabelle).
   *
   * @return �bergeordnetes Element oder Null
   */
  public Node getNodeOf();
}
