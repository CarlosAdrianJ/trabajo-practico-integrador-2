package integrador.prog2.dao;

import integrador.prog2.entities.Producto;

import java.util.List;
import java.util.Optional;

public class ProductoDAO implements IBaseDAO<Producto> {

    @Override
    public Producto crear(Producto entidad) {
        return null;
    }

    @Override
    public List<Producto> listar() {
        return List.of();
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return Optional.empty();
    }

    @Override
    public Producto actualizar(Producto entidad) {
        return null;
    }

    @Override
    public void eliminar(Long id) {
    }
}