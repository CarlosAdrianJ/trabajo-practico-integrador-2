package integrador.prog2.service;

import integrador.prog2.data.MemoriaDatos;
import integrador.prog2.entities.Categoria;
import integrador.prog2.entities.Producto;
import integrador.prog2.exception.BusinessException;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

import java.util.Comparator;
import java.util.List;

public class CategoriaService {

    public List<Categoria> listar() {
        return MemoriaDatos.CATEGORIAS.stream()
                .filter(categoria -> !categoria.isEliminado())
                .sorted(Comparator.comparing(Categoria::getId))
                .toList();
    }

    public Categoria buscarPorId(Long id) {
        validarId(id);

        return MemoriaDatos.CATEGORIAS.stream()
                .filter(categoria -> !categoria.isEliminado())
                .filter(categoria -> categoria.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Categoria", id));
    }

    public Categoria crear(String nombre, String descripcion) {
        validarNombre(nombre);
        validarDescripcion(descripcion);

        String nombreNormalizado = nombre.trim();
        String descripcionNormalizada = descripcion.trim();

        if (existeNombre(nombreNormalizado)) {
            throw new BusinessException("Ya existe una categoria con ese nombre.");
        }

        Categoria categoria = new Categoria(nombreNormalizado, descripcionNormalizada);
        categoria.setId(MemoriaDatos.siguienteIdCategoria());

        MemoriaDatos.CATEGORIAS.add(categoria);

        return categoria;
    }

    public Categoria actualizar(Long id, String nuevoNombre, String nuevaDescripcion) {
        validarId(id);

        Categoria categoria = buscarPorId(id);

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            String nombreNormalizado = nuevoNombre.trim();

            if (existeNombreEnOtraCategoria(nombreNormalizado, id)) {
                throw new BusinessException("Ya existe otra categoria con ese nombre.");
            }

            categoria.setNombre(nombreNormalizado);
        }

        if (nuevaDescripcion != null) {
            categoria.setDescripcion(normalizarTextoOpcional(nuevaDescripcion));
        }

        return categoria;
    }

    public void eliminar(Long id) {
        validarId(id);

        Categoria categoria = buscarPorId(id);

        if (tieneProductosActivos(id)) {
            throw new BusinessException(
                    "No se puede eliminar la categoria porque tiene productos activos asociados. " +
                            "Primero elimine o reasigne esos productos."
            );
        }

        categoria.setEliminado(true);
    }

    private boolean existeNombre(String nombre) {
        return MemoriaDatos.CATEGORIAS.stream()
                .filter(categoria -> !categoria.isEliminado())
                .anyMatch(categoria -> categoria.getNombre().equalsIgnoreCase(nombre));
    }

    private boolean existeNombreEnOtraCategoria(String nombre, Long idCategoria) {
        return MemoriaDatos.CATEGORIAS.stream()
                .filter(categoria -> !categoria.isEliminado())
                .filter(categoria -> !categoria.getId().equals(idCategoria))
                .anyMatch(categoria -> categoria.getNombre().equalsIgnoreCase(nombre));
    }

    private boolean tieneProductosActivos(Long categoriaId) {
        return MemoriaDatos.PRODUCTOS.stream()
                .filter(producto -> !producto.isEliminado())
                .map(Producto::getCategoria)
                .filter(categoria -> categoria != null && categoria.getId() != null)
                .anyMatch(categoria -> categoria.getId().equals(categoriaId));
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id debe ser un numero mayor que cero.");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de la categoria no puede estar vacio.");
        }
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ValidationException("La descripcion de la categoria no puede estar vacia.");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }
}
