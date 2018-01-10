package de.mach.tools.neodesigner.core.datamodel;

import de.mach.tools.neodesigner.core.Strings;

/**
 * Die Beziehungen aus der Graphendatenbank.
 *
 * @author Chris Deter
 *
 */
public class Relations {
  public enum Type {
    XPK, DATA, REFERENCE, FOREIGNKEY, INDEX, CONNECTED, DEFAULT
  }

  /**
   * Gibt den Typ der Beziehung zurück.
   *
   * @param t
   *          Typ
   * @return Textrepresentation des Types
   */
  public static String getType(final Type t) {
    // @formatter:off
    switch (t) {
      case XPK:
        return Strings.RELNAME_XPK;
      case DATA:
        return Strings.RELNAME_DATA;
      case REFERENCE:
        return Strings.RELNAME_REFERENCE;
      case FOREIGNKEY:
        return Strings.RELNAME_FOREIGNKEY;
      case INDEX:
        return Strings.RELNAME_INDEX;
      case CONNECTED:
        return Strings.RELNAME_CONNECTED;
      default:
        return "";
    }
    // @formatter:on
  }
}
