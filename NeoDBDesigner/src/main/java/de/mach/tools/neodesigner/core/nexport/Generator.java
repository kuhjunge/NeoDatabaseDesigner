package de.mach.tools.neodesigner.core.nexport;

import de.mach.tools.neodesigner.core.datamodel.Table;

import java.util.List;

/**
 * Generator Interface für den Export.
 *
 * @author Chris Deter
 *
 */
public interface Generator {
  /**
   * generiert aus dem Datenmodel eine Textrepresentation einer anderen
   * Darstellung.
   *
   * @param tableList
   *          Das Datenmodel
   * @return Strinrepresentation des Datenbankmodels
   */
  public String generate(List<Table> tableList);
}
