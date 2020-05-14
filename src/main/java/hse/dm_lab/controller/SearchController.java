package hse.dm_lab.controller;

import hse.dm_lab.MainApplication;
import hse.dm_lab.model.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SearchController {
    @FXML
    private TextField idTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private TextField recipeTextField;

    @FXML
    private Button searchButton;

    @FXML
    void searchItems(ActionEvent event) {
        Item filterItem = new Item();
        if (idTextField.getText() != null && !idTextField.getText().trim().isEmpty()) {
            Integer id = Integer.parseInt(idTextField.getText());
            filterItem.setId(id);
        }
        if (nameTextField.getText() != null && !nameTextField.getText().trim().isEmpty()) {
            filterItem.setName(nameTextField.getText());
        }
        String price = priceTextField.getText();
        String recipe = recipeTextField.getText();
        if (price != null && !price.trim().isEmpty()) {
            Integer prices = Integer.parseInt(price);
            filterItem.setPrice(prices);
        }
        if (recipe != null && !recipe.trim().isEmpty()) {
            if (recipe.equals("yes") || recipe.equals("no")) {
                filterItem.setRecipe(recipe);
            } else {
                System.out.println("Недопустимое значение пола");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка");
                alert.setContentText("Недопустимое значение пола");
                alert.showAndWait();
            }
        }

        MainApplication.manipulator.selectItems(filterItem);
        System.out.println("Search completed");
        searchButton.getScene().getWindow().hide();
    }
}
