package de.mach.tools.neodesigner.core;

import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Prüft ob ein Name oder eine Tabelle gewissen Regeln entspricht
 *
 * @author Chris Deter
 *
 */
public class Validator {

  private int maxTableNameLength = 18;
  private int maxColumnNameLength = 18;
  private final Save dm;
  private String lastError = "";

  /**
   * Konstruktor für den Validator.
   *
   * @param maxTable
   *          Maximale Größe für Tabellenamen
   * @param maxColumn
   *          Maximale Größe für Spaltennamen
   * @param dm
   *          Datenmodel
   */
  public Validator(final int maxTable, final int maxColumn, final Save dm) {
    maxTableNameLength = maxTable;
    maxColumnNameLength = maxColumn;
    this.dm = dm;
  }

  /**
   * Prüft ob ein reserved SQL Wort enthalten ist.
   *
   * @param inputStr
   * @return
   */
  private boolean stringContainsItemFromList(final String inputStr) {
    final String str = String.format(Strings.VALIDATOR_WORDWRAP, inputStr.toUpperCase());
    return Arrays.stream(Strings.RESERVED_SQL_WORDS).parallel().anyMatch(str::contains);
  }

  /**
   * Prüft ob ein Name in Ordnung ist
   *
   * @param name
   * @param length
   * @return
   */
  private boolean isNameOkay(final String name, final int length) {
    if (!stringContainsItemFromList(name) && name.length() <= length && name.matches(Strings.NAME_REGEX)) {
      return true;
    } else if (name.length() > length) {
      lastError = String.format(Strings.VALIDATOR_NAMETOLONG, name, length);
    } else if (!name.matches(Strings.NAME_REGEX)) {
      lastError = String.format(Strings.VALIDATOR_NAMENOTVALID, name, Strings.NAME_REGEX);
    } else {
      lastError = String.format(Strings.VALIDATOR_NAMECONTAINSSQL, name,
          Arrays.stream(Strings.RESERVED_SQL_WORDS).filter(p -> p.equals(name)).collect(Collectors.toList()));
    }
    return false;
  }

  /**
   * validiert einen Namen.
   *
   * @param name
   *          der Name
   * @return True wenn der Name okay ist, ansonsten False
   */
  public boolean validateName(final String name) {
    return isNameOkay(name, maxTableNameLength);
  }

  /**
   * prüft einen Namen auf Korrektheit.
   *
   * @param t
   *          Die zu analyiserende Tabelle
   * @return True wenn die Tabelle korrekt ist, ansonsten false
   */
  public boolean validateTable(final Table t, final boolean isNew) {
    boolean check = true;
    if (t.getData().isEmpty()) {
      lastError = Strings.VALIDATOR_TABLEHASNOFIELDS;
      check = false;
    }
    final List<Node> ln = new ArrayList<>();
    ln.addAll(t.getData());
    ln.addAll(t.getIndizies());
    ln.add(t.getXpk());
    ln.addAll(t.getForeignKeys());
    for (final Node i : ln) {
      if (!isNameOkay(i.getName().trim(), maxColumnNameLength)) {
        check = false;
      }
    }
    for (final ForeignKey i : t.getForeignKeys()) {
      if (i.getRefTable() == null) {
        lastError = String.format(Strings.VALIDATOR_REFTABLEISNULL, i.getName());
        check = false;
      }
      if (i.getFieldList().isEmpty()) {
        lastError = String.format(Strings.VALIDATOR_INDEXHASNOFIELDS, i.getName());
        check = false;
      }
    }
    if (dm.hasTable(t.getName()) && isNew) {
      lastError = String.format(Strings.VALIDATOR_TABLEALREADYEXISTS);
      check = false;
    }
    return check;
  }

  /**
   * Gibt den Fehler warum die Validierung gescheitert ist zurück.
   * 
   * @return
   */
  public String getLastError() {
    return lastError;
  }
}
