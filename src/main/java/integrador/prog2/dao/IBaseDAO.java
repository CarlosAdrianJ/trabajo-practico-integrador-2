package integrador.prog2.dao;

import java.util.List;
import java.util.Optional;

public interface IBaseDAO<T> {

    T crear(T entidad);

    List<T> listar();

    Optional<T> buscarPorId(Long id);

    T actualizar(T entidad);

    void eliminar(Long id);
}