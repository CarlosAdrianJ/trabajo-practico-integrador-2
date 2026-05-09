package integrador.prog2.ui;

public class PedidoMenu {

    private final LectorConsola lector;

    public PedidoMenu(LectorConsola lector) {
        this.lector = lector;
    }

    public void mostrar() {
        int opcion;

        do {
            mostrarOpciones();
            opcion = lector.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    System.out.println("Listar pedidos - pendiente de implementar.");
                    lector.pausar();
                    break;
                case 2:
                    System.out.println("Ver detalle de pedido - pendiente de implementar.");
                    lector.pausar();
                    break;
                case 3:
                    System.out.println("Crear pedido - pendiente de implementar.");
                    lector.pausar();
                    break;
                case 4:
                    System.out.println("Actualizar estado del pedido - pendiente de implementar.");
                    lector.pausar();
                    break;
                case 5:
                    System.out.println("Actualizar forma de pago - pendiente de implementar.");
                    lector.pausar();
                    break;
                case 6:
                    System.out.println("Eliminar pedido - pendiente de implementar.");
                    lector.pausar();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarOpciones() {
        System.out.println();
        System.out.println("=== GESTIÓN DE PEDIDOS ===");
        System.out.println("1. Listar pedidos");
        System.out.println("2. Ver detalle de pedido");
        System.out.println("3. Crear pedido");
        System.out.println("4. Actualizar estado del pedido");
        System.out.println("5. Actualizar forma de pago");
        System.out.println("6. Eliminar pedido");
        System.out.println("0. Volver");
    }
}