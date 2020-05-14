package hse.dm_lab.controller;

import hse.dm_lab.MainApplication;
import hse.dm_lab.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;

import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class MainViewController {

    @FXML
    private TableColumn<Item, Number> priceColumn;

    @FXML
    private TableView<Item> tableColumn;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<Item, String> nameColumn;

    @FXML
    private Button clearTableButton;

    @FXML
    private TableColumn<Item, String> idColumn;

    @FXML
    private Button insertButton;

    @FXML
    private Button searchButton;

    @FXML
    private TableColumn<Item, String> recipeColumn;

    @FXML
    void delete(ActionEvent event) {
        Item item = tableColumn.getSelectionModel().getSelectedItem();
        if (item == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Не выбран элемент для удаления");
            alert.showAndWait();
        } else {
            MainApplication.manipulator.deleteItemProcedure(item.getId());
            refreshTable();
        }
    }

    @FXML
    public void editNameColumn(TableColumn.CellEditEvent editedCell) {
        Item selectedItem = tableColumn.getSelectionModel().getSelectedItem();
        selectedItem.setName(editedCell.getNewValue().toString());
        refreshTable();
    }

    @FXML
    public void editRecipeColumn(TableColumn.CellEditEvent editedCell) {
        String newRecipe = editedCell.getNewValue().toString();
        if (newRecipe.equals("Yes") || newRecipe.equals("No")) {
            Item selectedItem = tableColumn.getSelectionModel().getSelectedItem();
            selectedItem.setRecipe(newRecipe);
            refreshTable();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Недопустимое значение");
            alert.showAndWait();
        }
    }

    @FXML
    public void editPriceColumn(TableColumn.CellEditEvent editedCell) {
        Integer newPrice = Integer.parseInt(editedCell.getNewValue().toString());
        Item selectedItem = tableColumn.getSelectionModel().getSelectedItem();
        selectedItem.setPrice(newPrice);
        refreshTable();
    }

    @FXML
    public void clearTable(ActionEvent event) {
        MainApplication.manipulator.clearDB();
        refreshTable();
    }

    @FXML
    void insert(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Pane pane = FXMLLoader.load(MainViewController.class.getClassLoader().getResource("fxml/InsertView.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Vatletsova File Database");
        stage.showAndWait();
        refreshTable();
    }

    @FXML
    void search(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Pane pane = FXMLLoader.load(MainViewController.class.getClassLoader().getResource("fxml/SearchView.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Vatletsova File Database");
        stage.showAndWait();
        refreshTable();
    }

    @FXML
    void initialize() {
        assert priceColumn != null : "fx:id=\"priceColumn\" was not injected: check your FXML file 'MainView.fxml'.";
        assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'MainView.fxml'.";
        assert nameColumn != null : "fx:id=\"nameColumn\" was not injected: check your FXML file 'MainView.fxml'.";
        assert idColumn != null : "fx:id=\"idColumn\" was not injected: check your FXML file 'MainView.fxml'.";
        assert insertButton != null : "fx:id=\"insertButton\" was not injected: check your FXML file 'MainView.fxml'.";
        assert searchButton != null : "fx:id=\"searchButton\" was not injected: check your FXML file 'MainView.fxml'.";
        assert recipeColumn != null : "fx:id=\"recipeColumn\" was not injected: check your FXML file 'MainView.fxml'.";
        ObservableList<Item> items = FXCollections.observableArrayList(MainApplication.manipulator.showAll());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        recipeColumn.setCellValueFactory(new PropertyValueFactory<>("recipe"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        tableColumn.setItems(items);

        tableColumn.setEditable(true);
        nameColumn.setCellFactory(forTableColumn());
        recipeColumn.setCellFactory(forTableColumn());
        priceColumn.setCellFactory(forTableColumn(new NumberStringConverter()));
    }

    private void refreshTable() {
        ObservableList<Item> items = FXCollections.observableArrayList(MainApplication.manipulator.showAll());
        tableColumn.setItems(items);
    }
}
