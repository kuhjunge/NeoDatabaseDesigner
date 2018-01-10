package de.mach.tools.neodesigner.ui;

import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewNodeImpl;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * Die Erstellung eines Tabs für die Tabelle.
 *
 * @author Chris Deter
 *
 */
public class DbTableTabView {
  private static final String[] DATABASE_DETAILS_TEXT = new String[] { Strings.NAME_FIELDS, Strings.NAME_INDEXES,
      Strings.NAME_FKS };
  private final String[] datatypeArray;
  private final Tab tab = new Tab();
  private final TabPane tableView;
  private final TitledPane[] tps = new TitledPane[DbTableTabView.DATABASE_DETAILS_TEXT.length];

  // Table Buttons
  private Button saveButton;
  private Button deleteButton;
  private TextField editName;
  private TextField editKategory;
  // Buttons for FK & Index
  private Button addIndex;
  private Button addFk;
  private Button modIndexRel;
  private Button modFkRel;
  private TableView<ViewIndex> tvi;
  private TableView<ViewForeignKey> tvfk;

  /**
   * Konstruktor
   *
   * @param tv
   * @param selectArray
   */
  public DbTableTabView(final TabPane tv, final String[] selectArray) {
    tableView = tv;
    datatypeArray = selectArray;
  }

  /**
   * Gibt den TableView für Index zurück
   *
   * @return
   */
  public TableView<ViewIndex> getTvi() {
    return tvi;
  }

  /**
   * Gibt den TableView für ForeignKey zurück
   *
   * @return
   */
  public TableView<ViewForeignKey> getTvfk() {
    return tvfk;
  }

  /**
   * Getter
   *
   * @return
   */
  public Button getAddIndex() {
    return addIndex;
  }

  /**
   * Getter
   *
   * @return
   */
  public Button getAddFk() {
    return addFk;
  }

  /**
   * Getter
   *
   * @return
   */
  public Button getModIndexRel() {
    return modIndexRel;
  }

  /**
   * Getter
   *
   * @return
   */
  public Button getModFkRel() {
    return modFkRel;
  }

  /**
   * Getter
   *
   * @return
   */
  public Tab getTab() {
    return tab;
  }

  /**
   * Getter
   *
   * @return
   */
  public Button getSaveButton() {
    return saveButton;
  }

  /**
   * Getter
   *
   * @return
   */
  public Button getDeleteButton() {
    return deleteButton;
  }

  /**
   * Entfernt dieses Tabellentab aus der View
   *
   * @return
   */
  public void removeTab() {
    tableView.getTabs().remove(tab);
  }

  /**
   * Getter
   *
   * @return
   */
  public String getNewName() {
    return editName.getText().trim();
  }

  /**
   * Getter
   *
   * @return
   */
  public String getNewKategory() {
    return editKategory.getText().trim();
  }

  /**
   * generiert ein neues Tab.
   *
   * @param tbl
   *          die Tabelle in der die Daten für den Tab sind
   */
  public void createNewTab(final ViewTable tbl) {
    tab.setClosable(true);
    tab.setText(tbl.getName());
    final VBox vbox = new VBox();
    vbox.setPadding(new Insets(0, 5, 5, 0));
    vbox.setAlignment(Pos.TOP_LEFT);
    final FlowPane flowButtons = createViewHead(tbl);
    vbox.getChildren().add(flowButtons);
    final Accordion viewData = createViewAccordion(tbl);
    vbox.getChildren().add(viewData);
    tab.setContent(vbox);
    tableView.getTabs().add(tab);
  }

  /**
   * Generiert in der GUI die Ansicht mit dem Tabellennamen und den
   * Tabelleneigenschaften und Schaltflächen zum Löschen oder Speichern
   *
   * @param t
   * @return
   */
  private FlowPane createViewHead(final ViewTable t) {
    // Tabellen Name, Kategorie und Speichernfunktion
    final FlowPane flowButtons = new FlowPane(Orientation.HORIZONTAL);
    flowButtons.setPadding(new Insets(5, 5, 5, 5));
    flowButtons.setHgap(18);
    flowButtons.getChildren().add(new Label(Strings.LABELNAME_NAME));
    editName = new TextField(t.getName());
    flowButtons.getChildren().add(editName);
    editName.textProperty()
        .addListener((observable, oldValue, newValue) -> t.getXpk().setName(Strings.RELNAME_XPK + newValue));
    flowButtons.getChildren().add(new Label(Strings.LABELNAME_KATEGORY));
    editKategory = new TextField(t.getCategory());
    flowButtons.getChildren().add(editKategory);
    deleteButton = new Button(Strings.BUTTONNAME_DELETE);
    flowButtons.getChildren().add(deleteButton);
    saveButton = new Button(Strings.BUTTONNAME_SAVE);
    flowButtons.getChildren().add(saveButton);
    return flowButtons;
  }

  /**
   * erstellt eine VBox für die Feld, Index und FK Ansicht
   *
   * @param tv
   * @param fp
   * @return
   */
  private <E extends ViewNodeImpl> VBox createVboxTable(final TableView<E> tv, final FlowPane fp) {
    final VBox vbox = new VBox();
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.getChildren().add(tv);
    fp.setPadding(new Insets(5, 5, 5, 5));
    fp.setHgap(18);
    vbox.getChildren().add(fp);
    return vbox;
  }

  /**
   * Erstellt das Accordeon Menu welches Feld, Index und FK Tabellen enthält
   *
   * @param t
   * @return
   */
  private Accordion createViewAccordion(final ViewTable t) {
    // Accordion View
    final Accordion accordion = new Accordion();
    final VBox vboxFields = createDataTable(t);
    tps[0] = new TitledPane(DbTableTabView.DATABASE_DETAILS_TEXT[0], vboxFields);

    final VBox vboxIndizies = createIndiziesTable(t);
    tps[1] = new TitledPane(DbTableTabView.DATABASE_DETAILS_TEXT[1], vboxIndizies);

    final VBox vboxFk = createForeignKeyTable(t);
    tps[2] = new TitledPane(DbTableTabView.DATABASE_DETAILS_TEXT[2], vboxFk);

    accordion.getPanes().addAll(tps);
    accordion.setExpandedPane(tps[0]);
    return accordion;
  }

  /**
   * Erstellt die Tabellenansicht für Feld
   *
   * @param t
   * @return
   */
  private VBox createDataTable(final ViewTable t) {
    final TableView<ViewField> tvf = generateTableForFields(t);
    final FlowPane fp = new FlowPane(Orientation.HORIZONTAL);
    final Button addField = new Button(Strings.BUTTONNAME_ADD + Strings.NAME_FIELD);
    fp.getChildren().add(addField);
    addDeleteButton(t, tvf, fp, Strings.NAME_FIELD);
    final VBox vboxFields = createVboxTable(tvf, fp);
    addField.setOnAction(event -> newTableField(t, tvf));
    return vboxFields;
  }

  /**
   * erstellt die Tabellenansicht für den Fremdschlüssel
   *
   * @param t
   * @return
   */
  private VBox createForeignKeyTable(final ViewTable t) {
    tvfk = generateTableForForeignKeys(t);
    final FlowPane fp = new FlowPane(Orientation.HORIZONTAL);
    addFk = new Button(Strings.BUTTONNAME_ADD + Strings.NAME_FK);
    fp.getChildren().add(addFk);
    modFkRel = new Button(Strings.BUTTONNAME_MODIFY + Strings.NAME_FK);
    fp.getChildren().add(modFkRel);
    addDeleteButtonFk(t, tvfk, fp, Strings.NAME_FK);
    return createVboxTable(tvfk, fp);
  }

  /**
   * Erstellt die Index Tabelle
   *
   * @param t
   * @return
   */
  private VBox createIndiziesTable(final ViewTable t) {
    tvi = generateTableForIndizies(t);
    final FlowPane fp = new FlowPane(Orientation.HORIZONTAL);
    addIndex = new Button(Strings.BUTTONNAME_ADD + Strings.NAME_INDEX);
    fp.getChildren().add(addIndex);
    modIndexRel = new Button(Strings.BUTTONNAME_MODIFY + Strings.NAME_INDEX);
    fp.getChildren().add(modIndexRel);
    addDeleteButton(t, tvi, fp, Strings.NAME_INDEX);
    return createVboxTable(tvi, fp);
  }

  /**
   * Fgüt einen Delete Button in die Ansicht hinzu
   *
   * @param t
   * @param tv
   * @param fp
   * @param name
   */
  private <E extends ViewNodeImpl> void addDeleteButton(final ViewTable t, final TableView<E> tv, final FlowPane fp,
      final String name) {
    final Button rmFk = new Button(Strings.BUTTONNAME_DELETE + name);
    fp.getChildren().add(rmFk);
    deleteButtonHandler(t, tv, rmFk);
  }

  /**
   * Fügt einen Delte Button für den Fremdschlüssel hinzu
   *
   * @param t
   * @param tv
   * @param fp
   * @param name
   */
  private void addDeleteButtonFk(final ViewTable t, final TableView<ViewForeignKey> tv, final FlowPane fp,
      final String name) {
    final Button rmFk = new Button(Strings.BUTTONNAME_DELETE + name);
    fp.getChildren().add(rmFk);
    deleteButtonFkHandler(t, tv, rmFk);
  }

  /**
   * Delete Handler für den Fremdschlüssel DeleteButton
   *
   * @param t
   * @param tv
   * @param rmVn
   */
  private void deleteButtonFkHandler(final ViewTable t, final TableView<ViewForeignKey> tv, final Button rmVn) {
    rmVn.setOnAction(e -> {
      final ViewForeignKey selectedItem = tv.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        final List<ViewField> lvf = new ArrayList<>();
        lvf.addAll(
            t.getVData().stream().filter(p -> selectedItem.getFieldList().contains(p)).collect(Collectors.toList()));
        t.delete.add(selectedItem);
        t.foreignKeys.remove(selectedItem);
        t.delete.add(selectedItem.getVIndex());
        t.indizes.remove(selectedItem.getVIndex());
        t.delete.addAll(lvf);
        t.dataFields.removeAll(lvf);
      }
    });
  }

  /**
   * Delete Handlr für den DeleteButton
   *
   * @param t
   * @param tv
   * @param rmVn
   */
  private <E extends ViewNodeImpl> void deleteButtonHandler(final ViewTable t, final TableView<E> tv,
      final Button rmVn) {
    rmVn.setOnAction(e -> {
      final E selectedItem = tv.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        t.delete.add(selectedItem);
        tv.getItems().remove(selectedItem);
      }
    });
  }

  /**
   * Erstellt ein neues Feld für die Feld Tabelle
   *
   * @param t
   * @param tvf
   */
  private void newTableField(final ViewTable t, final TableView<ViewField> tvf) {
    final ViewField f = new ViewField(Strings.NAME_NEWFIELD, Strings.NAME_NEWFIELD_DATATYPE, false, t);
    if (!t.dataFields.contains(f)) {
      f.setAsNewCreated();
      t.dataFields.add(f);
      tvf.selectionModelProperty().get().clearSelection();
      tvf.selectionModelProperty().get().select(f);
    }
  }

  /**
   * Generiert den Inhalt für die Field Tabelle
   *
   * @param t
   * @return
   */
  private TableView<ViewField> generateTableForFields(final ViewTable t) {
    final TableView<ViewField> table = new TableView<>();
    table.setItems(t.dataFields);
    table.getColumns().add(createTableRowName());
    table.getColumns().add(createTableRowTypeOfData());
    table.getColumns().add(createTableRowRequired());
    table.getColumns().add(createTableRowPrimKey());
    formatTable(table);
    return table;
  }

  /**
   * Formatiert die Tabelle
   *
   * @param table
   */
  private void formatTable(final TableView<?> table) {
    table.getSelectionModel().selectFirst();
    table.setEditable(true);
    table.setPrefHeight(800);
  }

  /**
   * Erstellt eine Tabellenspalte für die Primärschüssel Information
   *
   * @return
   */
  private TableColumn<ViewField, Boolean> createTableRowPrimKey() {
    final TableColumn<ViewField, Boolean> isPrimKey = new TableColumn<>(Strings.NAME_FIELD_ISPRIM);
    isPrimKey.setCellValueFactory(f -> f.getValue().primProperty());
    isPrimKey.setCellFactory(tc -> new CheckBoxTableCell<>());
    return isPrimKey;
  }

  /**
   * Erstellt eine Tabellenspalte für die Required Information
   *
   * @return
   */
  private TableColumn<ViewField, Boolean> createTableRowRequired() {
    final TableColumn<ViewField, Boolean> isNullCol = new TableColumn<>(Strings.NAME_FIELD_ISNULL);
    isNullCol.setCellValueFactory(f -> f.getValue().requiredProperty());
    isNullCol.setCellFactory(tc -> new CheckBoxTableCell<>());
    return isNullCol;
  }

  /**
   * Erstellt eine Tabellenspalte für die TyopeOfData Information
   *
   * @return
   */
  private TableColumn<ViewField, String> createTableRowTypeOfData() {
    final ObservableList<String> typeChoice = FXCollections.observableArrayList(datatypeArray);
    final TableColumn<ViewField, String> typeOfDataCol = new TableColumn<>(Strings.NAME_FIELD_DATATYPE);
    typeOfDataCol.setCellValueFactory(f -> f.getValue().typeOfDataProperty());
    typeOfDataCol.setCellFactory(ComboBoxTableCell.forTableColumn(typeChoice));
    return typeOfDataCol;
  }

  /**
   * Erstellt eine Tabellenspalte für die Namensinformation
   *
   * @return
   */
  private TableColumn<ViewField, String> createTableRowName() {
    final TableColumn<ViewField, String> nameCol = new TableColumn<>(Strings.TABLEROW_NAME);
    nameCol.setCellValueFactory(f -> f.getValue().nameProperty());
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    return nameCol;
  }

  /**
   * Generiert den Inhalt der Index Tabelle
   *
   * @param t
   * @return
   */
  private TableView<ViewIndex> generateTableForIndizies(final ViewTable t) {
    final TableView<ViewIndex> table = new TableView<>();
    // table.getItems().add(t.xpk);
    table.setItems(t.indizes);
    table.getColumns().add(createTableRowNameIndex());
    // Type of Data
    final TableColumn<ViewIndex, Boolean> isUnique = new TableColumn<>(Strings.NAME_INDEX_ISUNIQUE);
    isUnique.setCellValueFactory(f -> f.getValue().uniqueProperty());
    isUnique.setCellFactory(tc -> new CheckBoxTableCell<>());
    table.getColumns().add(isUnique);
    // Fields
    table.getColumns().add(generateFieldListCell());
    formatTable(table);
    return table;
  }

  /**
   * Generiert en Tabelleninhalt für die Fremdschlüsseltabelle.
   *
   * @param t
   * @return
   */
  private TableView<ViewForeignKey> generateTableForForeignKeys(final ViewTable t) {
    final TableView<ViewForeignKey> table = new TableView<>();
    table.setItems(t.foreignKeys);
    table.getColumns().add(createTableRowNameIndex());
    // Fields
    final TableColumn<ViewForeignKey, String> refTable = new TableColumn<>(Strings.NAME_TABLE);
    refTable.setCellValueFactory(f -> f.getValue().getRefTableName());
    refTable.setCellFactory(TextFieldTableCell.forTableColumn());
    table.getColumns().add(refTable);
    // Fields
    table.getColumns().add(generateFieldListCell());
    formatTable(table);
    refTable.setEditable(false);
    return table;
  }

  /**
   * Generiert eine Liste mit Feldnamen und eine Tabellenspalte in der diese
   * angezeigt werden.
   *
   * @return
   */
  private <T extends ViewIndex> TableColumn<T, String> generateFieldListCell() {
    final TableColumn<T, String> ofFields = new TableColumn<>(Strings.NAME_FIELDS);
    ofFields.setCellValueFactory(f -> f.getValue().fieldListStringProperty());
    ofFields.setCellFactory(TextFieldTableCell.forTableColumn());
    ofFields.setEditable(false);
    return ofFields;
  }

  /**
   * Generiert eine Tabellenspalte für den Namen vom View Index
   * 
   * @return
   */
  private <T extends ViewIndex> TableColumn<T, String> createTableRowNameIndex() {
    final TableColumn<T, String> nameCol = new TableColumn<>(Strings.TABLEROW_NAME);
    nameCol.setCellValueFactory(f -> f.getValue().nameProperty());
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    return nameCol;
  }

}
