package hse.dm_lab.util;

import com.google.gson.Gson;
import hse.dm_lab.model.Item;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class DBManipulator {
    private final Gson gson = new Gson();
    private final String URl = "JDBC:mysql://localhost:3306/data_management?serverTimezone=UTC&characterEncoding=utf8&";
    private final String id = "root";
    private final String password = "alfresco";
    private Connection connection;

    public DBManipulator() {
        try {
            String JDBC_DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(JDBC_DRIVER);
            Properties properties = new Properties();
            properties.setProperty("user", id);
            properties.setProperty("password", password);
            connection = DriverManager.getConnection(URl,properties);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Exception while connecting with MySQL");
            e.printStackTrace();
        }
    }

    private static final String PATH = "src/main/resources/db/database.txt";

    public void createDB() {
        try {
            deleteDB();
            Statement statement = connection.createStatement();
            String createTableCmd = "CREATE TABLE data_management.claims (" +
                    "    id INT(64) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "    fio VARCHAR(128)," +
                    "    sex BOOL DEFAULT FALSE," +
                    "    claim_count INT(128)," +
                    "    role VARCHAR(32)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

            connection = DriverManager.getConnection(URl,id,password);
            statement.executeUpdate(createTableCmd);
            System.out.println("Table created");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успех");
            alert.setHeaderText("Успех");
            alert.setContentText("Однотабличная база данных успешно создана");
            alert.showAndWait();
        } catch (SQLException e) {
            System.out.println("Could not create database");
            System.out.println("Cause: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Не удалось создать базу данных");
            alert.showAndWait();
        }
    }

    //удаление базы данных
    public void deleteDB() {
        try {
            Statement statement = connection.createStatement();
            String deleteCmd = "DROP TABLE IF EXISTS data_management.claims";
            statement.executeUpdate(deleteCmd);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успех");
            alert.setHeaderText("Успех");
            alert.setContentText("База данных успешно удалена");
            alert.showAndWait();
        } catch (SQLException e) {
            System.out.println("Could not delete database");
            System.out.println("Cause: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Не удалось удалить базу данных");
            alert.showAndWait();
        }
    }

    //очистка базы данных
    public void clearDB() {
        try {
            Statement statement = connection.createStatement();
            String query = "TRUNCATE TABLE data_management.claims";
            statement.executeUpdate(query);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успех");
            alert.setHeaderText("Успех");
            alert.setContentText("База данных успешно очищена");
            alert.showAndWait();
        } catch (SQLException e) {
            System.out.println("Could not clear database");
            System.out.println("Cause: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Не удалось очистить базу данных");
            alert.showAndWait();
        }
    }

    public List<Item> showAll() {
        try {
            List<Item> result = new ArrayList<>();
            String query = "SELECT * FROM data_management.claims";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                Item item = ItemConverter.entityFromResultSet(rs);
                result.add(item);
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Ошибка во время выполнения select'a");
            e.printStackTrace();
        }
        return null;
    }

    public void saveToDB(Item object) {
        try {
            String query = "INSERT INTO data_management.claims (fio, sex, claim_count, role) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, object.getFio());
            statement.setBoolean(2, object.getSex().equals("Мужской"));
            statement.setInt(3, object.getClaimCount());
            statement.setString(4, object.getRole());

            statement.execute();
        } catch (Exception e) {
            writingException(e);
        }
    }

    private void saveToDB(List<Item> items) {
        try {
            for (Item object : items) {
                saveToDB(object);
            }
        } catch (Exception e) {
            writingException(e);
        }
    }

    private Item getItem(Integer id) {
        List<Item> items = showAll();
        for (Item item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public void deleteItem(Integer itemId) {
        try {
            File file = new File(PATH);
            List<String> out = Files.lines(file.toPath())
                    .filter(line -> !(String.valueOf(itemId).equals(line.substring(0, line.indexOf("|")))))
                    .collect(Collectors.toList());
            Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Запись была успешно удалена");
        } catch (FileNotFoundException e) {
            fileNotFoundException(e);
        } catch (IOException e) {
            readingException(e);
        }
    }

    public void selectItems(Item filterItem) {
        List<Item> items = showAll();
        if (filterItem.getId() != null) {
            for (Item currentItem : new ArrayList<>(items)) {
                if (!currentItem.getId().equals(filterItem.getId())) {
                    items.remove(currentItem);
                }
            }
            saveToDB(items);
            return;
        }
        if (filterItem.getFio() != null) {
            for (Item currentItem : new ArrayList<>(items)) {
                if (!currentItem.getFio().equals(filterItem.getFio())) {
                    items.remove(currentItem);
                }
            }
        }
        if (filterItem.getClaimCount() != null) {
            for (Item currentItem : new ArrayList<>(items)) {
                if (!currentItem.getClaimCount().equals(filterItem.getClaimCount())) {
                    items.remove(currentItem);
                }
            }
        }
        if (filterItem.getSex() != null) {
            for (Item currentItem : new ArrayList<>(items)) {
                if (!currentItem.getSex().equals(filterItem.getSex())) {
                    items.remove(currentItem);
                }
            }
        }
        if (filterItem.getRole() != null) {
            for (Item currentItem : new ArrayList<>(items)) {
                if (!currentItem.getRole().equals(filterItem.getRole())) {
                    items.remove(currentItem);
                }
            }
        }
        saveToDB(items);
    }

    private void fileNotFoundException(Exception e) {
        System.out.println("Файл не найден");
        System.out.println("Cause: " + e.getMessage());
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка");
        alert.setContentText("Файл не найден");
        alert.showAndWait();
    }

    private void readingException(Exception e) {
        System.out.println("Ошибка при чтении из файла");
        System.out.println("Cause: " + e.getMessage());
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка");
        alert.setContentText("Ошибка удаления записи");
        alert.showAndWait();
    }

    private void writingException(Exception e) {
        System.out.println("Произошла ошибка при записи в базу данных");
        System.out.println("Cause: " + e.getMessage());
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка");
        alert.setContentText("Произошла ошибка при записи в базу данных");
        alert.showAndWait();
    }
}
