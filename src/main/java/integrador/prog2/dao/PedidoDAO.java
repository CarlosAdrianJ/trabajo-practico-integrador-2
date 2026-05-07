package integrador.prog2.dao;

import integrador.prog2.entities.Pedido;

import java.util.List;
import java.util.Optional;

public class PedidoDAO implements IBaseDAO<Pedido> {

    @Override
    public Pedido crear(Pedido entidad) {
        return null;
    }

    @Override
    public List<Pedido> listar() {
        return List.of();
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return Optional.empty();
    }

    @Override
    public Pedido actualizar(Pedido entidad) {
        return null;
    }

    @Override
    public void eliminar(Long id) {
    }
}