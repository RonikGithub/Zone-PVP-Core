package ronik.ffacore;
import java.sql.*;

public class DatabaseHandler {

    private static final String jdbcUrl = "jdbc:mysql://DATABASE URL";
    private static final String username = "#ENTER DATABSE USERNAME";
    private static final String password = "#ENTER DATABASE PASSOWRD";

    public static int[] getStats(String uuid) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            String query = "SELECT * FROM customer_551332_serverbase.playerStats WHERE uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();

            int[] stats = new int[4];
            if (resultSet.next()) {
                for (int i = 0; i < 4; i++) {
                    stats[i] = resultSet.getInt(i + 2);
                }
            } else {
                stats = null;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
            return stats;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addKill(String uuid) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Prepare the query with the indexed UUID condition
            String query = "UPDATE customer_551332_serverbase.playerStats SET kills = kills + 1 WHERE uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addDeath(String uuid) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Prepare the query with the indexed UUID condition
            String query = "UPDATE customer_551332_serverbase.playerStats SET deaths = deaths + 1 WHERE uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPlayerInDatabase(String uuid) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Prepare the query with the indexed UUID condition
            String query = "SELECT * FROM customer_551332_serverbase.playerStats WHERE uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isInDatabase = resultSet.next();

            resultSet.close();
            preparedStatement.close();
            connection.close();
            return isInDatabase;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addPlayerToDatabase(String uuid) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Prepare the query with the indexed UUID condition
            String query = "INSERT INTO customer_551332_serverbase.playerStats (uuid, credits, refill_credits, kills, deaths) VALUES (?, 0, 0, 0, 0)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveZoneInfo(String name, String teamThatCaptured, int health, String status, int captureScore) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Prepare the query with the indexed UUID condition
            String query = "INSERT INTO customer_551332_serverbase.zoneInfo (name, teamThatCaptured, health, status, captureScore) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE teamThatCaptured = VALUES(teamThatCaptured), health = VALUES(health), status = VALUES(status), captureScore = VALUES(captureScore)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, teamThatCaptured);
            preparedStatement.setInt(3, health);
            preparedStatement.setString(4, status);
            preparedStatement.setInt(5, captureScore);


            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String[] getZoneInfo(String name) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Prepare the query with the indexed UUID condition
            String query = "SELECT * FROM customer_551332_serverbase.zoneInfo WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            String[] zoneInfo = new String[4];
            if (resultSet.next()) {
                for (int i = 1; i < 5; i++) {
                    zoneInfo[i-1] = resultSet.getString(i + 1);
                }
            } else {
                zoneInfo = null;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
            return zoneInfo;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}