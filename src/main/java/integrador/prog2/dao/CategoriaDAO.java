package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.Categoria;
import integrador.prog2.exception.BusinessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDAO implements IBaseDAO<Categoria> {

    @Override
    public Categoria crear(Categoria categoria) {
        String sql = """
                INSERT INTO categoria (nombre, descripcion)
                VALUES (?, ?)
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getLong(1));
                }
            }

            return categoria;

        } catch (SQLException e) {
            throw new BusinessException("Error creando categoría. Revise que el nombre no esté repetido.", e);
        }
    }

    @Override
    public List<Categoria> listar() {
        String sql = """
                SELECT id, nombre, descripcion, eliminado, created_at
                FROM categoria
                WHERE eliminado = false
                ORDER BY id
                """;

        List<Categoria> categorias = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }

            return categorias;

        } catch (SQLException e) {
            throw new BusinessException("Error listando categorías.", e);
        }
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        String sql = """
                SELECT id, nombre, descripcion, eliminado, created_at
                FROM categoria
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCategoria(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new BusinessException("Error buscando categoría por id.", e);
        }
    }

    @Override
    public Categoria actualizar(Categoria categoria) {
        String sql = """
                UPDATE categoria
                SET nombre = ?, descripcion = ?
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setLong(3, categoria.getId());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BusinessException("No se pudo actualizar la categoría. Verifique que exista.");
            }

            return categoria;

        } catch (SQLException e) {
            throw new BusinessException("Error actualizando categoría. Revise que el nombre no esté repetido.", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = """
                UPDATE categoria
                SET eliminado = true
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BusinessException("No se pudo eliminar la categoría. Verifique que exista.");
            }

        } catch (SQLException e) {
            throw new BusinessException("Error eliminando categoría.", e);
        }
    }

    public boolean existeNombre(String nombre) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new BusinessException("Error validando nombre de categoría.", e);
        }
    }

    public boolean existeNombreEnOtraCategoria(String nombre, Long idCategoria) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                AND id <> ?
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setLong(2, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new BusinessException("Error validando nombre de categoría.", e);
        }
    }

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();

        categoria.setId(rs.getLong("id"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        categoria.setEliminado(rs.getBoolean("eliminado"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            categoria.setCreatedAt(createdAt.toLocalDateTime());
        }

        return categoria;
    }
}