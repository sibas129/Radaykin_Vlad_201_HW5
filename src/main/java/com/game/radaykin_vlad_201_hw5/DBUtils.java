package com.game.radaykin_vlad_201_hw5;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {

    /*
    * Метод для пороверки соединения с таблицей
    * */
    public static boolean wwdChk4Table(Connection conTst) throws SQLException {
        boolean chk = true;
        boolean doCreate = false;
        try {
            Statement s = conTst.createStatement();
            s.execute("update LAST_GAMES set ENTRY_DATE = CURRENT_TIMESTAMP, LOGIN = 'TEST ENTRY', SCORE = 2, SPENT_GAME_TIME = 10 where 1=3");
        } catch (SQLException sqle) {
            String theError = (sqle).getSQLState();
            //   System.out.println("  Utils GOT:  " + theError);
            /** If table exists will get -  WARNING 02000: No row was found **/
            if (theError.equals("42X05"))   // Table does not exist
            {
                return false;
            } else if (theError.equals("42X14") || theError.equals("42821")) {
                System.out.println("WwdChk4Table: Incorrect table definition. Drop table WISH_LIST and rerun this program");
                throw sqle;
            } else {
                System.out.println("WwdChk4Table: Unhandled SQLException");
                throw sqle;
            }
        }
        return true;
    }

//    public static void main(String[] args) {
//        // This method allows stand-alone testing of the getWishItem method
//        String answer;
//        do {
//            answer = getWishItem();
//            if (!answer.equals("exit")) {
//                System.out.println("You said: " + answer);
//            }
//        } while (!answer.equals("exit"));
//    }

}
