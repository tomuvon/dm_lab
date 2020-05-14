package hse.dm_lab.util;

import hse.dm_lab.model.Item;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBManipulator {
    private final String URl = "jdbc:postgresql://localhost:5347/lab5";
    private final String id = "postgres";
    private final String password = "603041";
    private Connection connection;

    public DBManipulator() {
        try {
            String JDBC_DRIVER = "org.postgresql.Driver";
            Class.forName(JDBC_DRIVER);
            Properties properties = new Properties();
            properties.setProperty("user", id);
            properties.setProperty("password", password);
            connection = DriverManager.getConnection(URl,properties);
            initProcedures();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Exception while connecting with MySQL");
            e.printStackTrace();
        }
    }

    public void createDB() {
        try {
            deleteDB();
            CallableStatement proc = connection.prepareCall("{ ? = call createDragsDB()() }");
            proc.execute();
            proc.close();

            connection = DriverManager.getConnection(URl,id,password);
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
            CallableStatement proc = connection.prepareCall("{ ? = call deleteDragsDB() }");
            proc.execute();
            proc.close();

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
            CallableStatement proc = connection.prepareCall("{ ? = call CleanDragsDB() }");
            proc.execute();
            proc.close();

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
            CallableStatement proc = connection.prepareCall("{ ? = call SelectDragsDB() }");
            proc.execute();
            ResultSet results = proc.getResultSet();

            while (results.next()) {
                Item item = ItemConverter.entityFromResultSet(results);
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
            CallableStatement statement = connection.prepareCall("{ call AddNewDrag(?, ?, ?) }");
            statement.setString(1, object.getName());
            statement.setBoolean(2, object.getRecipe().equals("yes"));
            statement.setInt(3, object.getPrice());
            statement.execute();
            statement.close();
        } catch (Exception e) {
            writingException(e);
        }
    }

    public void deleteItemProcedure(Integer itemId) {
        try {
            CallableStatement statement = connection.prepareCall("{ call DeleteDragByName(?) }");
            statement.setInt(1, itemId);
            statement.execute();
            statement.close();
            System.out.println("Запись была успешно удалена");
        } catch (SQLException e) {
            System.out.println("Could not clear database");
            System.out.println("Cause: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка");
            alert.setContentText("Не удалось удалить элемент из базы данных");
            alert.showAndWait();
        }
    }

    public void selectItems(Item filterItem) {

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

    private void initProcedures() {
        try {
            Statement statement = connection.createStatement();

            String createTableProcedure = "create or replace function createDragsDB()\n" +
                    "returns int as\n" +
                    "$$\n" +
                    "declare\n" +
                    "begin\n" +
                    "\n" +
                    "create table Drags (id int primary key check (id>=001),\n" +
                    "\t              name varchar(100) not null unique,\n" +
                    "\t              price int check (price>=001), \n" +
                    "\t              recipe boolean);\n" +
                    "return 1;\t\t\n" +
                    "end;\n" +
                    "$$language plpgsql";

            String dropTableProcedure = "create or replace function deleteDragsDB()\n" +
                    "returns int as\n" +
                    "$$\n" +
                    "declare\n" +
                    "begin\n" +
                    "\n" +
                    "drop table Drags;\n" +
                    "return 1;\t\t\n" +
                    "end;\n" +
                    "$$language plpgsql\n";

            String cleanTableProcedure = "create or replace function CleanDragsDB()\n" +
                    "returns int as\n" +
                    "$$\n" +
                    "begin\n" +
                    "\n" +
                    "delete from Drags where Drags.id is not null;\n" +
                    "\n" +
                    "return 1;\t\t\n" +
                    "end;\n" +
                    "$$language plpgsql\n";

            String showAllProcedure = "create function SelectDragsDB()\n" +
                    "returns table(id int, name text, price int, recipe text)\n" +
                    "as\n" +
                    "$$\n" +
                    "declare\n" +
                    "rec record;\n" +
                    "begin \n" +
                    "perform Drags.id, Drags.name, Drags.price, Drags.recipe from Drags where Drags.id is not null;\n" +
                    "for rec in select Drags.id, Drags.name, Drags.price, Drags.recipe from Drags where Drags.id is not null;\n" +
                    "group by Drags.id;\n" +
                    "end; \n" +
                    "$$ language plpgsql;";

            String insertNewItemProcedure = "create or replace function AddNewDrag(name varchar(100), price int, recipe boolean)\n" +
                    "returns int as\n" +
                    "$$\n" +
                    "declare\n" +
                    "n alias for $1;\n" +
                    "pr alias for $2;\n" +
                    "r alias for $3;\n" +
                    "d_id Drags.id%type;\n" +
                    "begin\n" +
                    "\n" +
                    "select max(id) into d_id from Drags;\n" +
                    "if(d_id is null) then\n" +
                    "d_id = 0;\n" +
                    "end if;\n" +
                    "\n" +
                    "insert into Drags values (d_id + 1, n, pr, r);\n" +
                    "\n" +
                    "return 1;\t\t\n" +
                    "end;\n" +
                    "$$language plpgsql";

            String deleteItemProcedure = "create or replace function DeleteDragByName(name varchar(100))\n" +
                    "returns int as\n" +
                    "$$\n" +
                    "declare\n" +
                    "n alias for $1;\n" +
                    "\n" +
                    "begin\n" +
                    "\n" +
                    "delete from Drags where Drags.name = n;\n" +
                    "\n" +
                    "if not found then\n" +
                    "raise exception 'Лекарства нет в базе';\n" +
                    "end if;\n" +
                    "\n" +
                    "return 1;\t\t\n" +
                    "end;\n" +
                    "$$language plpgsql";

            statement.executeUpdate(createTableProcedure);
            statement.executeUpdate(dropTableProcedure);
            statement.executeUpdate(showAllProcedure);
            statement.executeUpdate(cleanTableProcedure);
            statement.executeUpdate(insertNewItemProcedure);
            statement.executeUpdate(deleteItemProcedure);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}