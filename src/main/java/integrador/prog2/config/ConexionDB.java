package integrador.prog2.config;

import integrador.prog2.exception.BusinessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/basedatos_tpi2";
    private static final String USUARIO = "usuario_tpi2";
    private static final String PASSWORD = "Tpi2_2026!";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private ConexionDB() {

    }

    public static Connection getConexion() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new BusinessException("No se encontró el driver JDBC de MySQL.", e);
        } catch (SQLException e) {
            throw new BusinessException("No se pudo conectar a la base de datos.", e);
        }
    }
}