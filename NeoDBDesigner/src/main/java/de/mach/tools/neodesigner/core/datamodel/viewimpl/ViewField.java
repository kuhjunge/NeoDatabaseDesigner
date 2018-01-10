package de.mach.tools.neodesigner.core.datamodel.viewimpl;

import de.mach.tools.neodesigner.core.datamodel.Field;
import de.mach.tools.neodesigner.core.datamodel.Node;
import de.mach.tools.neodesigner.core.datamodel.impl.FieldImpl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Implementation von Field für den View.
 *
 * @author Chris Deter
 *
 */
public class ViewField extends ViewNodeImpl implements Field {
  private final StringProperty typeOfData = new SimpleStringProperty();
  private final BooleanProperty isRequired = new SimpleBooleanProperty(true);
  private final BooleanProperty isPartOfPrimary = new SimpleBooleanProperty(false);
  private boolean modifiedType = false;
  private boolean modifiedReq = false;
  private boolean modifiedPrim = false;
  private final Field data;

  /**
   * Konstruktor.
   *
   * @param name
   *          Name des Feldes
   * @param datatype
   *          Datentyp des Feldes
   * @param req
   *          ist das Feld ein Pflichtfeld?
   * @param nodeOf
   *          Tabelle dem das Feld zugeordnet ist
   */
  public ViewField(final String name, final String datatype, final boolean req, final Node nodeOf) {
    super(new FieldImpl(name, datatype, req, nodeOf));
    data = (Field) super.getNode();
    setListener();
  }

  /**
   * Konstruktor für den Imprort eines vorhanden Feldes.
   *
   * @param f
   *          das Feld
   * @param t
   *          Die tabelle dem das neue Feld zugeordnet werden soll
   */
  public ViewField(final Field f, final ViewTable t) {
    super(new FieldImpl(f.getName(), f.getTypeOfData(), f.isRequired(), t));
    data = (Field) super.getNode();
    data.setPartOfPrimaryKey(f.isPartOfPrimaryKey());
    setListener();
  }

  /**
   * Setzt die Listener um änderungen im Objekt auch im internen Model zu ändern
   */
  private void setListener() {
    setTypeOfData(data.getTypeOfData());
    setRequired(data.isRequired());
    setPartOfPrimaryKey(data.isPartOfPrimaryKey());
    typeOfData.addListener((obs, oldValue, newValue) -> {
      modifiedType = true;
      data.setTypeOfData(newValue);
    });
    isRequired.addListener((obs, oldValue, newValue) -> {
      modifiedReq = true;
      data.setRequired(newValue);
    });
    isPartOfPrimary.addListener((obs, oldValue, newValue) -> {
      modifiedPrim = true;
      data.setPartOfPrimaryKey(newValue);
    });
  }

  @Override
  public String getTypeOfData() {
    return typeOfData.get();
  }

  @Override
  public void setTypeOfData(final String typeOfData) {
    this.typeOfData.set(typeOfData);
  }

  @Override
  public boolean isRequired() {
    return isRequired.get();
  }

  @Override
  public void setRequired(final boolean isRequired) {
    this.isRequired.set(isRequired);
  }

  @Override
  public boolean isPartOfPrimaryKey() {
    return isPartOfPrimary.get();
  }

  @Override
  public void setPartOfPrimaryKey(final boolean prim) {
    isPartOfPrimary.set(prim);
  }

  @Override
  public void saved() {
    super.saved();
    modifiedType = false;
    modifiedReq = false;
    modifiedPrim = false;
  }

  /**
   * View Methode um zu erkennnen ob ein Typ verändert wurde.
   *
   * @return True wenn ein Typ verändert wurde
   */
  public boolean isModifiedType() {
    return modifiedType;
  }

  /**
   * View Methode um zu erkennen ob die Zuorndung zum Primärschlüssel geändert
   * wurde.
   *
   * @return True wenn die Zuordnung zum Primärschlüssel verändert wurde
   */
  public boolean isModifiedPrim() {
    return modifiedPrim;
  }

  /**
   * View Methode um zu erkennen ob die Eigenschaft Required verändert wurde
   * 
   * @return True wenn die Eigenschaft Required verändert wurde
   */
  public boolean isModifiedReq() {
    return modifiedReq;
  }

  /**
   * View Methode um eine Prim Property für die Tabelle in der GUI zu bekommen
   * 
   * @return Property Eigenschaft für View
   */
  public BooleanProperty primProperty() {
    return isPartOfPrimary;
  }

  /**
   * View Methode um eine typeOfData Property für die Tabelle in der GUI zu
   * bekommen
   * 
   * @return Property Eigenschaft für View
   */
  public StringProperty typeOfDataProperty() {
    return typeOfData;
  }

  /**
   * View Methode um eine Required Property für die Tabelle in der GUI zu
   * bekommen
   * 
   * @return Property Eigenschaft für View
   */
  public BooleanProperty requiredProperty() {
    return isRequired;
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
