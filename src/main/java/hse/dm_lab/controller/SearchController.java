package hse.dm_lab.controller;

import hse.dm_lab.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SearchController {

    @FXML
    private TextField nameTextField;

    @FXML
    private Button searchButton;

    @FXML
    void searchItems(ActionEvent event) {
        if (nameTextField.getText() != null && !nameTextField.getText().trim().isEmpty()) {
            MainApplication.setTempList(MainApplication.manipulator.selectItems(nameTextField.getText()));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Не указаны данные для поиска");
            alert.showAndWait();
        }

        System.out.println("Search completed");
        searchButton.getScene().getWindow().hide();
    }
}
