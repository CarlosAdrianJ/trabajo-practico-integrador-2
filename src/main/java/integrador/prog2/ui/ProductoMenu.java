package integrador.prog2.ui;

import integrador.prog2.entities.Categoria;
import integrador.prog2.entities.Producto;
import integrador.prog2.service.CategoriaService;
import integrador.prog2.service.ProductoService;

import java.util.List;

public class ProductoMenu {

    private final LectorConsola lector;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoMenu(LectorConsola lector) {
        this.lector = lector;
        this.productoService = new ProductoService();
        this.categoriaService = new CategoriaService();
    }

    public void mostrar() {
        int opcion;

        do {
            mostrarOpciones();
            opcion = lector.leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    ManejadorErroresConsola.ejecutar(this::listarProductos);
                    lector.pausar();
                    break;
                case 2:
                    ManejadorErroresConsola.ejecutar(this::listarProductosPorCategoria);
                    lector.pausar();
                    break;
                case 3:
                    ManejadorErroresConsola.ejecutar(this::crearProducto);
                    lector.pausar();
                    break;
                case 4:
                    ManejadorErroresConsola.ejecutar(this::editarProducto);
                    lector.pausar();
                    break;
                case 5:
                    ManejadorErroresConsola.ejecutar(this::eliminarProducto);
                    lector.pausar();
                    break;
                case 0:
                    System.out.println("Volviendo al menu principal...");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente nuevamente.");
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarOpciones() {
        System.out.println();
        System.out.println("=== GESTION DE PRODUCTOS ===");
        System.out.println("1. Listar productos");
        System.out.println("2. Listar productos por categoria");
        System.out.println("3. Crear producto");
        System.out.println("4. Editar producto");
        System.out.println("5. Eliminar producto");
        System.out.println("0. Volver");
    }

    private void listarProductos() {
        List<Producto> productos = productoService.listar();

        System.out.println();
        System.out.println("=== LISTADO DE PRODUCTOS ===");

        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }

        mostrarListaProductos(productos);
    }

    private void listarProductosPorCategoria() {
        System.out.println();
        System.out.println("=== LISTAR PRODUCTOS POR CATEGORIA ===");

        listarCategoriasDisponibles();

        Long categoriaId = lector.leerLong("Ingrese el id de la categoria: ");

        List<Producto> productos = productoService.listarPorCategoria(categoriaId);

        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados para esa categoria.");
            return;
        }

        mostrarListaProductos(productos);
    }

    private void crearProducto() {
        System.out.println();
        System.out.println("=== CREAR PRODUCTO ===");

        listarCategoriasDisponibles();

        String nombre = lector.leerTextoNoVacio("Nombre: ");
        Double precio = lector.leerDouble("Precio: ");
        String descripcion = lector.leerTexto("Descripcion: ");
        int stock = lector.leerEntero("Stock: ");
        String imagen = lector.leerTexto("Imagen: ");
        boolean disponible = lector.confirmar("¿El producto esta disponible?");
        Long categoriaId = lector.leerLong("Id de categoria: ");

        Producto productoCreado = productoService.crear(
                nombre,
                precio,
                descripcion,
                stock,
                imagen,
                disponible,
                categoriaId
        );

        System.out.println();
        System.out.println("Producto creado correctamente.");
        System.out.println("ID generado: " + productoCreado.getId());
    }

    private void editarProducto() {
        System.out.println();
        System.out.println("=== EDITAR PRODUCTO ===");

        listarProductos();

        Long id = lector.leerLong("Ingrese el id del producto a editar: ");

        Producto productoActual = productoService.buscarPorId(id);

        System.out.println();
        System.out.println("Producto actual:");
        mostrarProducto(productoActual);

        System.out.println();
        System.out.println("Ingrese los nuevos datos. Presione ENTER para mantener el valor actual.");

        String nuevoNombre = lector.leerTexto("Nuevo nombre: ");
        Double nuevoPrecio = leerDoubleOpcional("Nuevo precio: ");
        String nuevaDescripcion = lector.leerTexto("Nueva descripcion: ");
        Integer nuevoStock = leerEnteroOpcional("Nuevo stock: ");
        String nuevaImagen = lector.leerTexto("Nueva imagen: ");

        Boolean nuevaDisponibilidad = leerBooleanOpcional("Nueva disponibilidad");

        System.out.println();
        System.out.println("Categoria actual: " + productoActual.getCategoria().getNombre());
        boolean cambiarCategoria = lector.confirmar("¿Desea cambiar la categoria?");

        Long nuevaCategoriaId = null;

        if (cambiarCategoria) {
            listarCategoriasDisponibles();
            nuevaCategoriaId = lector.leerLong("Nuevo id de categoria: ");
        }

        Producto productoActualizado = productoService.actualizar(
                id,
                nuevoNombre,
                nuevoPrecio,
                nuevaDescripcion,
                nuevoStock,
                nuevaImagen,
                nuevaDisponibilidad,
                nuevaCategoriaId
        );

        System.out.println();
        System.out.println("Producto actualizado correctamente.");
        mostrarProducto(productoActualizado);
    }

    private void eliminarProducto() {
        System.out.println();
        System.out.println("=== ELIMINAR PRODUCTO ===");

        listarProductos();

        Long id = lector.leerLong("Ingrese el id del producto a eliminar: ");

        Producto producto = productoService.buscarPorId(id);

        System.out.println();
        System.out.println("Producto seleccionado:");
        mostrarProducto(producto);

        boolean confirma = lector.confirmar("¿Confirma la eliminacion logica de este producto?");

        if (!confirma) {
            System.out.println("Operacion cancelada.");
            return;
        }

        productoService.eliminar(id);

        System.out.println("Producto eliminado correctamente.");
    }

    private void listarCategoriasDisponibles() {
        List<Categoria> categorias = categoriaService.listar();

        System.out.println();
        System.out.println("=== CATEGORIAS DISPONIBLES ===");

        if (categorias.isEmpty()) {
            System.out.println("No hay categorias cargadas.");
            return;
        }

        for (Categoria categoria : categorias) {
            System.out.println("ID: " + categoria.getId() + " | Nombre: " + categoria.getNombre());
        }
    }

    private void mostrarListaProductos(List<Producto> productos) {
        for (Producto producto : productos) {
            System.out.println("--------------------------------");
            mostrarProducto(producto);
        }

        System.out.println("--------------------------------");
    }

    private void mostrarProducto(Producto producto) {
        String categoria = producto.getCategoria() != null
                ? producto.getCategoria().getNombre()
                : "-";

        System.out.println("ID: " + producto.getId());
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Precio: " + producto.getPrecio());
        System.out.println("Descripcion: " + mostrarTexto(producto.getDescripcion()));
        System.out.println("Stock: " + producto.getStock());
        System.out.println("Imagen: " + mostrarTexto(producto.getImagen()));
        System.out.println("Disponible: " + (producto.getDisponible() ? "SI" : "NO"));
        System.out.println("Categoria: " + categoria);
    }

    private String mostrarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "-";
        }

        return texto;
    }

    private Double leerDoubleOpcional(String mensaje) {
        String texto = lector.leerTexto(mensaje);

        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un numero decimal valido.");
        }
    }

    private Integer leerEnteroOpcional(String mensaje) {
        String texto = lector.leerTexto(mensaje);

        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un numero entero valido.");
        }
    }

    private Boolean leerBooleanOpcional(String mensaje) {
        System.out.println();
        System.out.println(mensaje + ":");
        System.out.println("1. Disponible");
        System.out.println("2. No disponible");
        System.out.println("0. Mantener valor actual");

        int opcion = lector.leerEntero("Seleccione una opcion: ");

        return switch (opcion) {
            case 1 -> true;
            case 2 -> false;
            case 0 -> null;
            default -> throw new IllegalArgumentException("Opcion invalida para disponibilidad.");
        };
    }
}