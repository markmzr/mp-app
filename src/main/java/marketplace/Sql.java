package marketplace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Sql {
    static final String DB_URL = System.getenv("DB_URL");
    static final String USERNAME = System.getenv("USERNAME");
    static final String PASSWORD = System.getenv("PASSWORD");

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
