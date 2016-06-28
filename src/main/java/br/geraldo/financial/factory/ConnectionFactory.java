package br.geraldo.financial.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	private final static String URL = "jdbc:h2:~/pltask;multi_threaded=true";
	private final static String user = "SA";
	private final static String passwd = "";
	private static Connection con;
	
	static{
		try {
			con = DriverManager.getConnection(URL,user,passwd);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static Connection getConnection() throws SQLException{
		return con;
	}

}
