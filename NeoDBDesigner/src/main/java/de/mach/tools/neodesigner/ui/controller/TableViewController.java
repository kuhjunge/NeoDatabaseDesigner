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

package de.mach.tools.neodesigner.ui.controller;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import de.mach.tools.neodesigner.core.Validator;
import de.mach.tools.neodesigner.core.category.CategoryObj;
import de.mach.tools.neodesigner.core.datamodel.Domain;
import de.mach.tools.neodesigner.core.datamodel.ForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewField;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewForeignKey;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewIndex;
import de.mach.tools.neodesigner.core.datamodel.viewimpl.ViewTable;
import de.mach.tools.neodesigner.ui.GuiUtil;
import de.mach.tools.neodesigner.ui.Strings;
import de.mach.tools.neodesigner.ui.controller.customcells.NameCell;


/** Diese Klasse generiert ein Tab mit einer Tabelle aus dem Datenbankmodel.
 *
 * @author Chris Deter */
public class TableViewController implements Initializable {
  private final String[] datatypeArray;
  private ObservableList<CategoryObj> categories;
  private int tabindex = 1;
  private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");


  // Main
  @FXML
  private VBox tableContainer;

  @FXML
  private TextField tableNameField;

  @FXML
  private TabPane tabSorter;

  // Main Buttons
  @FXML
  private Button saveTbl;

  @FXML
  private Button deleteTbl;

  // Meta
  @FXML
  private Button openGraph;

  @FXML
  private ComboBox<CategoryObj> selectCategory;

  @FXML
  private TextArea tableComment;

  // Field
  @FXML
  private TableView<ViewField> tvField;

  @FXML
  private Button addField;

  @FXML
  private Button deleteField;

  // Index
  @FXML
  private TableView<ViewIndex> tvIndex;

  @FXML
  private Button addIndex;

  @FXML
  private Button deleteIndex;

  @FXML
  private Button modifyIndex;

  // ForeignKey
  @FXML
  private TableView<ViewForeignKey> tvForeignkey;

  @FXML
  private Button addForeignkey;

  @FXML
  private Button deleteForeignkey;

  @FXML
  private Button modifyForeignkey;
  // RefTable
  @FXML
  private TableView<ViewForeignKey> tvRefTable;

  /** Konstruktor.
   *
   * @param selectArray Array das alle Typs die für ein Feld möglich sind, enthält */
  public TableViewController(final String[] selectArray) {
    datatypeArray = selectArray;
  }

  /** Functions that activates the New Element Function Context Sensitive. */
  void createNew() {
    final int i = tabSorter.getTabs().indexOf(tabSorter.getSelectionModel().getSelectedItem());
    switch (i) {
      case 1:
        addField.fire();
        break;
      case 2:
        addIndex.fire();
        break;
      case 3:
        addForeignkey.fire();
        break;
    }
  }

  /** Erstellt eine Tabellenspalte für die Namensinformation.
   *
   * @return Die Tabellenspalte */
  private TableColumn<ViewField, String> createTableRowComment() {
    final TableColumn<ViewField, String> nameCol = new TableColumn<>(Strings.TABLEROW_COMMENT);
    nameCol.setCellValueFactory(f -> f.getValue().commentProperty());
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    nameCol.setPrefWidth(300);
    return nameCol;
  }

  private TableColumn<ViewField, String> createTableRowOrder() {
    final TableColumn<ViewField, String> nameCol = new TableColumn<>(Strings.TABLEROW_ORDER);
    nameCol.setCellValueFactory(f -> f.getValue().orderProperty());
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    nameCol.setPrefWidth(50);
    return nameCol;
  }

  /** Erstellt eine Tabellenspalte für die Namensinformation.
   *
   * @return Die Tabellenspalte */
  private TableColumn<ViewField, String> createTableRowName(final Validator validator2) {
    final TableColumn<ViewField, String> nameCol = new TableColumn<>(Strings.TABLEROW_NAME);
    nameCol.setCellValueFactory(f -> f.getValue().nameProperty());
    nameCol.setCellFactory(NameCell.forTableColumn(validator2));
    return nameCol;
  }

  /** Generiert eine Tabellenspalte für den Namen vom View Index.
   *
   * @return die Tabellenspalte */
  private TableColumn<ViewForeignKey, String> createTableRowNameFk() {
    final TableColumn<ViewForeignKey, String> nameCol = new TableColumn<>(Strings.TABLEROW_NAME);
    nameCol.setCellValueFactory(f -> f.getValue().nameProperty());
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    return nameCol;
  }

  /** Generiert eine Tabellenspalte für den Namen vom View Index.
   *
   * @return die Tabellenspalte */
  private <T extends ViewIndex> TableColumn<T, String> createTableRowNameIndex() {
    final TableColumn<T, String> nameCol = new TableColumn<>(Strings.TABLEROW_NAME);
    nameCol.setCellValueFactory(f -> f.getValue().nameProperty());
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    return nameCol;
  }

  /** Erstellt eine Tabellenspalte für die Primärschüssel Information.
   *
   * @return Row Primkey */
  private TableColumn<ViewField, Boolean> createTableRowPrimKey() {
    final TableColumn<ViewField, Boolean> isPrimKey = new TableColumn<>(Strings.NAME_FIELD_ISPRIM);
    isPrimKey.setCellValueFactory(f -> f.getValue().primProperty());
    isPrimKey.setCellFactory(tc -> new CheckBoxTableCell<>());
    return isPrimKey;
  }

  /** Erstellt eine Tabellenspalte für die Required Information.
   *
   * @return Row Required */
  private TableColumn<ViewField, Boolean> createTableRowRequired() {
    final TableColumn<ViewField, Boolean> isNullCol = new TableColumn<>(Strings.NAME_FIELD_ISNULL);
    isNullCol.setCellValueFactory(f -> f.getValue().requiredProperty());
    isNullCol.setCellFactory(tc -> new CheckBoxTableCell<>());
    return isNullCol;
  }

  /** Erstellt eine Tabellenspalte für die TyopeOfData Information.
   *
   * @return Row Type of Data */
  private TableColumn<ViewField, String> createTableRowTypeOfData() {
    final ObservableList<String> typeChoice = FXCollections.observableArrayList(datatypeArray);
    final TableColumn<ViewField, String> typeOfDataCol = new TableColumn<>(Strings.NAME_FIELD_DATATYPE);
    typeOfDataCol.setCellValueFactory(f -> f.getValue().typeOfDataProperty());
    typeOfDataCol.setCellFactory(ComboBoxTableCell.forTableColumn(typeChoice));
    typeOfDataCol.setPrefWidth(100);
    return typeOfDataCol;
  }

  /** Delete für den Field DeleteButton.
   *
   * @param t Die Tabelle
   * @param tv der TableView */
  private void deleteField(final ViewTable t, final TableView<ViewField> tv) {
    final ViewField selectedItem = tv.getSelectionModel().getSelectedItem();
    if (selectedItem != null && !selectedItem.hasIndex()) {
      t.addNoteToDeleteList(selectedItem);
      t.setModified();
      tv.getItems().remove(selectedItem);
    }
  }

  /** Delete Handler für den Fremdschlüssel DeleteButton.
   *
   * @param t Tabelle
   * @param tv TableView für ForeignKey */
  private void deleteForeignkey(final ViewTable t, final TableView<ViewForeignKey> tv) {
    final ViewForeignKey selectedItem = tv.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      t.addNoteToDeleteList(selectedItem);
      t.getForeignKeys().remove(selectedItem);
      t.addNoteToDeleteList(selectedItem.getVIndex());
      t.getIndizesRaw().remove(selectedItem.getVIndex());
      t.setModified();
      tv.getItems().remove(selectedItem);
      tvIndex.getItems().remove(selectedItem.getVIndex());
      selectedItem.setModified();
      selectedItem.getVIndex().setModified();
    }
  }

  /** Delete Handler für den DeleteButton.
   *
   * @param t Die Tabelle
   * @param tv der TableView */
  private void deleteIndex(final ViewTable t, final TableView<ViewIndex> tv) {
    final ViewIndex selectedItem = tv.getSelectionModel().getSelectedItem();
    if (selectedItem != null && !selectedItem.hasFk()) {
      t.addNoteToDeleteList(selectedItem);
      t.setModified();
      tv.getItems().remove(selectedItem);
    }
  }

  /** Füllt das Tab mit den Daten aus einer Tabelle.
   *
   * @param tbl Die Tabelle
   * @param validator Validator für die validierung des Tabellenamens */
  public void fillTabWithData(final ViewTable tbl, final Validator validator) {
    tableNameField.setText(tbl.getName());
    tableNameField.textProperty().addListener((o, ov, nv) -> {
      tbl.setModified();
      tbl.setName(nv);
    });
    GuiUtil.validator(tableNameField, validator, tbl.getName());
    tableComment.setText(tbl.getComment());
    tableComment.textProperty().addListener((o, ov, nv) -> tbl.setModified());
    initComboBox(tbl);
    generateTableForFields(tbl, validator);
    addField.setOnAction(event -> newField(tbl, tvField));
    deleteField.setOnAction(e -> deleteField(tbl, tvField));
    generateTableForIndizies(tbl);
    deleteIndex.setOnAction(e -> deleteIndex(tbl, tvIndex));
    generateTableForForeignKeys(tbl);
    deleteForeignkey.setOnAction(e -> deleteForeignkey(tbl, tvForeignkey));
    generateTableForRefTables(tbl);
    tabSorter.getSelectionModel().select(tabSorter.getTabs().get(1));
  }

  private void focusNewElement(final TableView<ViewField> tvf, final ViewField f, final ViewTable t) {
    f.setAsNewCreated();
    t.getDataFieldsRaw().add(f);
    tvf.selectionModelProperty().get().clearSelection();
    tvf.selectionModelProperty().get().select(f);
    final int row = tvf.itemsProperty().get().size() - 1;
    tvf.scrollTo(row);
    tvf.getFocusModel().focus(row);
    // Layout Hack: edit wont work without layout()?
    tvf.layout();
    tvf.edit(row, tvf.getColumns().get(0));
  }

  public void focusTableName() {
    GuiUtil.repeatFocus(tableNameField);
  }

  /** Formatiert die Tabelle.
   *
   * @param table die Tabelle */
  private void formatTable(final TableView<?> table) {
    table.getSelectionModel().selectFirst();
    table.setEditable(true);
    table.setPrefHeight(1080);
  }

  /** Generiert eine Liste mit Feldnamen und eine Tabellenspalte in der diese angezeigt werden.
   *
   * @return die Tabellenspalte */
  private <T extends ViewIndex> TableColumn<T, String> generateFieldListCell() {
    final TableColumn<T, String> ofFields = new TableColumn<>(Strings.NAME_FIELDS);
    ofFields.setCellValueFactory(f -> f.getValue().fieldListStringProperty());
    ofFields.setCellFactory(TextFieldTableCell.forTableColumn());
    ofFields.setEditable(false);
    return ofFields;
  }

  /** Generiert eine Liste mit Feldnamen und eine Tabellenspalte in der diese angezeigt werden.
   *
   * @return die Tabellenspalte */
  private TableColumn<ViewForeignKey, String> generateFieldListCellFk() {
    final TableColumn<ViewForeignKey, String> ofFields = new TableColumn<>(Strings.NAME_FIELDS);
    ofFields.setCellValueFactory(f -> f.getValue().fieldListStringProperty());
    ofFields.setCellFactory(TextFieldTableCell.forTableColumn());
    ofFields.setEditable(false);
    return ofFields;
  }

  /** Generiert den Inhalt für die Field Tabelle.
   *
   * @param t die Tabelle
   * @param validator2 der Validator */
  private void generateTableForFields(final ViewTable t, final Validator validator2) {
    tvField.setItems(t.getDataFieldsRaw());
    tvField.getColumns().add(createTableRowName(validator2));
    tvField.getColumns().add(createTableRowTypeOfData());
    tvField.getColumns().add(createTableRowRequired());
    tvField.getColumns().add(createTableRowPrimKey());
    tvField.getColumns().add(createTableRowOrder());
    tvField.getColumns().add(createTableRowComment());
    formatTable(tvField);

    for (TableColumn c : tvField.getColumns()) {
      c.setSortable(false);
    }

    tvField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
      if (e.getCode() == KeyCode.TAB) {
        final int row = tvField.getSelectionModel().getSelectedCells().get(0).getRow();
        final int col = tabindex++;
        if (tabindex >= tvField.getColumns().size()) {
          tabindex = 0;
        }
        tvField.scrollTo(row);
        // Layout Hack: edit wont work without layout()?
        tvField.layout();
        tvField.edit(row, tvField.getColumns().get(col));
      }
      if ((e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.ESCAPE)
          && tvField.editingCellProperty().getValue() == null) {
        // Wenn gerade kein Feld im Bearbeitungsmodus ist
        GuiUtil.repeatFocus(tvField);
      }
    });

    tvField.setRowFactory(tv -> {
      TableRow<ViewField> row = new TableRow<>();

      row.setOnDragDetected(event -> {
        if (!row.isEmpty()) {
          Integer index = row.getIndex();
          Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
          db.setDragView(row.snapshot(null, null));
          ClipboardContent cc = new ClipboardContent();
          cc.put(SERIALIZED_MIME_TYPE, index);
          db.setContent(cc);
          event.consume();
        }
      });

      row.setOnDragOver(event -> {
        Dragboard db = event.getDragboard();
        if (db.hasContent(SERIALIZED_MIME_TYPE)) {
          if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
          }
        }
      });

      row.setOnDragDropped(event -> {
        Dragboard db = event.getDragboard();
        if (db.hasContent(SERIALIZED_MIME_TYPE)) {
          int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
          ViewField draggedPerson = tvField.getItems().remove(draggedIndex);

          int dropIndex;

          if (row.isEmpty()) {
            dropIndex = tvField.getItems().size();
          }
          else {
            dropIndex = row.getIndex();
          }

          tvField.getItems().add(dropIndex, draggedPerson);

          event.setDropCompleted(true);
          tvField.getSelectionModel().select(dropIndex);
          draggedPerson.setDisplayOrder(dropIndex + 1);
          event.consume();
        }
      });

      return row;
    });
  }

  /** Generiert en Tabelleninhalt für die Fremdschlüsseltabelle.
   *
   * @param t die Tabelle */
  private void generateTableForForeignKeys(final ViewTable t) {
    tvForeignkey.setItems(t.getForeignKeysRaw());
    genForeignKeyTableRows(tvForeignkey);
  }

  /** Generiert den Inhalt der Index Tabelle.
   *
   * @param t Die Tabelle */
  private void generateTableForIndizies(final ViewTable t) {
    tvIndex.setItems(t.getIndizesRaw());
    tvIndex.getColumns().add(createTableRowNameIndex());
    // Type of Data
    final TableColumn<ViewIndex, Boolean> isUnique = new TableColumn<>(Strings.NAME_INDEX_ISUNIQUE);
    isUnique.setCellValueFactory(f -> f.getValue().uniqueProperty());
    isUnique.setCellFactory(tc -> new CheckBoxTableCell<>());
    tvIndex.getColumns().add(isUnique);
    // Fields
    tvIndex.getColumns().add(generateFieldListCell());
    formatTable(tvIndex);
  }

  /** Erstellt eine Liste mit allen foreignkeys die auf diese Tabell zeigen.
   *
   * @param t Table */
  private void generateTableForRefTables(final ViewTable t) {
    final ObservableList<ViewForeignKey> foreignKeys = FXCollections.observableArrayList();
    for (final ForeignKey i : t.getRefForeignKeys()) {
      foreignKeys.add(new ViewForeignKey(i, new ViewTable(i.getTable(), true)));
    }
    tvRefTable.setItems(foreignKeys);
    genForeignKeyTableRows(tvRefTable);
    tvRefTable.setEditable(false);
  }

  /** Spalten einer ForeignKey Table.
   *
   * @param table die Tabelle */
  private void genForeignKeyTableRows(final TableView<ViewForeignKey> table) {
    table.getColumns().add(createTableRowNameFk());
    // Table
    final TableColumn<ViewForeignKey, String> thisTable = new TableColumn<>(Strings.NAME_TABLE);
    thisTable.setCellValueFactory(f -> ((ViewTable) f.getValue().getTable()).nameProperty());
    thisTable.setCellFactory(TextFieldTableCell.forTableColumn());
    thisTable.setEditable(false);
    table.getColumns().add(thisTable);

    // Fields
    final TableColumn<ViewForeignKey, String> refTable = new TableColumn<>(Strings.NAME_REFTABLE);
    refTable.setCellValueFactory(f -> f.getValue().getVRefTable().nameProperty());
    refTable.setCellFactory(TextFieldTableCell.forTableColumn());
    refTable.setEditable(false);
    table.getColumns().add(refTable);

    // Fields
    table.getColumns().add(generateFieldListCellFk());
    formatTable(table);
  }

  public Button getAddFk() {
    return addForeignkey;
  }

  public Button getAddIndex() {
    return addIndex;
  }

  /** Getter deleteButton.
   *
   * @return den Löschen Button */
  public Button getDeleteButton() {
    return deleteTbl;
  }

  public Button getGraphButton() {
    return openGraph;
  }

  public Button getModFkRel() {
    return modifyForeignkey;
  }

  public Button getModIndexRel() {
    return modifyIndex;
  }

  /** Getter Category.
   *
   * @return Name der Kategorie */
  public String getNewCategory() {
    return selectCategory.getValue().getCategory();
  }

  public String getNewComment() {
    return tableComment.getText();
  }

  /** Getter Name.
   *
   * @return Name der Tabelle */
  public String getNewName() {
    return tableNameField.getText().trim();
  }

  /** Getter SaveButton.
   *
   * @return den Save Button */
  public Button getSaveButton() {
    return saveTbl;
  }

  public TableView<ViewForeignKey> getTvfk() {
    return tvForeignkey;
  }

  public TableView<ViewIndex> getTvi() {
    return tvIndex;
  }

  private void initComboBox(final ViewTable t) {
    selectCategory.setItems(categories);
    if (categories == null || categories.size() < 2) {
      selectCategory.setEditable(true);
    }
    selectCategory.getSelectionModel().select(new CategoryObj(t.getCategory(), 0));
    selectCategory.setConverter(new StringConverter<CategoryObj>() {
      @Override
      public CategoryObj fromString(final String string) {
        CategoryObj category = new CategoryObj(Strings.NAMEEMPTYCATEGORY, 0, string);
        for (final CategoryObj emp : selectCategory.getItems()) {
          if (emp.getCategoryText().equals(string)) {
            category = emp;
            break;
          }
        }
        return category;
      }

      @Override
      public String toString(final CategoryObj object) {
        if (object == null) {
          return null;
        }
        return object.getDisplayText();
      }
    });
    selectCategory.valueProperty().addListener((observable, oldValue, newValue) -> t.setModified());
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    tableContainer.setPrefHeight(1);
  }

  /** Erstellt ein neues Feld für die Feld Tabelle.
   *
   * @param t Table
   * @param tvf TableView for Field */
  private void newField(final ViewTable t, final TableView<ViewField> tvf) {
    final ViewField f = new ViewField(Strings.NAME_NEWFIELD, Domain.DomainId.STRING, 20, false, t, Strings.EMPTYSTRING);
    if (!t.getDataFieldsRaw().contains(f)) {
      focusNewElement(tvf, f, t);
      // Sollte ein bereits gelöschtes Feld hinzugefügt werden, dann muss es wieder aus der Löschliste entfernt werden
      // t.getDeleteRaw().remove(f);
    }
  }

  /** aktiviert den "Speichern" Button. */
  void save() {
    if (!saveTbl.isDisable()) {
      saveTbl.fire();
    }
  }

  public void setCategoryLookup(final ObservableList<CategoryObj> obsArrList) {
    categories = obsArrList;
  }
}
