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

package de.mach.tools.neodesigner.inex.nexport.tex;


import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Index.Type;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.inex.nexport.tex.model.Column;
import de.mach.tools.neodesigner.inex.nexport.tex.model.PdfDataModel;


/** Klasse zum Erstellen eines Datenmodells für die PDF Erstellung.
 *
 * @author cd */
public class LineProcessor {
  private static final Logger LOG = Logger.getLogger(LineProcessor.class.getName());

  private static String getTransl(final DomainId input, final int length) {
    String ret = Domain.getName(input);
    if (input.equals(DomainId.STRING)) {
      ret = ret + length;
    }
    else if (input.equals(DomainId.DATE)) {
      ret = Strings.DOMAIN_DATETIME;
    }
    else if (input.equals(DomainId.BLOB)) {
      ret = Strings.DOMAIN_RAWBLOB;
    }
    else if (input.equals(DomainId.CLOB)) {
      ret = Strings.DOMAIN_STRINGBLOB;
    }
    return ret;
  }

  private final PdfDataModel datamodel;

  /** Constructor
   *
   * @param catTransl Ausgeschriebene Bedeutung der Kategorien */
  public LineProcessor(final Map<String, String> catTransl) {
    datamodel = new PdfDataModel(catTransl);
  }

  /** gibt die Kategorie als String zurück z.B.: 1,1
   *
   * @param t Tabelle
   * @return Name der Kategorie */
  private String checkCategory(final Table t) {
    final String[] val = t.getCategory().split(Strings.COMMA);
    int number = 0;
    try {
      number += Integer.parseInt(val[0]);
      number += Integer.parseInt(val[1]);
    }
    catch (final Exception e) {
      number = 0;
      LineProcessor.LOG.log(Level.WARNING, e.getMessage(), e);
    }
    return number > 0 ? t.getCategory() : Strings.CATEGORYNONE;
  }

  /** generiert das Datenmodel für das PDF aus den Tabellen.
   *
   * @param tables Tabellen die ins PDF gegeben werden sollen */
  public void generate(final List<Table> tables) {
    for (final Table t : tables) {
      setFieldInfo(t);
      setFkInfo(t);
      setIndexInfo(t);
    }
  }

  public PdfDataModel getDatamodel() {
    return datamodel;
  }

  private void setFieldInfo(final Table t) {
    // Setzt die Domain, den Datentypen, Ob das Feld ein Pflichtfeld ist und ob des
    // teil eines Pflichtfeldes ist
    for (final Field f : t.getFields()) {
      final Column column = datamodel.getColumn(checkCategory(t), t.getName(), f.getName());
      column.setDomain(LineProcessor.getTransl(f.getDomain(), f.getDomainLength()));
      column.setNullable(!f.isRequired());
      column.setPrimaryKey(t.getXpk().getFieldList().contains(f));
      column.setForeignKey(false);
    }
  }

  private void setFkInfo(final Table t) {
    // Für die Lookup Domain und das setzten der Verlinkung des Fremdschlüssels
    for (final ForeignKey fk : t.getForeignKeys()) {
      for (final Field ffk : fk.getIndex().getFieldList()) {
        datamodel.addFkToList(fk.getName(), fk.getTable().getName(), fk.getRefTable().getName(), ffk.getName(),
                              fk.getIndex().getOrder(ffk.getName(), false));
        final Column column = datamodel.getColumn(checkCategory(t), t.getName(), ffk.getName());
        column.setForeignKey(true);
        column.setForeignKeyTableName(fk.getRefTable().getName());
      }
    }
  }

  private void setIndexInfo(final Table t) {
    // Für die Spalte in der XIE und XAK Schlüssel eingeblendet werden
    for (final Index i : t.getIndizies()) {
      if (i.getType().equals(Type.XIE) || i.getType().equals(Type.XAK)) {
        for (final Field fx : i.getFieldList()) {
          datamodel.getColumn(checkCategory(t), t.getName(), fx.getName())
              .addIndexColumn(i.getName(), i.getOrder(fx.getName(), false));
        }
      }
    }
  }
}
