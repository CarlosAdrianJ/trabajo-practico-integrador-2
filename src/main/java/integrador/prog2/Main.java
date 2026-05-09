package integrador.prog2;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.ui.MenuPrincipal;

import java.sql.Connection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MenuPrincipal menuPrincipal = new MenuPrincipal();
        menuPrincipal.iniciar();
        /*try (Connection conexion = ConexionDB.getConexion()) {
            System.out.println("Conexión exitosa a MySQL.");
            System.out.println("Base de datos conectada: basedatos_tpi2");
            System.out.println("Usuario conectado: usuario_tpi2");
        } catch (Exception e) {
            System.out.println("Error al probar la conexión.");
            System.out.println(e.getMessage());
        } */
    }
}