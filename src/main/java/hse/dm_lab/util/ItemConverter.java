package hse.dm_lab.util;

import hse.dm_lab.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemConverter {

    static Item entityFromResultSet(ResultSet resultSet) {
        try {
            Item item = new Item();
            item.setId(resultSet.getInt("id"));
            item.setName(resultSet.getString("name"));
            item.setPrice(resultSet.getInt("price"));
            item.setRecipe(resultSet.getBoolean("recipe") ? "yes" : "no");
            return item;
        } catch (SQLException e) {
            System.out.println("Ошибка во время извлечения сущности из бд");
            e.printStackTrace();
        }
        return null;
    }
}
