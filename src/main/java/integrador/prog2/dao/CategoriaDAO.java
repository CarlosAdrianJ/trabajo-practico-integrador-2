package integrador.prog2.dao;

import integrador.prog2.entities.Categoria;

import java.util.List;
import java.util.Optional;

public class CategoriaDAO implements IBaseDAO<Categoria> {

    @Override
    public Categoria crear(Categoria entidad) {
        return null;
    }

    @Override
    public List<Categoria> listar() {
        return List.of();
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return Optional.empty();
    }

    @Override
    public Categoria actualizar(Categoria entidad) {
        return null;
    }

    @Override
    public void eliminar(Long id) {
    }
}