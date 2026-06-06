package integrador.prog2.ui;

import integrador.prog2.entities.Categoria;
import integrador.prog2.service.CategoriaService;

import java.util.List;

public class CategoriaMenu {

    private final LectorConsola lector;
    private final CategoriaService categoriaService;

    public CategoriaMenu(LectorConsola lector) {
        this.lector = lector;
        this.categoriaService = new CategoriaService();
    }

    public void mostrar() {
        int opcion;

        do {
            mostrarOpciones();
            opcion = lector.leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    ManejadorErroresConsola.ejecutar(this::listarCategorias);
                    lector.pausar();
                    break;
                case 2:
                    ManejadorErroresConsola.ejecutar(this::crearCategoria);
                    lector.pausar();
                    break;
                case 3:
                    ManejadorErroresConsola.ejecutar(this::editarCategoria);
                    lector.pausar();
                    break;
                case 4:
                    ManejadorErroresConsola.ejecutar(this::eliminarCategoria);
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
        System.out.println("=== GESTION DE CATEGORIAS ===");
        System.out.println("1. Listar categorias");
        System.out.println("2. Crear categoria");
        System.out.println("3. Editar categoria");
        System.out.println("4. Eliminar categoria");
        System.out.println("0. Volver");
    }

    private void listarCategorias() {
        List<Categoria> categorias = categoriaService.listar();

        System.out.println();
        System.out.println("=== LISTADO DE CATEGORIAS ===");

        if (categorias.isEmpty()) {
            System.out.println("No hay categorias cargadas.");
            return;
        }

        for (Categoria categoria : categorias) {
            System.out.println("--------------------------------");
            System.out.println("ID: " + categoria.getId());
            System.out.println("Nombre: " + categoria.getNombre());
            System.out.println("Descripcion: " + mostrarTexto(categoria.getDescripcion()));
        }

        System.out.println("--------------------------------");
    }

    private void crearCategoria() {
        System.out.println();
        System.out.println("=== CREAR CATEGORIA ===");

        String nombre = lector.leerTextoNoVacio("Nombre: ");
        String descripcion = lector.leerTextoNoVacio("Descripcion: ");

        Categoria categoriaCreada = categoriaService.crear(nombre, descripcion);

        System.out.println();
        System.out.println("Categoria creada correctamente.");
        System.out.println("ID generado: " + categoriaCreada.getId());
    }

    private void editarCategoria() {
        System.out.println();
        System.out.println("=== EDITAR CATEGORIA ===");

        listarCategorias();

        Long id = lector.leerLong("Ingrese el id de la categoria a editar: ");

        Categoria categoriaActual = categoriaService.buscarPorId(id);

        System.out.println();
        System.out.println("Categoria actual:");
        System.out.println("ID: " + categoriaActual.getId());
        System.out.println("Nombre: " + categoriaActual.getNombre());
        System.out.println("Descripcion: " + mostrarTexto(categoriaActual.getDescripcion()));

        System.out.println();
        System.out.println("Ingrese los nuevos datos. Presione ENTER para mantener el valor actual.");

        String nuevoNombre = lector.leerTexto("Nuevo nombre: ");
        String nuevaDescripcion = lector.leerTexto("Nueva descripcion: ");

        Categoria categoriaActualizada = categoriaService.actualizar(id, nuevoNombre, nuevaDescripcion);

        System.out.println();
        System.out.println("Categoria actualizada correctamente.");
        System.out.println("ID: " + categoriaActualizada.getId());
        System.out.println("Nombre: " + categoriaActualizada.getNombre());
        System.out.println("Descripcion: " + mostrarTexto(categoriaActualizada.getDescripcion()));
    }

    private void eliminarCategoria() {
        System.out.println();
        System.out.println("=== ELIMINAR CATEGORIA ===");

        listarCategorias();

        Long id = lector.leerLong("Ingrese el id de la categoria a eliminar: ");

        Categoria categoria = categoriaService.buscarPorId(id);

        System.out.println();
        System.out.println("Categoria seleccionada:");
        System.out.println("ID: " + categoria.getId());
        System.out.println("Nombre: " + categoria.getNombre());
        System.out.println("Descripcion: " + mostrarTexto(categoria.getDescripcion()));

        boolean confirma = lector.confirmar("¿Confirma la eliminacion logica de esta categoria?");

        if (!confirma) {
            System.out.println("Operacion cancelada.");
            return;
        }

        categoriaService.eliminar(id);

        System.out.println("Categoria eliminada correctamente.");
    }

    private String mostrarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "-";
        }

        return texto;
    }
}