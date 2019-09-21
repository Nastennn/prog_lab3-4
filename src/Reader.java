import story.Character;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Vector;

public class Reader {
    private Connection connection;

    Reader(Connection connection) {
        this.connection = connection;
    }

    boolean checkUser(String userHash){
        //language=PostgreSQL
        String check = "SELECT hash FROM users";
        try {
            PreparedStatement select = connection.prepareStatement(check);
            ResultSet resultSet = select.executeQuery();
            while (resultSet.next()){
                if (resultSet.getString("hash").equals(userHash)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при работе с базой данных.");
            return false;
        }
    }

    String deleteCharacter(Character character, String userHash) {
        String name = character.getName();
        int height = character.getHeight();
        double x = character.getX();
        double y = character.getY();
        int userId = getUserID(userHash);
        //language=PostgreSQL
        String query = "SELECT count(user_id) as count FROM characters WHERE name = ? AND height =? AND x = ? AND y = ? AND user_id = ?";
        try {
        PreparedStatement check = connection.prepareStatement(query);
            check.setString(1, name);
            check.setInt(2, height);
            check.setDouble(3, x);
            check.setDouble(4, y);
            check.setInt(5, userId);
            ResultSet rs = check.executeQuery();
            rs.next();
            int id = rs.getInt("count");
            if (id == 0 ){
                return "Вы можете удалить только своего персонажа.";
            }else {
                String delete = "DELETE FROM characters WHERE name = ? AND height = ? AND x = ? " +
                        "AND y = ? AND user_id = ?";
                PreparedStatement statement = connection.prepareStatement(delete);
                statement.setString(1, name);
                statement.setInt(2, height);
                statement.setDouble(3, x);
                statement.setDouble(4, y);
                statement.setInt(5, userId);
                statement.executeUpdate();
                return "Персонаж удален.";
            }
        } catch (SQLException e) {
            return "Ошибка при чтении из базы данных.";
        }
    }

    int getUserID(String userHash) {
        //language=PostgreSQL
        String query = "SELECT id FROM users WHERE hash = ?";
        try {
            PreparedStatement select = connection.prepareStatement(query);
            select.setString(1, userHash);
            ResultSet resultSet = select.executeQuery();
            resultSet.next();
            String id = resultSet.getString("id");
            return Integer.parseInt(id);
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных.");
            return -1;
        }
    }

    boolean checkIfCharacterExists(Character character){
        String name = character.getName();
        int height = character.getHeight();
        double x = character.getX();
        double y = character.getY();
        //language=PostgreSQL
        String query = "SELECT * FROM characters WHERE name = ? AND height = ? " +
                "AND x = ? AND y = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setInt(2, height);
            statement.setDouble(3, x);
            statement.setDouble(4, y);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    String deleteFirst(String userHash){
        int userID = getUserID(userHash);
        //language=PostgreSQL
        String query = "DELETE FROM characters WHERE user_id = ? AND id = (SELECT MIN(id) FROM characters)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
           statement.setInt(1, userID);
           statement.executeUpdate();
           return "Персонаж удален.";
        } catch (SQLException e) {
            return "Ошибка при удалении персонажа из базы данных.";
        }
    }

    String show(){
        String query = "SELECT users.login, name, height, x, y, date FROM characters LEFT JOIN users ON characters.user_id= users.id";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            StringBuilder stringBuilder = new StringBuilder();
            while (resultSet.next()){
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                ZonedDateTime zonedDate = date.atZone(ZoneId.of("UTC+3"));
                stringBuilder.append("\n\n [Пользователь: " +resultSet.getString("login") + "\n Имя персонажа: " + resultSet.getString("name")
                       + "\n Рост: " + resultSet.getInt("height") + "\n Координаты: " + resultSet.getDouble("x")
                       + ", " + resultSet.getDouble("y") + "\n Дата создания: " + zonedDate + " ]");
            }
            return new String(stringBuilder);
        } catch (SQLException e) {
            return "Ошибка при чтении из базы данных.";
        }
    }

    boolean checkIfTableIsEmpty(){
        String query = "SELECT count(id) as count FROM characters";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int i = resultSet.getInt("count");
            if (i==0){
                return true;
            } else{
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных.");
            return false;
        }
    }


    void sort(){
        String query = "with _characters as  (\n" +
                "         select id, row_number()  over (order by name) as row_n\n" +
                "         from characters\n" +
                "     )\n" +
                "update characters\n" +
                "set sort = _characters.row_n\n" +
                "from _characters\n" +
                "where _characters.id = characters.id;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при чтении из базы данных.");
        }
    }



}
