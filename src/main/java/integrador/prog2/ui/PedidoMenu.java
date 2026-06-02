package integrador.prog2.ui;

import integrador.prog2.entities.DetallePedido;
import integrador.prog2.entities.Pedido;
import integrador.prog2.entities.Producto;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;
import integrador.prog2.service.PedidoService;
import integrador.prog2.service.ProductoService;
import integrador.prog2.service.UsuarioService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PedidoMenu {

    private final LectorConsola lector;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public PedidoMenu(LectorConsola lector) {
        this.lector = lector;
        this.pedidoService = new PedidoService();
        this.usuarioService = new UsuarioService();
        this.productoService = new ProductoService();
    }

    public void mostrar() {
        int opcion;

        do {
            mostrarOpciones();
            opcion = lector.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    ManejadorErroresConsola.ejecutar(this::listarPedidos);
                    lector.pausar();
                    break;
                case 2:
                    ManejadorErroresConsola.ejecutar(this::verDetallePedido);
                    lector.pausar();
                    break;
                case 3:
                    ManejadorErroresConsola.ejecutar(this::crearPedido);
                    lector.pausar();
                    break;
                case 4:
                    ManejadorErroresConsola.ejecutar(this::actualizarEstadoPedido);
                    lector.pausar();
                    break;
                case 5:
                    ManejadorErroresConsola.ejecutar(this::actualizarFormaPago);
                    lector.pausar();
                    break;
                case 6:
                    ManejadorErroresConsola.ejecutar(this::eliminarPedido);
                    lector.pausar();
                    break;
                case 7:
                    ManejadorErroresConsola.ejecutar(this::listarPedidosPorUsuario);
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
        System.out.println("7. Listar pedidos por usuario");
        System.out.println("0. Volver");
    }

    private void listarPedidos() {
        List<Pedido> pedidos = pedidoService.listar();

        System.out.println();
        System.out.println("=== LISTADO DE PEDIDOS ===");

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos cargados.");
            return;
        }

        for (Pedido pedido : pedidos) {
            System.out.println("--------------------------------");
            mostrarPedidoResumen(pedido);
        }

        System.out.println("--------------------------------");
    }

    private void verDetallePedido() {
        System.out.println();
        System.out.println("=== DETALLE DE PEDIDO ===");

        listarPedidos();

        Long id = lector.leerLong("Ingrese el id del pedido: ");

        Pedido pedido = pedidoService.buscarPorIdConDetalles(id);

        System.out.println();
        mostrarPedidoResumen(pedido);

        System.out.println();
        System.out.println("Detalles:");

        if (pedido.getDetalles().isEmpty()) {
            System.out.println("El pedido no tiene detalles cargados.");
            return;
        }

        for (DetallePedido detalle : pedido.getDetalles()) {
            System.out.println("--------------------------------");
            System.out.println("Producto: " + detalle.getProducto().getNombre());
            System.out.println("Cantidad: " + detalle.getCantidad());
            System.out.println("Subtotal: " + detalle.getSubtotal());
        }

        System.out.println("--------------------------------");
    }

    private void crearPedido() {
        System.out.println();
        System.out.println("=== CREAR PEDIDO ===");

        listarUsuariosDisponibles();

        Long usuarioId = lector.leerLong("Ingrese el id del usuario: ");
        FormaPago formaPago = leerFormaPago();

        Map<Long, Integer> productosCantidades = new LinkedHashMap<>();

        boolean agregarMas;

        do {
            listarProductosDisponibles();

            Long productoId = lector.leerLong("Ingrese el id del producto: ");
            int cantidad = lector.leerEntero("Ingrese la cantidad: ");

            productosCantidades.merge(productoId, cantidad, Integer::sum);

            agregarMas = lector.confirmar("¿Desea agregar otro producto al pedido?");

        } while (agregarMas);

        Pedido pedidoCreado = pedidoService.crear(usuarioId, formaPago, productosCantidades);

        System.out.println();
        System.out.println("Pedido creado correctamente.");
        System.out.println("ID generado: " + pedidoCreado.getId());
        System.out.println("Total: " + pedidoCreado.getTotal());
    }

    private void actualizarEstadoPedido() {
        System.out.println();
        System.out.println("=== ACTUALIZAR ESTADO DEL PEDIDO ===");

        listarPedidos();

        Long pedidoId = lector.leerLong("Ingrese el id del pedido: ");
        Estado estado = leerEstado();

        pedidoService.actualizarEstado(pedidoId, estado);

        System.out.println("Estado actualizado correctamente.");
    }

    private void actualizarFormaPago() {
        System.out.println();
        System.out.println("=== ACTUALIZAR FORMA DE PAGO ===");

        listarPedidos();

        Long pedidoId = lector.leerLong("Ingrese el id del pedido: ");
        FormaPago formaPago = leerFormaPago();

        pedidoService.actualizarFormaPago(pedidoId, formaPago);

        System.out.println("Forma de pago actualizada correctamente.");
    }

    private void eliminarPedido() {
        System.out.println();
        System.out.println("=== ELIMINAR PEDIDO ===");

        listarPedidos();

        Long pedidoId = lector.leerLong("Ingrese el id del pedido a eliminar: ");

        Pedido pedido = pedidoService.buscarPorId(pedidoId);

        System.out.println();
        System.out.println("Pedido seleccionado:");
        mostrarPedidoResumen(pedido);

        boolean confirma = lector.confirmar("¿Confirma la eliminación lógica del pedido?");

        if (!confirma) {
            System.out.println("Operación cancelada.");
            return;
        }

        pedidoService.eliminar(pedidoId);

        System.out.println("Pedido eliminado correctamente.");
    }

    private void listarUsuariosDisponibles() {
        List<Usuario> usuarios = usuarioService.listar();

        System.out.println();
        System.out.println("=== USUARIOS DISPONIBLES ===");

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios cargados.");
            return;
        }

        for (Usuario usuario : usuarios) {
            System.out.println("ID: " + usuario.getId()
                    + " | " + usuario.getNombre()
                    + " " + usuario.getApellido()
                    + " | Mail: " + usuario.getMail());
        }
    }

    private void listarProductosDisponibles() {
        List<Producto> productos = productoService.listar();

        System.out.println();
        System.out.println("=== PRODUCTOS DISPONIBLES ===");

        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }

        for (Producto producto : productos) {
            System.out.println("ID: " + producto.getId()
                    + " | " + producto.getNombre()
                    + " | Precio: " + producto.getPrecio()
                    + " | Stock: " + producto.getStock()
                    + " | Disponible: " + (producto.getDisponible() ? "Sí" : "No"));
        }
    }

    private FormaPago leerFormaPago() {
        while (true) {
            System.out.println();
            System.out.println("Seleccione forma de pago:");
            System.out.println("1. TARJETA");
            System.out.println("2. TRANSFERENCIA");
            System.out.println("3. EFECTIVO");

            int opcion = lector.leerEntero("Opción: ");

            switch (opcion) {
                case 1:
                    return FormaPago.TARJETA;
                case 2:
                    return FormaPago.TRANSFERENCIA;
                case 3:
                    return FormaPago.EFECTIVO;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }
        }
    }

    private Estado leerEstado() {
        while (true) {
            System.out.println();
            System.out.println("Seleccione estado:");
            System.out.println("1. PENDIENTE");
            System.out.println("2. CONFIRMADO");
            System.out.println("3. TERMINADO");
            System.out.println("4. CANCELADO");

            int opcion = lector.leerEntero("Opción: ");

            switch (opcion) {
                case 1:
                    return Estado.PENDIENTE;
                case 2:
                    return Estado.CONFIRMADO;
                case 3:
                    return Estado.TERMINADO;
                case 4:
                    return Estado.CANCELADO;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }
        }
    }

    private void mostrarPedidoResumen(Pedido pedido) {
        String usuario = pedido.getUsuario() != null
                ? pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido()
                : "-";

        System.out.println("ID: " + pedido.getId());
        System.out.println("Fecha: " + pedido.getFecha());
        System.out.println("Usuario: " + usuario);
        System.out.println("Estado: " + pedido.getEstado());
        System.out.println("Forma de pago: " + pedido.getFormaPago());
        System.out.println("Total: " + pedido.getTotal());
    }
    private void listarPedidosPorUsuario() {
        System.out.println();
        System.out.println("=== LISTAR PEDIDOS POR USUARIO ===");

        listarUsuariosDisponibles();

        Long usuarioId = lector.leerLong("Ingrese el id del usuario: ");

        List<Pedido> pedidos = pedidoService.listarPorUsuario(usuarioId);

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos cargados para ese usuario.");
            return;
        }

        for (Pedido pedido : pedidos) {
            System.out.println("--------------------------------");
            mostrarPedidoResumen(pedido);
        }

        System.out.println("--------------------------------");
    }
}