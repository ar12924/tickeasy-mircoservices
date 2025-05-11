package user.member.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JdbcUtil {
	public static DataSource getDatasource() {
		DataSource ds;
		try {
			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/tickeasy");
			return ds;
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
//	  private static final String URL = "jdbc:mysql://localhost:3306/tickeasy?serverTimezone=UTC";
//	  private static final String USER = "root";
//	  private static final String PASSWORD = "123456";
//
//	    static {
//	        try {
//	            Class.forName("com.mysql.cj.jdbc.Driver");
//	        } catch (ClassNotFoundException e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    public static Connection getConnection() {
//	        try {
//	            return DriverManager.getConnection(URL, USER, PASSWORD);
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	        return null;
//	    }
}
