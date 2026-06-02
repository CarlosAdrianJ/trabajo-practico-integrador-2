package integrador.prog2.service;

import integrador.prog2.dao.CategoriaDAO;
import integrador.prog2.entities.Categoria;
import integrador.prog2.exception.BusinessException;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

import java.util.List;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public List<Categoria> listar() {
        return categoriaDAO.listar();
    }

    public Categoria buscarPorId(Long id) {
        validarId(id);

        return categoriaDAO.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría", id));
    }

    public Categoria crear(String nombre, String descripcion) {
        validarNombre(nombre);

        String nombreNormalizado = nombre.trim();
        validarDescripcion(descripcion);
        String descripcionNormalizada = descripcion.trim();

        if (categoriaDAO.existeNombre(nombreNormalizado)) {
            throw new BusinessException("Ya existe una categoría con ese nombre.");
        }

        Categoria categoria = new Categoria(nombreNormalizado, descripcionNormalizada);

        return categoriaDAO.crear(categoria);
    }

    public Categoria actualizar(Long id, String nuevoNombre, String nuevaDescripcion) {
        validarId(id);

        Categoria categoria = buscarPorId(id);

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            String nombreNormalizado = nuevoNombre.trim();

            if (categoriaDAO.existeNombreEnOtraCategoria(nombreNormalizado, id)) {
                throw new BusinessException("Ya existe otra categoría con ese nombre.");
            }

            categoria.setNombre(nombreNormalizado);
        }

        if (nuevaDescripcion != null) {
            categoria.setDescripcion(normalizarTextoOpcional(nuevaDescripcion));
        }

        return categoriaDAO.actualizar(categoria);
    }

    public void eliminar(Long id) {
        validarId(id);

        buscarPorId(id);
        if (categoriaDAO.tieneProductosActivos(id)) {
            throw new BusinessException(
                    "No se puede eliminar la categoría porque tiene productos activos asociados. " +
                            "Primero elimine o reasigne esos productos."
            );
        }
        categoriaDAO.eliminar(id);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id debe ser un número mayor que cero.");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de la categoría no puede estar vacío.");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }
    private void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ValidationException("La descripción de la categoría no puede estar vacía.");
        }
    }
}