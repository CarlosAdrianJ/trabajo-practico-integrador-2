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

    }
}