package hse.dm_lab.controller;

import hse.dm_lab.MainApplication;
import hse.dm_lab.model.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class InsertController {

    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private RadioButton yesButton;

    @FXML
    private RadioButton noButton;

    @FXML
    private Button submitButton;

    @FXML
    void submitInsert(ActionEvent event) {
        Item item = new Item();

        if (nameTextField.getText() == null || nameTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Не заполнено поле ФИО");
            alert.showAndWait();
        } else {
            if (nameTextField.getText().length() < 2) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка");
                alert.setContentText("Слишком короткое название");
                alert.showAndWait();
            }
        }
        item.setName(nameTextField.getText());


        if (priceTextField.getText() == null || priceTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Внимание");
            alert.setHeaderText("Внимание");
            alert.setContentText("Вы не указали количество заявок, будет присвоен 0");
            alert.showAndWait();
            item.setPrice(0);
        } else {
            try {
                int price = Integer.parseInt(priceTextField.getText());
                item.setPrice(price);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка");
                alert.setContentText("Вы ввели не числовое значение количества заявок");
                alert.showAndWait();
            }
        }

        String recipe = ((ToggleButton)toggleGroup.getSelectedToggle()).getText();
        if (recipe != null) {
            item.setRecipe(recipe);
        }

        MainApplication.manipulator.saveToDB(item);
        submitButton.getScene().getWindow().hide();
    }

    @FXML
    void initialize() {
        yesButton.setToggleGroup(toggleGroup);
        yesButton.setSelected(true);
        noButton.setToggleGroup(toggleGroup);
    }
}
