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

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.ImportTask;
import de.mach.tools.neodesigner.inex.nimport.ImportCsvTask;


class TestDatenmodelIntegration {

  @Test
  void testDbIntegration() {
    // TODO: Integrationstest weiter ausbauen
    Model model = new ModelImpl(new MockConfigSaver(), new MockConfigSaver());
    model.connect();
    // Import von Datenbank
    String metaCols = "Abteilung;Bezeichnung; \r\n" + "Abteilung;Nummer; \r\n" + "Dienstwagen;Kennzeichen; \r\n"
                      + "Dienstwagen;PersonalNr; \r\n" + "Fahrzeug;Fahrzeugtyp; \r\n" + "Fahrzeug;Farbe; \r\n"
                      + "Fahrzeug;Kennzeichen; \r\n" + "Mitarbeiter;Abteilung; \r\n"
                      + "Mitarbeiter;AptBezeichnung; \r\n" + "Mitarbeiter;AptNummer; \r\n" + "Mitarbeiter;EMail; \r\n"
                      + "Mitarbeiter;Geburtsdatum; \r\n" + "Mitarbeiter;Name; \r\n" + "Mitarbeiter;PersonalNr; \r\n"
                      + "Mitarbeiter;Raum; \r\n" + "Mitarbeiter;Telefon; \r\n" + "Mitarbeiter;Vorname; \r\n"
                      + "Schadensfall;Datum; \r\n" + "Schadensfall;Ort; \r\n" + "Schadensfall;Sachbearbeiter; \r\n"
                      + "Schadensfall;Schadenshoehe; \r\n" + "Schadensfall;Umstaende; \r\n"
                      + "Schadensfall;Verletzte; \r\n" + "Schadensfall;Vorgangsnummer; \r\n"
                      + "VersNehmer;FuehrerscheinSeit; \r\n" + "VersNehmer;Geburtstag; \r\n"
                      + "VersNehmer;Hausnummer; \r\n" + "VersNehmer;KundenNr; \r\n" + "VersNehmer;Name; \r\n"
                      + "VersNehmer;Ort; \r\n" + "VersNehmer;PLZ; \r\n" + "VersNehmer;Strasse; \r\n"
                      + "VersNehmer;Vorname; \r\n" + "VersVertrag;Kennzeichen; \r\n" + "VersVertrag;KundenNr; \r\n"
                      + "VersVertrag;PersonalNr; \r\n" + "VersVertrag;Vertragsabschluss; \r\n"
                      + "VersVertrag;Vertragsart; \r\n" + "VersVertrag;Vertragsnr; \r\n" + "";
    String metaTbls = "Abteilung;1,2; \r\n" + "Dienstwagen;1,3; \r\n" + "Fahrzeug;1,2; \r\n" + "Mitarbeiter;1,2; \r\n"
                      + "Schadensfall;1,2; \r\n" + "VersNehmer;1,2; \r\n" + "VersVertrag;1,2; \r\n" + "";
    String fKeys = "DIENSTWAGEN;FAHRZEUG;R_100;KENNZEICHEN;1;N\r\n" + "DIENSTWAGEN;MITARBEITER;R_101;PERSONALNR;1;N\r\n"
                   + "MITARBEITER;ABTEILUNG;R_102;APTNUMMER;1;N\r\n"
                   + "MITARBEITER;ABTEILUNG;R_102;APTBEZEICHNUNG;2;N\r\n"
                   + "SCHADENSFALL;MITARBEITER;R_106;SACHBEARBEITER;1;N\r\n"
                   + "VERSVERTRAG;VERSNEHMER;R_103;KUNDENNR;1;N\r\n" + "VERSVERTRAG;FAHRZEUG;R_104;KENNZEICHEN;1;N\r\n"
                   + "VERSVERTRAG;MITARBEITER;R_105;PERSONALNR;1;N\r\n" + "";
    String pKeys = "ABTEILUNG;XPKABTEILUNG;NUMMER;1\r\n" + "ABTEILUNG;XPKABTEILUNG;BEZEICHNUNG;2\r\n"
                   + "DIENSTWAGEN;XPKDIENSTWAGEN;KENNZEICHEN;1\r\n" + "FAHRZEUG;XPKFAHRZEUG;KENNZEICHEN;1\r\n"
                   + "MITARBEITER;XPKMITARBEITER;PERSONALNR;1\r\n" + "SCHADENSFALL;XPKSCHADENSFALL;VORGANGSNUMMER;1\r\n"
                   + "VERSNEHMER;XPKVERSNEHMER;KUNDENNR;1\r\n" + "VERSVERTRAG;XPKVERSVERTRAG;VERTRAGSNR;1\r\n" + "";
    String idxs = "DIENSTWAGEN;XIF101MITARBEITER;PERSONALNR;1;N\r\n" + "MITARBEITER;XIF102ABTEILUNG;APTNUMMER;1;N\r\n"
                  + "MITARBEITER;XIF102ABTEILUNG;APTBEZEICHNUNG;2;N\r\n"
                  + "SCHADENSFALL;XIF106MITARBEITER;SACHBEARBEITER;1;N\r\n"
                  + "VERSVERTRAG;XIF103VERSNEHMER;KUNDENNR;1;N\r\n" + "VERSVERTRAG;XIF104FAHRZEUG;KENNZEICHEN;1;N\r\n"
                  + "VERSVERTRAG;XIF105MITARBEITER;PERSONALNR;1;N\r\n" + "";
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
    String tbls = "ABTEILUNG\r\n" + "DIENSTWAGEN\r\n" + "FAHRZEUG\r\n" + "MITARBEITER\r\n" + "SCHADENSFALL\r\n"
                  + "VERSNEHMER\r\n" + "VERSVERTRAG";
    final ImportTask importTask = new ImportCsvTask(tbls, cols, idxs, pKeys, fKeys, metaTbls, metaCols);
    assertTrue(importTask.startImport());
    model.importTables(importTask.getList());
    checkDbIsCorrectlyImported(model);
    final Save s = model.getSaveObj();
    // Tabellenname & Kategorie ändern
    checkTableFkConnection(model, "Mitarbeiter", "1,2", "Schadensfall", "Sachbearbeiter", "PersonalNr",
                           "XPKMITARBEITER");
    final String tblNameNeu = "Mitarbeiter2";
    final String tblCat = "1,4";
    s.changeTableName("Mitarbeiter", tblNameNeu, Strings.INDEXTYPE_XPK + tblNameNeu);
    s.changeTableCategory(tblNameNeu, tblCat);
    checkTableFkConnection(model, tblNameNeu, tblCat, "Schadensfall", "Sachbearbeiter", "PersonalNr",
                           Strings.INDEXTYPE_XPK + tblNameNeu);
    Assert.assertEquals("[1,2, 1, 1,3, 1,4]", model.getAllCategories().toString());
    // Neue Tabelle einfügen

    // Neue Tabelle wird mit Feldern etc eingefügt

    // Tabelle löschen

    // Feld ändern
    s.changeNodeNameFromTable("PersonalNr", tblNameNeu, "PersoId", "Column");
    checkTableFkConnection(model, tblNameNeu, tblCat, "Schadensfall", "Sachbearbeiter", "PersoId",
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
    assertTrue(model.deleteDatabase());
  }

  void checkDbIsCorrectlyImported(final Model model) {
    assertEquals(7, model.getAllTables().size());
    assertTrue(model.getTable("Dienstwagen").isPresent());
    final Table testTable = model.getTable("Dienstwagen").get();
    assertEquals("1,3", testTable.getCategory());
    assertEquals(2, testTable.getFields().size());
    assertEquals(1, testTable.getXpk().getFieldList().size());
    assertEquals("Kennzeichen", testTable.getXpk().getFieldList().get(0).getName());
    assertEquals(testTable.getXpk().getFieldList().get(0).getDomain(), Domain.DomainId.STRING);
    assertEquals(10, testTable.getXpk().getFieldList().get(0).getDomainLength());
    final Map<String, Integer> metadata = model.getDatabaseStats();
    assertEquals(46, (int) metadata.get(""));
    assertEquals(7, (int) metadata.get("Table"));
    assertEquals(39, (int) metadata.get("Column"));
    assertEquals(63, (int) metadata.get("Relation "));
    assertEquals(8, (int) metadata.get("Relation XPK"));
    assertEquals(39, (int) metadata.get("Relation DATA"));
    assertEquals(8, (int) metadata.get("Relation INDEX"));
    assertEquals(8, (int) metadata.get("Relation REFERENCE"));
  }

  private void checkTableFkConnection(final Model db, final String tblNameNeu, final String tblCat,
                                      final String tblQuerCheck, final String tblQuerFieldName,
                                      final String tblPkFieldName, final String tblpkName) {
    final Optional<Table> opt = db.getTable(tblNameNeu);
    assertTrue(opt.isPresent());
    assertEquals(opt.get().getCategory(), tblCat);
    assertEquals(opt.get().getXpk().getName(), tblpkName.toUpperCase());
    final Optional<Table> opt2 = db.getTable(tblQuerCheck);
    assertTrue(opt2.isPresent());
    assertEquals(opt2.get().getForeignKeys().get(0).getRefTable().getName(), tblNameNeu);
    assertEquals(opt2.get().getForeignKeys().get(0).getAltName(tblQuerFieldName), tblPkFieldName);
  }
}
