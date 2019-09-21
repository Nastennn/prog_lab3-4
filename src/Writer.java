import story.Character;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.*;

public class Writer {
    private Connection connection;
    Reader reader;

    Writer(Connection connection) {
        this.connection = connection;
        reader = new Reader(connection);
    }


    String writeUser(String email, String password) {
        String login = email.split("@")[0];
        //language=PostgreSQL
        String check = "SELECT mail FROM users";
        try {
            PreparedStatement select = connection.prepareStatement(check);
            //select.setString(1, email);
            ResultSet resultSet = select.executeQuery();
            while (resultSet.next()) {
                if (!resultSet.getString("mail").equals(email)) {
                    //language=PostgreSQL
                    String insert = "INSERT INTO users (mail, login, password, hash) VALUES (?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(insert);
                    statement.setString(1, email);
                    statement.setString(2, login);
                    statement.setString(3, password);
                    statement.setString(4, authHash(login, password));
                    statement.executeUpdate();
                    return "Регистрация прошла успешно.";
                } else {
                    return "Пользователь с такой почтой уже существует.";
                }
            }
        } catch (SQLException e) {
            return "Ошибка при работе с базой данных.";
        }
        return "";
    }

    boolean loginUser(String email, String password) {
        //language=PostgreSQL
        String check = "SELECT mail, password FROM users";
        try {
            PreparedStatement statement = connection.prepareStatement(check);
            //statement.setString(1, email);
            //statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if ((resultSet.getString("mail").equals(email))) {
                    if (resultSet.getString("password").equals(password)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка в в работе с базой данных.");
            return false;
        }
        return false;
    }

    String authHash(String login, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String authHash = login + password;
            BigInteger number = new BigInteger(1, md.digest(authHash.getBytes(StandardCharsets.UTF_8)));
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "Ошибка при хешировании.";
        }
    }

    String addCharacter(Character character, String userHash) {
        String name = character.getName();
        int height = character.getHeight();
        double x = character.getX();
        double y = character.getY();
        int userId = reader.getUserID(userHash);
        ZonedDateTime date = character.getDateOfBirth();
        OffsetDateTime dateForDB = date.withZoneSameInstant(ZoneId.of("UTC+3")).toOffsetDateTime();
        String query = "INSERT INTO characters (user_id, name, height, x, y, date) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, name);
            statement.setInt(3, height);
            statement.setDouble(4, x);
            statement.setDouble(5, y);
            statement.setTimestamp(6, new Timestamp((dateForDB.toLocalDateTime().toEpochSecond(ZoneOffset.UTC)*1000L)));
            statement.executeUpdate();
            return "Персонаж добавлен в базу данных.";
        } catch (SQLException e) {
            return "Ошибка при записи в базу данных.";
        }
    }


}
