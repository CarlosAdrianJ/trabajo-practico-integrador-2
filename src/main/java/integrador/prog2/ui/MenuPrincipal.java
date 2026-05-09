package integrador.prog2.ui;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;
    private final LectorConsola lector;

    private final CategoriaMenu categoriaMenu;
    private final ProductoMenu productoMenu;
    private final UsuarioMenu usuarioMenu;
    private final PedidoMenu pedidoMenu;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.lector = new LectorConsola(scanner);

        this.categoriaMenu = new CategoriaMenu(lector);
        this.productoMenu = new ProductoMenu(lector);
        this.usuarioMenu = new UsuarioMenu(lector);
        this.pedidoMenu = new PedidoMenu(lector);
    }

    public void iniciar() {
        int opcion;

        do {
            mostrarMenu();
            opcion = lector.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    categoriaMenu.mostrar();
                    break;
                case 2:
                    productoMenu.mostrar();
                    break;
                case 3:
                    usuarioMenu.mostrar();
                    break;
                case 4:
                    pedidoMenu.mostrar();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("=== SISTEMA DE PEDIDOS FOOD STORE ===");
        System.out.println("1. Categorías");
        System.out.println("2. Productos");
        System.out.println("3. Usuarios");
        System.out.println("4. Pedidos");
        System.out.println("0. Salir");
    }
}