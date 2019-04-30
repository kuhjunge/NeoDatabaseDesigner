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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.Table;


/** Prüft ob ein Name oder eine Tabelle gewissen Regeln entspricht.
 *
 * @author Chris Deter */
public class Validator {

  private int maxTableNameLength;
  private int maxColumnNameLength;
  private int uniqueUntilLength;
  private final Save dm;
  private String lastError = "";

  /** Konstruktor für den Validator.
   *
   * @param maxTable Maximale Größe für Tabellenamen
   * @param maxColumn Maximale Größe für Spaltennamen
   * @param dm Datenmodel */
  Validator(final int maxTable, final int maxColumn, final int uniqueUntlLength, final Save dm) {
    maxTableNameLength = maxTable;
    maxColumnNameLength = maxColumn;
    uniqueUntilLength = uniqueUntlLength;
    this.dm = dm;
  }

  /** Prüft ob ein reserved SQL Wort enthalten ist.
   *
   * @param inputStr der Input
   * @return true wenn der String ein Element enthält */
  private boolean stringContainsItemFromList(final String inputStr) {
    final String str = String.format(Strings.VALIDATOR_WORDWRAP, inputStr.toUpperCase());
    return Arrays.stream(Strings.RESERVED_SQL_WORDS).parallel().anyMatch(str::contains);
  }

  /** Prüft ob ein Name in Ordnung ist.
   *
   * @param name der Name
   * @param length die maximale Länge
   * @return true wenn der Name in Ordnung ist */
  private boolean isNameOkay(final String name, final int length) {
    if (!stringContainsItemFromList(name) && name.length() <= length && name.matches(Strings.NAME_REGEX)) {
      return true;
    }
    else if (name.length() > length) {
      lastError = String.format(Strings.VALIDATOR_NAMETOLONG, name, length);
    }
    else if (!name.matches(Strings.NAME_REGEX)) {
      lastError = String.format(Strings.VALIDATOR_NAMENOTVALID, name, Strings.NAME_REGEX);
    }
    else {
      lastError = String
          .format(Strings.VALIDATOR_NAMECONTAINSSQL, name,
                  Arrays.stream(Strings.RESERVED_SQL_WORDS).filter(p -> p.equals(name)).collect(Collectors.toList()));
    }
    return false;
  }

  /** validiert einen Namen.
   *
   * @param name der Name
   * @return True wenn der Name okay ist, ansonsten False */
  boolean validateName(final String name) {
    return isNameOkay(name, maxTableNameLength);
  }

  /** validiert einen Namen.
   *
   * @param name der Name
   * @return True wenn der Name okay ist, ansonsten False */
  public boolean isFieldNameInvalid(final String name) {
    String lname = name.trim();
    boolean ret = isNameOkay(lname, maxColumnNameLength);
    if (ret) {
      final List<String> altName = dm.getFieldNameWithoutCase(lname);
      if (!altName.isEmpty() && !altName.contains(lname)) {
        ret = false;
        lastError = String.format(Strings.VALIDATOR_NAMEWIHTANOTHERCASE, lname, altName);
      }
    }
    return !ret;
  }

  /** prüft einen Namen auf Korrektheit.
   *
   * @param t Die zu analyiserende Tabelle
   * @return True wenn die Tabelle korrekt ist, ansonsten false */
  public boolean validateTable(final Table t, final boolean isNew) {
    boolean check = true;
    if (t.getFields().isEmpty()) {
      lastError = Strings.VALIDATOR_TABLEHASNOFIELDS;
      check = false;
    }
    check = checkNodeNames(t) && check;
    check = checkFields(t, check);
    for (final ForeignKey i : t.getForeignKeys()) {
      if (i.getRefTable() == null) {
        lastError = String.format(Strings.VALIDATOR_REFTABLEISNULL, i.getName());
        check = false;
      }
      if (i.getIndex().getFieldList().isEmpty()) {
        lastError = String.format(Strings.VALIDATOR_INDEXHASNOFIELDS, i.getName());
        check = false;
      }
    }
    if (dm.hasTable(t.getName()) && isNew) {
      lastError = Strings.VALIDATOR_TABLEALREADYEXISTS;
      check = false;
    }
    final Optional<Table> before = dm.getTable(t.getName());
    // Prüft ob ein Feld mit dem selben Namen bereits vorhanden ist
    if (before.isPresent()) {
      check = checkPrimFieldValid(t, check, before.get());
    }
    return check;
  }

  private boolean checkPrimFieldValid(final Table t, boolean check, final Table before) {
    for (final Field f : t.getFields()) {
      if (f.isPartOfPrimaryKey() && !(before.getXpk().getFieldList().contains(f))) {
        for (final ForeignKey fk : t.getRefForeignKeys()) {
          if (fk.getTable().getFields().contains(f)) {
            lastError = Strings.VALIDATOR_PRIMAERFIELDNOTVALID;
            check = false;
          }
        }
        // Prüft ob ein Feld entfernt wird, was bereits Teil eines Pks ist
        if (!f.isPartOfPrimaryKey() && before.getXpk().getFieldList().contains(f)) {
          for (final ForeignKey fk : t.getRefForeignKeys()) {
            for (final Field fx : fk.getIndex().getFieldList()) {
              if (fx.isPartOfPrimaryKey()) {
                lastError = Strings.VALIDATOR_PRIMAERFIELDNOTVALID;
                check = false;
              }
            }
          }
        }
      }
    }
    return check;
  }

  private boolean checkNodeNames(final Table t) {
    final List<Node> ln = new ArrayList<>(t.getIndizies());
    ln.addAll(t.getForeignKeys());
    ln.add(t.getXpk());
    for (final Node i : ln) {
      if (!isNameOkay(i.getName().trim(), maxColumnNameLength)) {
        return false;
      }
    }
    return true;
  }

  private boolean checkFields(final Table t, final boolean c) {
    boolean check = c;
    for (final Field f : t.getFields()) {
      if (isFieldNameInvalid(f.getName().trim())) {
        check = false;
      }
      int i = 0;
      for (final Field cmpF : t.getFields()) {
        if (cmpF.getName().equalsIgnoreCase(f.getName())) {
          i++;
        }
      }
      if (i > 1) {
        lastError = Strings.VALIDATOR_DUPLICATEFIELDS;
        check = false;
      }
    }
    return check;
  }

  /** Gibt die Information zurück ob eine Tabelle im Datenmodel vorhanden ist.
   *
   * @param name der Tabelle
   * @param length die Länge des Tabellennamens
   * @return True wenn die Tabelle vorhanden ist */
  private boolean hasTable(final String name, final int length) {
    if (name != null && name.length() > 1) {
      final String shortCompareName = Util.getShortName(name, length);
      return dm.getTables().stream().anyMatch(t -> Util.getShortName(t.getName(), length).equals(shortCompareName));
    }
    return false;
  }

  /** Gibt den Fehler warum die Validierung gescheitert ist zurück.
   *
   * @return den letzten Fehler */
  public String getLastError() {
    return lastError;
  }

  /** Prüft ob der Name der Tabelle bereits existiert oder nicht eindeutig genug. ist
   *
   * @param newName Name der Tabelle
   * @return True wenn der Name einwandfrei ist */
  public boolean validateTableName(final String newName, final String oldName) {
    boolean b = validateName(newName);
    if (b && !(!hasTable(newName, uniqueUntilLength) || newName.compareTo(oldName) == 0)) {
      lastError = Strings.VALIDATOR_TABLEISSIMILAR;
      b = false;
    }
    return b;
  }

  public int getTableNameLength() {
    return maxTableNameLength;
  }

  public int getNodeNameLength() {
    return maxColumnNameLength;
  }
}
