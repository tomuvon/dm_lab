package hse.dm_lab.util;

import hse.dm_lab.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemConverter {

    static List<Item> entityFromResultSet(ResultSet resultSet) {
        List<Item> res = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Item item = new Item();
                String[] object = resultSet.getString(1).substring(1, resultSet.getString(1).length() - 1).split(",");
                item.setId(Integer.parseInt(object[0]));
                item.setName(object[1].replace("\"", ""));
                item.setPrice(Integer.parseInt(object[2]));
                item.setRecipe(object[3].equals("t") ? "yes" : "no");
                res.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}