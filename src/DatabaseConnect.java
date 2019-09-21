import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnect {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lab";
    private static final String LOGIN = "Ana";
    private static final String PASSWORD = "123456";
    private Connection connection;

    DatabaseConnect (){
        boolean connectStatus = connect();
    }

    boolean connect() {
        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println("Driver was not found");
            return false;
        }
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
                System.out.println("Database is connected.");
                return true;
            } catch (SQLException e) {
                System.out.println("Connection failed");
                return false;
            }
        }
        return false;
    }

    public Connection getConnection() {
        return connection;
    }


}
