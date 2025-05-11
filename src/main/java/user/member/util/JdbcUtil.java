package user.member.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcUtil {
	  private static final String URL = "jdbc:mysql://localhost:3306/tickeasy?serverTimezone=UTC";
	  private static final String USER = "root";
	  private static final String PASSWORD = "123456";

	    static {
	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	    }

	    public static Connection getConnection() {
	        try {
	            return DriverManager.getConnection(URL, USER, PASSWORD);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
}
