package de.mach.tools.neodesigner.core.nexport;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.Domain.DomainId;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Index;
import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Konkreter SQL Generator für den Export.
 *
 * @author Chris Deter
 *
 */
public class SqlGenerator implements Generator {

  private static final int MAX_SPACE_DATA_FIELD = 22;
  private static final int MAX_SPACE_INDEX = 26;
  private static final Logger LOG = Logger.getLogger(SqlGenerator.class.getName());

  /**
   * erzeugt Leerzeichen.
   *
   * @param count
   *          Anzahl der Leerzeichen
   * @return die angegebene Anzahl an Leerzeichen
   */
  private String getSpace(final int count) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 1; i < count; i++) {
      sb.append(Strings.SPACE);
    }
    return sb.toString();
  }

  @Override
  public String generate(final List<Table> tableList) {
    final long start = new Date().getTime();
    final StringBuilder sb = new StringBuilder();

    for (final Table table : tableList) {
      createTableWithFields(sb, table);
      createIndizes(sb, table);
      createPrimaryKey(sb, table);
    }
    creForeignKeys(tableList, sb);
    final String str = sb.toString().replaceAll(Strings.SEMICOLON, Strings.SEMICOLON + Strings.EOL);
    SqlGenerator.LOG.log(Level.INFO, () -> Strings.EXPORTFROMDB + (new Date().getTime() - start));
    return str;
  }

  /**
   * Erstellt ein PK Statement im SQL.
   *
   * @param sb
   *          Stringbuilder der verwendet werden soll
   * @param table
   *          die Tabelle die den PK enthält.
   */
  private void createPrimaryKey(final StringBuilder sb, final Table table) {
    sb.append(String.format(Strings.SQL_PRIMKEY, table.getName() + Strings.EOL, table.getXpk().getName()));
    for (final Field f : table.getXpk().getFieldList()) {
      sb.append(String.format(Strings.COMMAVALUE, f.getName()));
    }
    sb.deleteCharAt(sb.length() - 2);
    sb.append(Strings.CLOSEDBRACKET + Strings.SPACE + Strings.CLOSEDBRACKET + Strings.SPACE + Strings.SEMICOLON
        + Strings.EOL);
  }

  /**
   * Erstellt alle Index Statements einer Tabelle im SQL.
   *
   * @param sb
   *          Stringbuilder der verwendet werden soll
   * @param table
   *          die Tabelle die die Indizes enthält.
   */
  private void createIndizes(final StringBuilder sb, final Table table) {
    for (final Index index : table.getIndizies()) {
      // OracleSQL: Wenn der Index die selben Felder wie der XPK hat, dann
      // nutze
      // den XPK
      if (!(table.getXpk().getFieldList().containsAll(index.getFieldList())
          && index.getFieldList().containsAll(table.getXpk().getFieldList())
          && index.getName().substring(index.getName().length() - Strings.IMPORTGENERATEDINDEXMARKER.length())
              .equals(Strings.IMPORTGENERATEDINDEXMARKER))) {
        generateIndexStatement(sb, table, index);
      }
    }
  }

  /**
   * Erstellt ein Index Statement einer Tabelle im SQL.
   *
   * @param sb
   *          Stringbuilder der verwendet werden soll
   * @param table
   *          die Tabelle die die Indizes enthält.
   *
   *
   * @param index
   *          der Index zu dem ein SQL Statement erstellt werden soll
   */
  private void generateIndexStatement(final StringBuilder sb, final Table table, final Index index) {
    sb.append(String.format(Strings.SQL_INDEX, index.isUnique() ? Strings.SPACE + Strings.UNIQUE : Strings.EMPTYSTRING,
        index.getName(), table.getName() + Strings.EOL, Strings.EOL));
    for (final Field f : index.getFieldList()) {
      sb.append(String.format(Strings.SQL_INDEX_ELEM,
          f.getName() + getSpace(SqlGenerator.MAX_SPACE_INDEX - f.getName().length()), Strings.EOL));
    }
    sb.deleteCharAt(sb.length() - 3);
    sb.append(Strings.CLOSEDBRACKET + Strings.SEMICOLON + Strings.EOL);
  }

  /**
   * Erstellt für alle Felder einer Tabelle SQL Statements.
   *
   * @param sb
   *          der Stringbuilder
   * @param table
   *          die die Felder enthält
   */
  private void createTableWithFields(final StringBuilder sb, final Table table) {
    sb.append(String.format(Strings.SQL_TABLE_COMMENT, table.getName(), table.getCategory(),
        table.getComment().replaceAll(Strings.CARRIAGE_RETURN, Strings.EMPTYSTRING).replaceAll(Strings.NEWLINE,
            Strings.HTML_BR) + Strings.EOL));
    // Erstelle Tabellen
    sb.append(String.format(Strings.SQL_TABLE, table.getName(), Strings.EOL));
    // mit Feldern
    for (final Field field : table.getFields()) {
      sb.append(String.format(Strings.SQL_TABLE_ELEM,
          field.getName() + getSpace(SqlGenerator.MAX_SPACE_DATA_FIELD - field.getName().length())
              + typeTransl(field.getDomain(), field.getDomainLength()),
          field.isRequired() ? Strings.NOTNULL : Strings.NULL, Strings.EOL));
    }
    sb.deleteCharAt(sb.length() - 3);
    sb.append(Strings.CLOSEDBRACKET + Strings.SEMICOLON + Strings.EOL);
  }

  private String typeTransl(final DomainId domain, final int domainLength) {
    String ret;
    if (domain.equals(DomainId.STRING)) {
      ret = Strings.IMPORT_TYPE_VARCHAR + Strings.OPENBRACKET + domainLength + Strings.CLOSEDBRACKET;
    } else if (domain.equals(DomainId.LOOKUP)) {
      ret = Strings.IMPORT_TYPE_VARCHAR + Strings.OPENBRACKET + 20 + Strings.CLOSEDBRACKET;
    } else if (domain.equals(DomainId.AMOUNT)) {
      ret = Strings.IMPORT_TYPE_NUMBER + Strings.OPENBRACKET + 18 + Strings.COMMA + 5 + Strings.CLOSEDBRACKET;
    } else if (domain.equals(DomainId.COUNTER)) {
      ret = Strings.IMPORT_TYPE_INT;
    } else if (domain.equals(DomainId.BOOLEAN)) {
      ret = Strings.IMPORT_TYPE_SMALLINT;
    } else if (domain.equals(DomainId.BLOB)) {
      ret = Strings.IMPORT_TYPE_BLOB;
    } else {
      // CLOB, DATE
      ret = Domain.getName(domain).toUpperCase();
    }
    return ret;
  }

  /**
   * Erstellt für alle Foreignkeys aller Tabellen SQL Statements.
   *
   * @param tl
   *          die Liste mit allen Tabellen
   * @param sb
   *          der Strinbuilder
   */
  private void creForeignKeys(final List<Table> tl, final StringBuilder sb) {
    for (final Table table : tl) {
      for (final ForeignKey fk : table.getForeignKeys()) {
        sb.append(
            String.format(Strings.SQL_FOREIGNKEY_START, table.getName() + Strings.EOL, fk.getName() + Strings.EOL));
        final List<Field> sortedList = sortPkElem(fk);
        // Schreibe die sortierten Felder
        for (final Field f : sortedList) {
          sb.append(String.format(Strings.COMMAVALUE, f.getName()));
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(String.format(Strings.SQL_FOREIGNKEY_END, Strings.EOL, fk.getRefTable().getName()));
        sb.append(Strings.CLOSEDBRACKET + Strings.SPACE + Strings.SEMICOLON + Strings.EOL);
      }
    }
  }

  /**
   * Sortiert die Field Elemente in die Reihenfolge des Pks auf die der FK
   * verweist.
   *
   * @param fk
   *          der Felder und RefTable mit PK enthält
   * @return eine sortierte Liste mit Feldern die die gleiche Reihenfolge wie der
   *         PK hat
   */
  private List<Field> sortPkElem(final ForeignKey fk) {
    // Sortiere Felder nach PK Reihenfolge
    final List<Field> sortedList = new ArrayList<>();
    for (final Field rf : fk.getRefTable().getXpk().getFieldList()) {
      for (final Field ff : fk.getIndex().getFieldList()) {
        if (fk.getIndex().getAltName(ff.getName()).equals(rf.getName())) {
          sortedList.add(ff);
        }
      }
    }
    return sortedList;
  }
}
