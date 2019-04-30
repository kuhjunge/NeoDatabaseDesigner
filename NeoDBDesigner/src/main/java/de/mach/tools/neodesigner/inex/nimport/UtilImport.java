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

package de.mach.tools.neodesigner.inex.nimport;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.Util;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;


/** Tools für den Import.
 *
 * @author Chris Deter */
public class UtilImport {
  private static final Logger LOG = Logger.getLogger(UtilImport.class.getName());

  /** generiert einen fehlenden Index für den Fremdschlüssel.
   *
   * @param fk Fremdschlüssel
   * @param indexFields Felder des Indexes
   * @param number Nummer des Fremdschlüssels
   * @return neuen Index */
  private static Index generateMissingIndex(final ForeignKey fk, final List<Field> indexFields, final String number) {
    Index selected = new IndexImpl(Util.getShortName(Strings.INDEXTYPE_XIG + number + fk.getTable().getName(),
                                                     Strings.INDEXNAMELENGTH),
                                   fk.getTable());
    for (final Field f : indexFields) {
      selected.addField(f);
    }
    // Wenn identisch mit Primärschlüssel, dann Primärschlüsselsortierung übernehmen
    if (fk.getTable().getXpk().getFieldList().containsAll(indexFields)) {
      for (final Field f : indexFields) {
        selected.setOrder(f.getName(), fk.getTable().getXpk().getOrder(f.getName(), false), false);
      }
    }
    fk.getTable().getIndizies().add(selected);
    UtilImport.LOG.log(Level.FINE,
                       () -> String.format(Strings.CORRECTEDIMPORT, fk.getTable().getName(), selected.getName()));
    return selected;
  }

  /** Ordnet den Index einen Fremschlüssel zu.
   *
   * @param fk der Fremschlüssel
   * @param ixfsWithSameSize alle Indizes die passen könnten
   * @param indexFields die Felder des Indexes
   * @return der dazugehörige Index */
  private static Index getSelectedFromList(final ForeignKey fk, final List<Index> ixfsWithSameSize,
                                           final List<Field> indexFields) {
    Index selected = null;
    String number = "0";
    if (ixfsWithSameSize.size() == 1) {
      selected = ixfsWithSameSize.get(0);
    }
    else {
      number = fk.getName().substring(2, fk.getName().length() > 8 ? 8 : fk.getName().length()).replaceAll("\\D+", "");
      // Indizes vergleichen
      for (final Index couldbe : ixfsWithSameSize) {
        if (couldbe.getName().contains(number)) {
          // Wenn Index die Nummer des FK enthaelt
          selected = couldbe;
        }
      }
    }
    if (selected == null) {
      selected = UtilImport.generateMissingIndex(fk, indexFields, number);
    }
    return selected;
  }

  // VARCHAR2(20) = Lookup
  // VARCHAR2(X) = StringX
  // CHAR(X) = StringX
  // NUMBER(18,5) = Amount
  // INTEGER = Counter
  // SMALLINT = Boolean
  // DATE = Datetime
  // LONG RAW = RawBlob
  // BLOB = RawBlob
  // CLOB = StringBlob
  public static Domain oracleTypeToDomain(final String type) {
    String ret;
    final String[] tod = type.split(Strings.IMPORT_TYPE_SPLIT);
    switch (tod[0]) {
      case Strings.IMPORT_TYPE_VARCHAR:
      case Strings.IMPORT_TYPE_CHAR:
        // VARCHAR , CHAR
        ret = Domain.getName(DomainId.STRING) + Strings.COLON
              + tod[1].replace(Strings.REPLACECHAR_BRACKETOPEN, Strings.REPLACECHAR_SPACE).trim();
        break;
      case Strings.IMPORT_TYPE_NUMBER:
        // NUMBER(18,5)
        ret = Domain.getName(DomainId.AMOUNT);
        break;
      case Strings.IMPORT_TYPE_INT:
        // INTEGER
        ret = Domain.getName(DomainId.COUNTER);
        break;
      case Strings.IMPORT_TYPE_SMALLINT:
        // SMALLINT
        ret = Domain.getName(DomainId.BOOLEAN);
        break;
      case Strings.IMPORT_TYPE_LONGRAW:
        // Handle like a blob
      case Strings.IMPORT_TYPE_BLOB:
        // BLOB
        ret = Domain.getName(DomainId.BLOB);
        break;
      default:
        ret = Domain.getName(Domain.getFromName(type));
        break;
    }
    return new Domain(ret);
  }

  /** Verbindet die Felder mit dem Fremdschlüssel.
   *
   * @param fk der Fremdschlüssel
   * @param indexFields die Felder des Indexes vom Fremdschlüssel */
  public static void setFieldForeignkeyRelation(final ForeignKey fk, final List<Field> indexFields) {
    // Hole Felder vom Primärschlüssel der Referenztabelle
    final List<Field> primKeyFields = fk.getRefTable().getXpk().getFieldList();
    // Filtere heraus, welche Indizes als Index für den Fremdschlüssel passen würden
    final List<Index> possible = fk.getTable().getIndizies().stream()
        .filter(p -> p.getType() == Index.Type.XIF && p.getFieldList().size() == primKeyFields.size()
                     && p.getFieldList().containsAll(indexFields))
        .collect(Collectors.toList());
    // Wähle den passenden Index für die Tabelle
    fk.setIndex(UtilImport.getSelectedFromList(fk, possible, indexFields));
    int i = 1;
    for (final Field f : indexFields) {
      if (fk.getIndex().getOrder(f.getName(), false) != i) {
        LOG.log(Level.WARNING, "Table: " + f.getTable().getName() + " Index not correct ordered: "
                               + fk.getIndex().getName() + " Field: " + f.getName());
      }
      fk.setFkOrder(fk.getTable().getField(f.getName()), i++);
    }
  }

  private UtilImport() {}
}
