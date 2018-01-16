package de.mach.tools.neodesigner.core.nimport;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.ForeignKeyImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.IndexImpl;
import de.mach.tools.neodesigner.core.datamodel.impl.TableImpl;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLBaseListener;
import de.mach.tools.neodesigner.core.nimport.antlrsql.SQLParser.CreCategoryContext;
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
import java.util.Optional;

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
  private String category = "";
  private String comment = "";
  private final List<Field> tempFields = new ArrayList<>();

  @Override
  public void enterCreCategory(final CreCategoryContext ctx) {
    final String infoLine = ctx.getText();
    if (infoLine.startsWith("-- Table: ")) {
      final String[] temp = infoLine.split(Strings.COLON);
      if (temp.length > 2) {
        category = temp[2].trim().split(Strings.SPACE)[0].trim();
      }
      if (temp.length > 3) {
        comment = temp[3].trim();
      }
    }
  }

  @Override
  public void enterCreTable(final CreTableContext ctx) {
    actualTable = new TableImpl(ctx.tablename().getText());
    if (category.length() > 0) {
      actualTable.setCategory(category);
      actualTable.setComment(comment);
    }
    category = "";
  }

  @Override
  public void exitCreTable(final CreTableContext ctx) {
    tables.add(actualTable);
    actualTable = null;
  }

  @Override
  public void enterCreField(final CreFieldContext ctx) {
    final Domain d = UtilImport.oracleTypeToDomain(ctx.type().getText());
    final FieldImpl f = new FieldImpl(ctx.fieldname().getText(), d.getDomain(), d.getDomainlength(),
        ctx.isNull() == null, Strings.EMPTYSTRING, actualTable);
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
      final Optional<Field> field = actualTable.getField(fnc.getText());
      if (field.isPresent()) {
        if (actualIndex != null) {
          actualIndex.addField(field.get());
        } else {
          tempFields.add(field.get());
        }
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
    final String refTableName = ctx.tablename(1).getText();
    final String tableName = ctx.tablename(0).getText();
    final String indexName = ctx.indexname().getText();

    final Table refTable = new TableImpl(refTableName);
    actualTable = getActualTable(tableName);
    final ForeignKey index = new ForeignKeyImpl(indexName, actualTable);
    actualIndex = null;
    index.setRefTable(tables.get(tables.indexOf(refTable)));
    actualTable.getForeignKeys().add(index);
    actualFk = index;
  }

  @Override
  public void exitCreForeignKey(final CreForeignKeyContext ctx) {
    final List<Field> primKeyFields = actualFk.getRefTable().getXpk().getFieldList();
    UtilImport.setFieldForeignkeyRelation(actualFk, tempFields);
    for (final Field f : actualFk.getIndex().getFieldList()) {
      if (f.getDomain().equals(DomainId.STRING) && f.getDomainLength() == 20) {
        f.setDomain(DomainId.LOOKUP);
      }
      actualFk.getIndex().setAltName(f.getName(),
          UtilImport.getFieldFromRefField(primKeyFields, f, tempFields.indexOf(f)).getName());
      // TODO: Test der diese Funktion prüft
    }
    tempFields.clear();
  }

  /**
   * Erstellt einen Index.
   *
   * @param name
   *          Name des Indexes
   * @param tablename
   *          Name der übergeordneten Tabelle
   */
  private void createIndex(final String name, final String tablename) {
    actualTable = getActualTable(tablename);
    final Index index = new IndexImpl(name, actualTable);
    actualIndex = index;
  }

  /**
   * Läd mithilfe des Tabellennamens die Tabelle aus der Liste der importierten
   * Tabellen.
   *
   * @param tablename
   *          Der Tabellenname
   */
  private Table getActualTable(final String tablename) {
    final Table table = new TableImpl(tablename);
    return tables.get(tables.indexOf(table));
  }
}