package com.game.radaykin_vlad_201_hw5;

import java.sql.*;

public class DBWorker {
    public void addInfo(String login, int spent_game_time, int score){
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        String dbName = "lastGameResults";
        String connectionURL = "jdbc:derby:" + dbName + ";create=true";

        Connection conn = null;
        Statement s;
        PreparedStatement psInsert;
        String createString = "CREATE TABLE LAST_GAMES ("
                + "ID INT GENERATED ALWAYS AS IDENTITY, "
                + " LOGIN VARCHAR(32) NOT NULL, "
                + " ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + " SCORE INT NOT NULL, "
                + " SPENT_GAME_TIME INT NOT NULL) ";

        try {
            System.out.println("Connecting to database " + dbName);
            conn = DriverManager.getConnection(connectionURL);
            System.out.println("Connected to database " + dbName);

            // Create a statement to issue simple commands.
            s = conn.createStatement();
            // Call utility method to check if table exists.
            //      Create the table if needed
            if (!DBUtils.wwdChk4Table(conn)) {
                System.out.println(" . . . . creating table");
                s.execute(createString);
            }
            //  Prepare the insert statement to use
            psInsert = conn.prepareStatement("insert into LAST_GAMES(LOGIN, SCORE, SPENT_GAME_TIME) values (?,?,?)");

            psInsert.setString(1, login);
            psInsert.setInt(2, score);
            psInsert.setInt(3, spent_game_time);
            psInsert.executeUpdate();

            psInsert.close();
            s.close();
            conn.close();
            System.out.println("Closed connection");

            if (driver.equals("org.apache.derby.jdbc.EmbeddedDriver")) {
                boolean gotSQLExc = false;
                try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (SQLException se) {
                    if (se.getSQLState().equals("XJ015")) {
                        gotSQLExc = true;
                    }
                }
                if (!gotSQLExc) {
                    System.out.println("Database did not shut down normally");
                } else {
                    System.out.println("Database shut down normally");
                }
            }

        } catch (Throwable e) {
            System.out.println(" . . . exception thrown:");
            e.printStackTrace(System.out);
        }

    }

    public void getInfo(){

        String driver = "org.apache.derby.client.jdbc.EmbeddedDriver";
        String dbName = "lastGameResults";
        String connectionURL = "jdbc:derby:" + dbName + ";create=true";

        Connection conn = null;
        Statement s;
        String createString = "CREATE TABLE LAST_GAMES ("
                + "ID INT GENERATED ALWAYS AS IDENTITY, "
                + " LOGIN VARCHAR(32) NOT NULL, "
                + " ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + " SCORE INT NOT NULL, "
                + " SPENT_GAME_TIME INT NOT NULL) ";

        try {
            System.out.println("Connecting to database " + dbName);
            conn = DriverManager.getConnection(connectionURL);
            System.out.println("Connected to database " + dbName);

            // Create a statement to issue simple commands.
            s = conn.createStatement();
            // Call utility method to check if table exists.
            //      Create the table if needed
            if (!DBUtils.wwdChk4Table(conn)) {
                System.out.println(" . . . . creating table");
                s.execute(createString);
            }

            String query = "SELECT ID, LOGIN, ENTRY_DATE, SCORE, SPENT_GAME_TIME FROM LAST_GAMES";
            ResultSet rs = s.executeQuery(query);
            while(rs.next()) {
                System.out.println("Id: "+rs.getString("Id"));
                System.out.println("Name: "+rs.getString("Name"));
                System.out.println("Salary: "+rs.getString("Salary"));
                System.out.println(" ");
            }

            s.close();
            conn.close();
            System.out.println("Closed connection");

            if (driver.equals("org.apache.derby.jdbc.EmbeddedDriver")) {
                boolean gotSQLExc = false;
                try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (SQLException se) {
                    if (se.getSQLState().equals("XJ015")) {
                        gotSQLExc = true;
                    }
                }
                if (!gotSQLExc) {
                    System.out.println("Database did not shut down normally");
                } else {
                    System.out.println("Database shut down normally");
                }
            }

        } catch (Throwable e) {
            System.out.println(" . . . exception thrown:");
            e.printStackTrace(System.out);
        }
    }
}
