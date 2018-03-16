package marketplace;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Sql {
    // static final String DB_URL = "jdbc:mysql://rwminj5u3kqjbo5l:nvf6a84y7s77sgo7@lg7j30weuqckmw07.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/y0izairs2fhusgoi";
    // static final String USERNAME = "rwminj5u3kqjbo5l";
    // static final String PASSWORD = "nvf6a84y7s77sgo7";

    private static Connection getConnection() throws URISyntaxException, SQLException {
        URI jdbUri = new URI(System.getenv("JAWSDB_URL"));

        String username = jdbUri.getUserInfo().split(":")[0];
        String password = jdbUri.getUserInfo().split(":")[1];
        String port = String.valueOf(jdbUri.getPort());
        String jdbUrl = "jdbc:mysql://" + jdbUri.getHost() + ":" + port + jdbUri.getPath();

        return DriverManager.getConnection(jdbUrl, username, password);
    }

    public List<Item> readItems(String keywords) {
        Connection connection = null;
        Statement statement = null;
        List<Item> items = new ArrayList<>();

        try {
            connection = getConnection();
            // connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
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
