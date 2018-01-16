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

package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Tools für den Import.
 *
 * @author Chris Deter
 *
 */
class UtilImport {
  private static final Logger LOG = Logger.getLogger(UtilImport.class.getName());

  private UtilImport() {
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
    String ret = "";
    final String[] tod = type.split(Strings.IMPORT_TYPE_SPLIT);
    if (Strings.IMPORT_TYPE_VARCHAR.equals(tod[0]) || Strings.IMPORT_TYPE_CHAR.equals(tod[0])) {
      // VARCHAR , CHAR
      ret = Domain.getName(DomainId.STRING) + Strings.COLON
          + tod[1].replace(Strings.REPLACECHAR_BRACKETOPEN, Strings.REPLACECHAR_SPACE).trim();
    } else if (Strings.IMPORT_TYPE_NUMBER.equals(tod[0])) {
      // NUMBER(18,5)
      ret = Domain.getName(DomainId.AMOUNT);
    } else if (Strings.IMPORT_TYPE_INT.equals(tod[0])) {
      // INTEGER
      ret = Domain.getName(DomainId.COUNTER);
    } else if (Strings.IMPORT_TYPE_SMALLINT.equals(tod[0])) {
      // SMALLINT
      ret = Domain.getName(DomainId.BOOLEAN);
    } else if (Strings.IMPORT_TYPE_BLOB.equals(tod[0])) {
      // BLOB
      ret = Domain.getName(DomainId.BLOB);
    } else {
      ret = Domain.getName(Domain.getFromName(type));
    }
    return new Domain(ret);
  }

  // Cannot identify "SMALLINT"
  public static Domain csvTypeToDomain(final String name, final String strSize, final String nrSize) {
    String ret = "";
    if (Strings.IMPORT_TYPE_VARCHAR.equals(name)) {
      // VARCHAR , CHAR
      ret = Domain.getName(DomainId.STRING) + Strings.COLON + strSize;
    } else if (Strings.IMPORT_TYPE_NUMBER.equals(name) && "18".equals(nrSize)) {
      // NUMBER(18,5)
      ret = Domain.getName(DomainId.AMOUNT);
    } else if (Strings.IMPORT_TYPE_NUMBER.equals(name)) {
      // INTEGER
      ret = Domain.getName(DomainId.COUNTER);
    } else if (Strings.IMPORT_TYPE_BLOB.equals(name)) {
      // LONG RAW -> BLOB
      ret = Domain.getName(DomainId.BLOB);
    } else {
      // DATE, CLOB, BLOB
      ret = Domain.getName(Domain.getFromName(name));
    }
    return new Domain(ret);
  }

  /**
   * Ordnet das Feld einen Feld in dem PK der Referenztabelle zu.
   *
   * @param primKeyFields
   *          Felder des Primärschlüssels
   * @param ref
   *          referenzfeld
   * @param posInFk
   *          Position im Fremdschlüssel
   * @return das passende Feld aus dem PK
   */
  static Field getFieldFromRefField(final List<Field> primKeyFields, final Field ref, final int posInFk) {
    Field ret = new FieldImpl(Strings.EMPTYSTRING);
    // Wenn der Primärschlüssel das Feld enthält (namentlich) und der Typ gleich
    // ist
    if (primKeyFields.contains(ref)) {
      for (final Field f : primKeyFields) {
        if (f.getName().equals(ref.getName()) && UtilImport.isSameDomain(f, ref)) {
          ret = f;
        }
      }
    }
    // wenn keine Name = Name & Typ = Typ übereinstimmung dann nur nach typ
    // übereinstimmung suchen
    // Wenn im PK die Pos de schlüssels den im FK entspricht und der Typ
    // gleich ist
    if (ret.getName().equals(Strings.EMPTYSTRING) && posInFk >= 0
        && UtilImport.isSameDomain(primKeyFields.get(posInFk), ref)) {
      ret = primKeyFields.get(posInFk);
    }
    if (ret.getName().equals(Strings.EMPTYSTRING)) {
      UtilImport.LOG.warning(() -> "Field without matching FK Reference!");
    }
    return ret;
  }

  private static boolean isSameDomain(final Field f, final Field ref) {
    return f.getDomain().equals(ref.getDomain()) && f.getDomainLength() == ref.getDomainLength()
        || f.getDomain().equals(DomainId.LOOKUP) && ref.getDomain().equals(DomainId.STRING)
            && ref.getDomainLength() == 20
        || ref.getDomain().equals(DomainId.LOOKUP) && f.getDomain().equals(DomainId.STRING)
            && f.getDomainLength() == 20;
  }

  /**
   * Verbindet die Felder mit dem Fremdschlüssel.
   *
   * @param fk
   *          der Fremdschlüssel
   * @param indexFields
   *          die Felder des Indexes vom Fremdschlüssel
   */
  public static void setFieldForeignkeyRelation(final ForeignKey fk, final List<Field> indexFields) {
    final List<Field> primKeyFields = fk.getRefTable().getXpk().getFieldList();
    final List<Index> possible = fk
        .getTable().getIndizies().stream().filter(p -> p.getType() == Index.Type.XIF
            && p.getFieldList().size() == primKeyFields.size() && p.getFieldList().containsAll(indexFields))
        .collect(Collectors.toList());
    fk.setIndex(UtilImport.getSelectedFromList(fk, possible, indexFields));
  }

  /**
   * Ordnet den Index ein Fremschlüssel zu.
   *
   * @param fk
   *          der Fremschlüssel
   * @param ixfsWithSameSize
   *          alle Indizes die passen könnten
   * @param indexFields
   *          die Felder des Indexes
   * @return der dazugehörige Index
   */
  private static Index getSelectedFromList(final ForeignKey fk, final List<Index> ixfsWithSameSize,
      final List<Field> indexFields) {
    Index selected = null;
    String number = "0";
    if (ixfsWithSameSize.size() == 1) {
      selected = ixfsWithSameSize.get(0);
    } else {
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

  /**
   * generiert einen fehlenden Index für den Fremdschlüssel.
   *
   * @param fk
   *          Fremdschlüssel
   * @param indexFields
   *          Felder des Indexes
   * @param number
   *          Nummer des Fremdschlüssels
   * @return neuen Index
   */
  private static Index generateMissingIndex(final ForeignKey fk, final List<Field> indexFields, final String number) {
    Index selected;
    selected = new IndexImpl("XIF" + number + fk.getTable().getName() + Strings.IMPORTGENERATEDINDEXMARKER,
        fk.getTable());
    for (final Field f : indexFields) {
      selected.addField(f);
    }
    fk.getTable().getIndizies().add(selected);
    UtilImport.LOG.info(() -> String.format(Strings.CORRECTEDIMPORT, fk.getTable().getName(), selected.getName()));
    return selected;
  }
}
