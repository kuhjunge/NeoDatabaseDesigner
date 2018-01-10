package de.mach.tools.neodesigner.core.datamodel.impl;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Node;

/**
 * Implementation vom Field.
 *
 * @author Chris Deter
 *
 */
public class FieldImpl extends NodeImpl implements Field {
  private String typeOfData = "";
  private boolean isRequired = true;
  private boolean isPartOfPrimaryKey = false;

  /**
   * Konstruktor
   * 
   * @param name
   *          des Feldes
   */
  public FieldImpl(final String name) {
    super(name);
  }

  /**
   * Konstruktor Field.
   *
   * @param name
   *          Der Name
   * @param datatype
   *          Der Datentyp
   * @param req
   *          ist das Feld required
   * @param nodeOf
   *          die Tabelle des Feldes
   */
  public FieldImpl(final String name, final String datatype, final boolean req, final Node nodeOf) {
    super(name, nodeOf);
    setRequired(req);
    setTypeOfData(datatype);
  }

  @Override
  public String getTypeOfData() {
    return typeOfData;
  }

  @Override
  public void setTypeOfData(final String typeOfData) {
    this.typeOfData = typeOfData;
  }

  @Override
  public boolean isRequired() {
    return isRequired;
  }

  @Override
  public void setRequired(final boolean isNull) {
    isRequired = isNull;
  }

  @Override
  public boolean isPartOfPrimaryKey() {
    return isPartOfPrimaryKey;
  }

  @Override
  public void setPartOfPrimaryKey(final boolean prim) {
    isPartOfPrimaryKey = prim;
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Field)) {
      return false;
    }
    final Field other = (Field) o;
    return getName().equals(other.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
