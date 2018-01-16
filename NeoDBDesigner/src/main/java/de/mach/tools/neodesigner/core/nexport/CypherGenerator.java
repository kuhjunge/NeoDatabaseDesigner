package de.mach.tools.neodesigner.core.nexport;

import de.mach.tools.neodesigner.core.Strings;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.Table;
import de.mach.tools.neodesigner.database.cypher.QueryBuilder;

import java.util.List;

public class CypherGenerator implements Generator {

  @Override
  public String generate(final List<Table> tableList) {
    final QueryBuilder qb = new QueryBuilder();
    final StringBuilder query = new StringBuilder();
    for (final Table table : tableList) {
      qb.importTable(table);
      query.append(qb + Strings.SEMICOLON + Strings.EOL);
    }
    for (final Table table : tableList) {
      for (final ForeignKey fk : table.getForeignKeys()) {
        qb.importForeignKey(fk);
        query.append(qb + Strings.SEMICOLON + Strings.EOL);
      }
    }
    return query.toString().replaceAll("CREATE", Strings.EOL + "CREATE").replaceAll("MATCH", Strings.EOL + "MATCH")
        .replaceAll("MERGE", Strings.EOL + "MERGE");
  }
}
