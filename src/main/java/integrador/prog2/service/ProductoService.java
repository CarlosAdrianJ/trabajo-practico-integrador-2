package integrador.prog2.service;

import integrador.prog2.data.MemoriaDatos;
import integrador.prog2.entities.Categoria;
import integrador.prog2.entities.Producto;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

import java.util.Comparator;
import java.util.List;

public class ProductoService {

    private final CategoriaService categoriaService;

    public ProductoService() {
        this.categoriaService = new CategoriaService();
    }

    public List<Producto> listar() {
        return MemoriaDatos.PRODUCTOS.stream()
                .filter(producto -> !producto.isEliminado())
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        validarId(categoriaId);

        categoriaService.buscarPorId(categoriaId);

        return MemoriaDatos.PRODUCTOS.stream()
                .filter(producto -> !producto.isEliminado())
                .filter(producto -> producto.getCategoria() != null)
                .filter(producto -> producto.getCategoria().getId().equals(categoriaId))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    public Producto buscarPorId(Long id) {
        validarId(id);

        return MemoriaDatos.PRODUCTOS.stream()
                .filter(producto -> !producto.isEliminado())
                .filter(producto -> producto.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Producto", id));
    }

    public Producto crear(
            String nombre,
            Double precio,
            String descripcion,
            int stock,
            String imagen,
            Boolean disponible,
            Long categoriaId
    ) {
        validarNombre(nombre);
        validarPrecio(precio);
        validarStock(stock);
        validarId(categoriaId);

        Categoria categoria = categoriaService.buscarPorId(categoriaId);

        Producto producto = new Producto(
                nombre.trim(),
                precio,
                normalizarTextoOpcional(descripcion),
                stock,
                normalizarTextoOpcional(imagen),
                disponible != null ? disponible : true,
                categoria
        );
        producto.setId(MemoriaDatos.siguienteIdProducto());

        MemoriaDatos.PRODUCTOS.add(producto);

        return producto;
    }

    public Producto actualizar(
            Long id,
            String nuevoNombre,
            Double nuevoPrecio,
            String nuevaDescripcion,
            Integer nuevoStock,
            String nuevaImagen,
            Boolean nuevaDisponibilidad,
            Long nuevaCategoriaId
    ) {
        validarId(id);

        Producto producto = buscarPorId(id);

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            producto.setNombre(nuevoNombre.trim());
        }

        if (nuevoPrecio != null) {
            validarPrecio(nuevoPrecio);
            producto.setPrecio(nuevoPrecio);
        }

        if (nuevaDescripcion != null) {
            producto.setDescripcion(normalizarTextoOpcional(nuevaDescripcion));
        }

        if (nuevoStock != null) {
            validarStock(nuevoStock);
            producto.setStock(nuevoStock);
        }

        if (nuevaImagen != null) {
            producto.setImagen(normalizarTextoOpcional(nuevaImagen));
        }

        if (nuevaDisponibilidad != null) {
            producto.setDisponible(nuevaDisponibilidad);
        }

        if (nuevaCategoriaId != null) {
            validarId(nuevaCategoriaId);
            Categoria categoria = categoriaService.buscarPorId(nuevaCategoriaId);
            producto.setCategoria(categoria);
        }

        return producto;
    }

    public void eliminar(Long id) {
        validarId(id);

        Producto producto = buscarPorId(id);
        producto.setEliminado(true);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id debe ser un numero mayor que cero.");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre del producto no puede estar vacio.");
        }
    }

    private void validarPrecio(Double precio) {
        if (precio == null || precio < 0) {
            throw new ValidationException("El precio no puede ser negativo.");
        }
    }

    private void validarStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new ValidationException("El stock no puede ser negativo.");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }
}
