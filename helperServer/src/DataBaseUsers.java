import java.sql.*;

public class DataBaseUsers {
    private String url = "";
    private String user = "";
    private String password = "";

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    DataBaseUsers(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * @return returns a database response or error
     */
    public String insertUser(String username, String user_password, int id_role){
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT name FROM test.users WHERE name='" + username + "'");
            if(resultSet.next())
                return "Username is already taken";
            else
                statement.executeUpdate("INSERT INTO test.users (name, password, id_role) \n" +
                        " VALUES ('" + username + "', '" + user_password + "', " + id_role + ");");

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return sqlEx.toString();
        } finally {
            try { connection.close(); } catch(SQLException se) { return se.toString(); }
            try { statement.close(); } catch(SQLException se) { return se.toString(); }
            try { resultSet .close(); } catch(SQLException se) { return se.toString(); }
        }
        return "User successfully inserted";
    }

    public boolean authentication(String username, String user_password){
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT password FROM test.users WHERE name='" + username + "'");
            if(resultSet.next()) {
                if (user_password.equals(resultSet.getString(1)))
                    return true;
            }
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return false;
        } finally {
            try { connection.close(); } catch(SQLException se) { return false; }
            try { statement.close(); } catch(SQLException se) { return false; }
            try { resultSet .close(); } catch(SQLException se) { return false; }
        }
        return false;
    }
}
