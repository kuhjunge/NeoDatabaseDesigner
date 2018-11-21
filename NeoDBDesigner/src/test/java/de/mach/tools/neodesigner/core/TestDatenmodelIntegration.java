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


import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.category.CategoryTranslator;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.nimport.ImportCsvTask;
import de.mach.tools.neodesigner.core.nimport.ImportTask;
import de.mach.tools.neodesigner.database.cypher.DatabaseConnectorImpl;
import de.mach.tools.neodesigner.database.local.LocalDatabaseManager;


public class TestDatenmodelIntegration {
  String tbls = "ABTEILUNG\r\n" + "DIENSTWAGEN\r\n" + "FAHRZEUG\r\n" + "MITARBEITER\r\n" + "SCHADENSFALL\r\n"
                + "VERSNEHMER\r\n" + "VERSVERTRAG";
  String cols = "ABTEILUNG;BEZEICHNUNG;VARCHAR2;1;0;N\r\n" + "ABTEILUNG;NUMMER;NUMBER;0;0;N\r\n"
                + "DIENSTWAGEN;KENNZEICHEN;VARCHAR2;10;0;N\r\n" + "DIENSTWAGEN;PERSONALNR;NUMBER;0;0;Y\r\n"
                + "FAHRZEUG;FAHRZEUGTYP;VARCHAR2;30;0;N\r\n" + "FAHRZEUG;FARBE;VARCHAR2;10;0;Y\r\n"
                + "FAHRZEUG;KENNZEICHEN;VARCHAR2;10;0;N\r\n" + "MITARBEITER;ABTEILUNG;VARCHAR2;10;0;N\r\n"
                + "MITARBEITER;APTBEZEICHNUNG;VARCHAR2;1;0;N\r\n" + "MITARBEITER;APTNUMMER;NUMBER;0;0;N\r\n"
                + "MITARBEITER;EMAIL;VARCHAR2;30;0;Y\r\n" + "MITARBEITER;GEBURTSDATUM;DATE;0;0;N\r\n"
                + "MITARBEITER;NAME;VARCHAR2;30;0;N\r\n" + "MITARBEITER;PERSONALNR;NUMBER;0;0;N\r\n"
                + "MITARBEITER;RAUM;VARCHAR2;10;0;Y\r\n" + "MITARBEITER;TELEFON;VARCHAR2;10;0;Y\r\n"
                + "MITARBEITER;VORNAME;VARCHAR2;30;0;N\r\n" + "SCHADENSFALL;DATUM;DATE;0;0;N\r\n"
                + "SCHADENSFALL;ORT;VARCHAR2;20;0;N\r\n" + "SCHADENSFALL;SACHBEARBEITER;NUMBER;0;0;N\r\n"
                + "SCHADENSFALL;SCHADENSHOEHE;NUMBER;0;18;Y\r\n" + "SCHADENSFALL;UMSTAENDE;VARCHAR2;40;0;N\r\n"
                + "SCHADENSFALL;VERLETZTE;VARCHAR2;1;0;Y\r\n" + "SCHADENSFALL;VORGANGSNUMMER;NUMBER;0;0;N\r\n"
                + "VERSNEHMER;FUEHRERSCHEINSEIT;DATE;0;0;Y\r\n" + "VERSNEHMER;GEBURTSTAG;DATE;0;0;Y\r\n"
                + "VERSNEHMER;HAUSNUMMER;VARCHAR2;30;0;N\r\n" + "VERSNEHMER;KUNDENNR;NUMBER;0;0;N\r\n"
                + "VERSNEHMER;NAME;VARCHAR2;30;0;N\r\n" + "VERSNEHMER;ORT;VARCHAR2;30;0;N\r\n"
                + "VERSNEHMER;PLZ;VARCHAR2;30;0;N\r\n" + "VERSNEHMER;STRASSE;VARCHAR2;30;0;N\r\n"
                + "VERSNEHMER;VORNAME;VARCHAR2;30;0;Y\r\n" + "VERSVERTRAG;KENNZEICHEN;VARCHAR2;10;0;N\r\n"
                + "VERSVERTRAG;KUNDENNR;NUMBER;0;0;N\r\n" + "VERSVERTRAG;PERSONALNR;NUMBER;0;0;N\r\n"
                + "VERSVERTRAG;VERTRAGSABSCHLUSS;DATE;0;0;Y\r\n" + "VERSVERTRAG;VERTRAGSART;VARCHAR2;10;0;N\r\n"
                + "VERSVERTRAG;VERTRAGSNR;NUMBER;0;0;N\r\n" + "";
  String idxs = "DIENSTWAGEN;XIF101MITARBEITER;PERSONALNR;1;N\r\n" + "MITARBEITER;XIF102ABTEILUNG;APTNUMMER;1;N\r\n"
                + "MITARBEITER;XIF102ABTEILUNG;APTBEZEICHNUNG;2;N\r\n"
                + "SCHADENSFALL;XIF106MITARBEITER;SACHBEARBEITER;1;N\r\n"
                + "VERSVERTRAG;XIF103VERSNEHMER;KUNDENNR;1;N\r\n" + "VERSVERTRAG;XIF104FAHRZEUG;KENNZEICHEN;1;N\r\n"
                + "VERSVERTRAG;XIF105MITARBEITER;PERSONALNR;1;N\r\n" + "";
  String pKeys = "ABTEILUNG;XPKABTEILUNG;NUMMER;1\r\n" + "ABTEILUNG;XPKABTEILUNG;BEZEICHNUNG;2\r\n"
                 + "DIENSTWAGEN;XPKDIENSTWAGEN;KENNZEICHEN;1\r\n" + "FAHRZEUG;XPKFAHRZEUG;KENNZEICHEN;1\r\n"
                 + "MITARBEITER;XPKMITARBEITER;PERSONALNR;1\r\n" + "SCHADENSFALL;XPKSCHADENSFALL;VORGANGSNUMMER;1\r\n"
                 + "VERSNEHMER;XPKVERSNEHMER;KUNDENNR;1\r\n" + "VERSVERTRAG;XPKVERSVERTRAG;VERTRAGSNR;1\r\n" + "";
  String fKeys = "DIENSTWAGEN;FAHRZEUG;R_100;KENNZEICHEN;1;N\r\n" + "DIENSTWAGEN;MITARBEITER;R_101;PERSONALNR;1;N\r\n"
                 + "MITARBEITER;ABTEILUNG;R_102;APTNUMMER;1;N\r\n"
                 + "MITARBEITER;ABTEILUNG;R_102;APTBEZEICHNUNG;2;N\r\n"
                 + "SCHADENSFALL;MITARBEITER;R_106;SACHBEARBEITER;1;N\r\n"
                 + "VERSVERTRAG;VERSNEHMER;R_103;KUNDENNR;1;N\r\n" + "VERSVERTRAG;FAHRZEUG;R_104;KENNZEICHEN;1;N\r\n"
                 + "VERSVERTRAG;MITARBEITER;R_105;PERSONALNR;1;N\r\n" + "";
  String metaTbls = "Abteilung;1,2; \r\n" + "Dienstwagen;1,3; \r\n" + "Fahrzeug;1,2; \r\n" + "Mitarbeiter;1,2; \r\n"
                    + "Schadensfall;1,2; \r\n" + "VersNehmer;1,2; \r\n" + "VersVertrag;1,2; \r\n" + "";
  String metaCols = "Abteilung;Bezeichnung; \r\n" + "Abteilung;Nummer; \r\n" + "Dienstwagen;Kennzeichen; \r\n"
                    + "Dienstwagen;PersonalNr; \r\n" + "Fahrzeug;Fahrzeugtyp; \r\n" + "Fahrzeug;Farbe; \r\n"
                    + "Fahrzeug;Kennzeichen; \r\n" + "Mitarbeiter;Abteilung; \r\n" + "Mitarbeiter;AptBezeichnung; \r\n"
                    + "Mitarbeiter;AptNummer; \r\n" + "Mitarbeiter;EMail; \r\n" + "Mitarbeiter;Geburtsdatum; \r\n"
                    + "Mitarbeiter;Name; \r\n" + "Mitarbeiter;PersonalNr; \r\n" + "Mitarbeiter;Raum; \r\n"
                    + "Mitarbeiter;Telefon; \r\n" + "Mitarbeiter;Vorname; \r\n" + "Schadensfall;Datum; \r\n"
                    + "Schadensfall;Ort; \r\n" + "Schadensfall;Sachbearbeiter; \r\n"
                    + "Schadensfall;Schadenshoehe; \r\n" + "Schadensfall;Umstaende; \r\n"
                    + "Schadensfall;Verletzte; \r\n" + "Schadensfall;Vorgangsnummer; \r\n"
                    + "VersNehmer;FuehrerscheinSeit; \r\n" + "VersNehmer;Geburtstag; \r\n"
                    + "VersNehmer;Hausnummer; \r\n" + "VersNehmer;KundenNr; \r\n" + "VersNehmer;Name; \r\n"
                    + "VersNehmer;Ort; \r\n" + "VersNehmer;PLZ; \r\n" + "VersNehmer;Strasse; \r\n"
                    + "VersNehmer;Vorname; \r\n" + "VersVertrag;Kennzeichen; \r\n" + "VersVertrag;KundenNr; \r\n"
                    + "VersVertrag;PersonalNr; \r\n" + "VersVertrag;Vertragsabschluss; \r\n"
                    + "VersVertrag;Vertragsart; \r\n" + "VersVertrag;Vertragsnr; \r\n" + "";

  @Test
  public void testDbIntegration() {
    final ModelImpl ndbm = new ModelImpl(new DatabaseConnectorImpl(), new CategoryTranslator());
    final LocalDatabaseManager db = new LocalDatabaseManager(true);
    ndbm.connectLocalDb(db);
    // Import von Datenbank
    final ImportTask importTask = new ImportCsvTask(db, tbls, cols, idxs, pKeys, fKeys, metaTbls, metaCols);
    assertTrue(importTask.startImport());
    // Prüfe Datenbank auf Vollständigkeit
    checkDbIsCorrectlyImported(ndbm);
    // TODO: Neue Tests implementieren
    final Save s = ndbm.getSaveObj();
    // Tabellenname & Kategorie ändern
    checkTableFkConnection(db, "Mitarbeiter", "1,2", "Schadensfall", "Sachbearbeiter", "PersonalNr", "XPKMITARBEITER");
    final String tblNameNeu = "Mitarbeiter2";
    final String tblCat = "1,4";
    s.changeTableName("Mitarbeiter", tblNameNeu, Strings.INDEXTYPE_XPK + tblNameNeu);
    s.changeTableCategory(tblNameNeu, tblCat);
    checkTableFkConnection(db, tblNameNeu, tblCat, "Schadensfall", "Sachbearbeiter", "PersonalNr",
                           Strings.INDEXTYPE_XPK + tblNameNeu);
    // Neue Tabelle einfügen

    // Neue Tabelle wird mit Feldern etc eingefügt

    // Tabelle löschen

    // Feld ändern
    s.changeNodeNameFromTable("PersonalNr", tblNameNeu, "PersoId", "Column");
    checkTableFkConnection(db, tblNameNeu, tblCat, "Schadensfall", "Sachbearbeiter", "PersoId",
                           Strings.INDEXTYPE_XPK + tblNameNeu);
    // Feld löschen

    // Index anlegen

    // Index verändern

    // Index felder hinzufügen

    // Index felder entfernen

    // Index löschen

    // Fremdschlüssel einfügen

    // Fremdschlüssel geändert

    // Fremdschlüssel entfernt

    // Neue Ref Tabelle für Fremdschlüssel

    // Disconnect
    ndbm.disconnectDb();
    assertTrue(!ndbm.isOnline());
  }

  private void checkTableFkConnection(final LocalDatabaseManager db, final String tblNameNeu, final String tblCat,
                                      final String tblQuerCheck, final String tblQuerFieldName,
                                      final String tblPkFieldName, final String tblpkName) {
    final Optional<Table> opt = db.getTable(tblNameNeu);
    assertTrue(opt.isPresent());
    assertTrue(opt.get().getCategory().equals(tblCat));
    assertTrue(opt.get().getXpk().getName().equals(tblpkName));
    final Optional<Table> opt2 = db.getTable(tblQuerCheck);
    assertTrue(opt2.isPresent());
    assertTrue(opt2.get().getForeignKeys().get(0).getRefTable().getName().equals(tblNameNeu));
    assertTrue(opt2.get().getForeignKeys().get(0).getAltName(tblQuerFieldName).equals(tblPkFieldName));
  }

  private void checkDbIsCorrectlyImported(final ModelImpl ndbm) {
    assertTrue(ndbm.getAllTables().size() == 7);
    assertTrue(ndbm.getTable("Dienstwagen").isPresent());
    final Table testTable = ndbm.getTable("Dienstwagen").get();
    assertTrue(testTable.getCategory().equals("1,3"));
    assertTrue(testTable.getFields().size() == 2);
    assertTrue(testTable.getXpk().getFieldList().size() == 1);
    assertTrue(testTable.getXpk().getFieldList().get(0).getName().equals("Kennzeichen"));
    assertTrue(testTable.getXpk().getFieldList().get(0).getDomain().equals(Domain.DomainId.STRING));
    assertTrue(testTable.getXpk().getFieldList().get(0).getDomainLength() == 10);
    final Map<String, Integer> metadata = ndbm.getDatabaseStats();
    assertTrue(metadata.get("") == 46);
    assertTrue(metadata.get("Table") == 7);
    assertTrue(metadata.get("Column") == 39);
    assertTrue(metadata.get("Relation ") == 63);
    assertTrue(metadata.get("Relation XPK") == 8);
    assertTrue(metadata.get("Relation DATA") == 39);
    assertTrue(metadata.get("Relation INDEX") == 8);
    assertTrue(metadata.get("Relation REFERENCE") == 8);
  }
}
