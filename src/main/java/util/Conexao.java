package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static  String driver;
    private static String url;
    private static String user;
    private static String password;

//    public static Connection getConnectionPostgre() throws ClassNotFoundException, SQLException {
//        driver = "org.postgresql.Driver";
//        url = "jdbc:postgresql://localhost:5432/crud_v2";
//        user = "postgres";
//        password = "123Fatec";
//        return getConnection();
//
//    }

    public static Connection getConnectionMySQL() throws ClassNotFoundException, SQLException {
        driver = "com.mysql.cj.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/crud_v2?useSSL=false";
        user = "root";
        password = "root";
        return getConnection();
    }

//    public static Connection getConnectionH2() throws ClassNotFoundException, SQLException {
//        driver = "org.h2.Driver";
//        url = "jdbc:h2:~/test";
//        user = "sa";
//        password = "";
//        return getConnection();
//    }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }
}
