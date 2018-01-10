package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLBaseListener;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreFieldContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreForeignKeyContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreIndexContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CrePrimKeyContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreTableContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreUniqueIndexContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CrefieldNameListContext;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.FieldnameContext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * ANTLR Import Listener für die Verarbeitung des input SQLs.
 *
 * @author Chris Deter
 *
 */
class SqlImportListener extends SQLBaseListener {
  List<Table> tables = new ArrayList<>();
  private Table actualTable;
  private Index actualIndex;
  private ForeignKey actualFk;
  private final List<Field> tempFields = new ArrayList<>();
  private static final Logger LOG = Logger.getLogger(SqlImportListener.class.getName());

  @Override
  public void enterCreTable(final CreTableContext ctx) {
    actualTable = new TableImpl(ctx.tablename().getText());
  }

  @Override
  public void exitCreTable(final CreTableContext ctx) {
    tables.add(actualTable);
    actualTable = null;
  }

  @Override
  public void enterCreField(final CreFieldContext ctx) {
    final FieldImpl f = new FieldImpl(ctx.fieldname().getText(), ctx.type().getText(), ctx.isNull() == null, // isrequired
        actualTable);
    actualTable.addField(f);
  }

  @Override
  public void enterCrePrimKey(final CrePrimKeyContext ctx) {
    createIndex(ctx.indexname().getText(), ctx.tablename().getText());
    actualTable.setXpk(actualIndex);
  }

  @Override
  public void enterCrefieldNameList(final CrefieldNameListContext ctx) {
    for (final FieldnameContext fnc : ctx.fieldname()) {
      final Field field = actualTable.getData().get(actualTable.getData().indexOf(new FieldImpl(fnc.getText())));
      if (actualIndex != null) {
        actualIndex.addField(field);
      } else {
        tempFields.add(field);
      }
    }
  }

  @Override
  public void enterCreIndex(final CreIndexContext ctx) {
    createIndex(ctx.indexname().getText(), ctx.tablename().getText());
    actualTable.getIndizies().add(actualIndex);
  }

  @Override
  public void enterCreUniqueIndex(final CreUniqueIndexContext ctx) {
    createIndex(ctx.indexname().getText(), ctx.tablename().getText());
    actualTable.getIndizies().add(actualIndex);
    actualIndex.setUnique(true);
  }

  @Override
  public void enterCreForeignKey(final CreForeignKeyContext ctx) {
    enterForeignKey(ctx.tablename(1).getText(), ctx.tablename(0).getText(), ctx.indexname().getText());
  }

  /**
   * Alles was ausgeführt werden soll, wenn ein Fremdschlüssel (FK) betreten
   * wird.
   *
   * @param refTableName
   * @param tableName
   * @param indexName
   */
  private void enterForeignKey(final String refTableName, final String tableName, final String indexName) {
    final Table refTable = new TableImpl(refTableName);
    getActualTable(tableName);
    final ForeignKey index = new ForeignKeyImpl(indexName, actualTable);
    actualIndex = null;
    index.setRefTable(tables.get(tables.indexOf(refTable)));
    actualTable.getForeignKeys().add(index);
    actualFk = index;
  }

  @Override
  public void exitCreForeignKey(final CreForeignKeyContext ctx) {
    final List<Field> primKeyFields = actualFk.getRefTable().getXpk().getFieldList();
    setFieldForeignkeyRelation(actualTable, actualFk, tempFields);
    for (final Field f : actualFk.getFieldList()) {
      actualFk.setAltName(f.getName(), getFieldFromRefField(primKeyFields, f, tempFields.indexOf(f)).getName());
    }
    tempFields.clear();
  }

  /**
   * Erstellt einen Index
   *
   * @param name
   * @param tablename
   */
  private void createIndex(final String name, final String tablename) {
    getActualTable(tablename);
    final Index index = new IndexImpl(name, actualTable);
    actualIndex = index;
  }

  /**
   * Verbindet die Felder mit dem Fremdschlüssel.
   *
   * @param t
   * @param fk
   * @param indexFields
   */
  private void setFieldForeignkeyRelation(final Table t, final ForeignKey fk, final List<Field> indexFields) {
    final List<Field> primKeyFields = fk.getRefTable().getXpk().getFieldList();
    final List<Index> possible = t
        .getIndizies().stream().filter(p -> p.getType() == Index.Type.XIF
            && p.getFieldList().size() == primKeyFields.size() && p.getFieldList().containsAll(indexFields))
        .collect(Collectors.toList());
    fk.setIndex(getSelectedFromList(fk, possible, indexFields));
  }

  /**
   * Ordnet den Index ein Fremschlüssel zu
   *
   * @param fk
   * @param ixfsWithSameSize
   * @param indexFields
   * @return
   */
  private Index getSelectedFromList(final ForeignKey fk, final List<Index> ixfsWithSameSize,
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
      selected = generateMissingIndex(fk, indexFields, number);
    }
    return selected;
  }

  /**
   * generiert einen fehlenden Index für den Fremdschlüssel
   *
   * @param fk
   * @param indexFields
   * @param number
   * @return
   */
  private Index generateMissingIndex(final ForeignKey fk, final List<Field> indexFields, final String number) {
    Index selected;
    selected = new IndexImpl("XIF" + number + fk.getNodeOf().getName() + Strings.IMPORTGENERATEDINDEXMARKER,
        fk.getNodeOf());
    for (final Field f : indexFields) {
      selected.addField(f);
    }
    actualTable.getIndizies().add(selected);
    SqlImportListener.LOG.info(() -> String.format(Strings.CORRECTEDIMPORT, actualTable.getName(), selected.getName()));
    return selected;
  }

  /**
   * Ordnet das Feld einen Feld in dem PK der Referenztabelle zu.
   *
   * @param primKeyFields
   * @param ref
   * @param posInFk
   * @return
   */
  private Field getFieldFromRefField(final List<Field> primKeyFields, final Field ref, final int posInFk) {
    Field ret = new FieldImpl("");
    // Wenn der Primärschlüssel das Feld enthält (namentlich) und der Typ gleich
    // ist
    if (primKeyFields.contains(ref)) {
      for (final Field f : primKeyFields) {
        if (f.getName().equals(ref.getName()) && f.getTypeOfData().equals(ref.getTypeOfData())) {
          ret = f;
        }
      }
    }
    // wenn keine Name = Name & Typ = Typ übereinstimmung dann nur nach typ
    // übereinstimmung suchen
    // Wenn im PK die Pos de schlüssels den im FK entspricht und der Typ
    // gleich ist
    if (ret.getName().equals(Strings.EMPTYSTRING)
        && primKeyFields.get(posInFk).getTypeOfData().equals(ref.getTypeOfData())) {
      ret = primKeyFields.get(posInFk);
    }
    return ret;
  }

  /**
   * Läd mithilfe des Tabellennamens die Tabelle aus der Liste der importierten
   * Tabellen
   *
   * @param tablename
   */
  private void getActualTable(final String tablename) {
    final Table table = new TableImpl(tablename);
    actualTable = tables.get(tables.indexOf(table));
  }
}