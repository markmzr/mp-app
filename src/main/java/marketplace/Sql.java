package marketplace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Sql {
    static final String DB_URL = System.getenv("DB_URL");
    static final String USERNAME = System.getenv("USERNAME");
    static final String PASSWORD = System.getenv("PASSWORD");

    public void createUser(User user) {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            ApplicationContext context = new AnnotationConfigApplicationContext(MvcConfig.class);
            BCryptPasswordEncoder passwordEncoder = (BCryptPasswordEncoder)context.getBean("passwordEncoder");

            String sql = "INSERT INTO Users (Username, Password, Enabled) VALUES ('" +
                    user.getUsername() + "', '" + passwordEncoder.encode(user.getPassword()) + "', '1')";
            statement.executeUpdate(sql);

            sql = "INSERT INTO Roles (Username, Role) VALUES ('" + user.getUsername() + "', 'User')";
            statement.executeUpdate(sql);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean userExists(User user) {
        Boolean usernameExists = true;
        Connection connection = null;
        Statement statement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            String sql = "SELECT * FROM Users WHERE Username='" + user.getUsername() + "'";
            ResultSet results = statement.executeQuery(sql);

            if (results.next()) {
                usernameExists = true;
            }
            else {
                usernameExists = false;
            }

            results.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return usernameExists;
    }

    public List<Item> readItems(String keywords) {
        Connection connection = null;
        Statement statement = null;
        List<Item> items = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            String sql = "SELECT * FROM Items WHERE Name LIKE '%" + keywords + "%'";
            ResultSet results = statement.executeQuery(sql);

            while (results.next()) {
                String name  = results.getString("Name");
                String description = results.getString("Description");
                Item item = new Item(name, description);
                items.add(item);
            }

            results.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return items;
    }
}
