package integrador.prog2.dao;

import integrador.prog2.entities.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements IBaseDAO<Usuario> {

    @Override
    public Usuario crear(Usuario entidad) {
        return null;
    }

    @Override
    public List<Usuario> listar() {
        return List.of();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.empty();
    }

    @Override
    public Usuario actualizar(Usuario entidad) {
        return null;
    }

    @Override
    public void eliminar(Long id) {
    }
}