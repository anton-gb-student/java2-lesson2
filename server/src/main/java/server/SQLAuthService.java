package server;

import java.io.PrintWriter;
import java.sql.*;

public class SQLAuthService implements AuthService {

    Connection sqlConnection = null;
    PrintWriter logwriter = null;

    public SQLAuthService() {
        try {
            sqlConnection = DriverManager.getConnection("jdbc:sqlite:users.db");
            System.out.println("база подключена");    // Здесь подключаемся к базе, но вот где отключаться, я не понимаю.
            DriverManager.setLogWriter(logwriter);    // Наверное, надо менять точку подключения, переносить в класс Server или ClientHandler
        } catch (SQLException e) {                    // и там же и отключать потом
            e.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) { // или подключаться в начале метода и отключаться в конце?
        try {                                                                    // Но тогда теряется последний return (оказывается после finally)
            PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            return result.getString("nickname");

        } catch (SQLException e) {
            System.out.println(DriverManager.getLogWriter());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try {
            PreparedStatement preparedStatement = sqlConnection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?,?,?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, nickname);
            if (preparedStatement.executeUpdate() == 1)
                return true;
        } catch (SQLException e) {
            System.out.println(DriverManager.getLogWriter());
            e.printStackTrace();

        }
        return false;
    }
}
