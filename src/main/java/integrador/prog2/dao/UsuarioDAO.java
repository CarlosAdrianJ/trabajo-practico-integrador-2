package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Rol;
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

public class UsuarioDAO implements IBaseDAO<Usuario> {

    @Override
    public Usuario crear(Usuario usuario) {
        String sql = """
                INSERT INTO usuario (nombre, apellido, mail, celular, contrasena, rol)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getMail());
            ps.setString(4, usuario.getCelular());
            ps.setString(5, usuario.getContrasena());
            ps.setString(6, usuario.getRol().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getLong(1));
                }
            }

            return usuario;

        } catch (SQLException e) {
            throw new BusinessException("Error creando usuario. Revise que el mail no esté repetido.", e);
        }
    }

    @Override
    public List<Usuario> listar() {
        String sql = """
                SELECT id, nombre, apellido, mail, celular, contrasena, rol, eliminado, created_at
                FROM usuario
                WHERE eliminado = false
                ORDER BY id
                """;

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

            return usuarios;

        } catch (SQLException e) {
            throw new BusinessException("Error listando usuarios.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = """
                SELECT id, nombre, apellido, mail, celular, contrasena, rol, eliminado, created_at
                FROM usuario
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new BusinessException("Error buscando usuario por id.", e);
        }
    }

    public Optional<Usuario> buscarPorMail(String mail) {
        String sql = """
                SELECT id, nombre, apellido, mail, celular, contrasena, rol, eliminado, created_at
                FROM usuario
                WHERE LOWER(mail) = LOWER(?)
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new BusinessException("Error buscando usuario por mail.", e);
        }
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        String sql = """
                UPDATE usuario
                SET nombre = ?,
                    apellido = ?,
                    mail = ?,
                    celular = ?,
                    contrasena = ?,
                    rol = ?
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getMail());
            ps.setString(4, usuario.getCelular());
            ps.setString(5, usuario.getContrasena());
            ps.setString(6, usuario.getRol().name());
            ps.setLong(7, usuario.getId());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BusinessException("No se pudo actualizar el usuario. Verifique que exista.");
            }

            return usuario;

        } catch (SQLException e) {
            throw new BusinessException("Error actualizando usuario. Revise que el mail no esté repetido.", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = """
                UPDATE usuario
                SET eliminado = true
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BusinessException("No se pudo eliminar el usuario. Verifique que exista.");
            }

        } catch (SQLException e) {
            throw new BusinessException("Error eliminando usuario.", e);
        }
    }

    public boolean existeMail(String mail) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM usuario
                WHERE LOWER(mail) = LOWER(?)
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new BusinessException("Error validando mail de usuario.", e);
        }
    }

    public boolean existeMailEnOtroUsuario(String mail, Long idUsuario) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM usuario
                WHERE LOWER(mail) = LOWER(?)
                AND id <> ?
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mail);
            ps.setLong(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new BusinessException("Error validando mail de usuario.", e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setId(rs.getLong("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setMail(rs.getString("mail"));
        usuario.setCelular(rs.getString("celular"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setRol(Rol.valueOf(rs.getString("rol")));
        usuario.setEliminado(rs.getBoolean("eliminado"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            usuario.setCreatedAt(createdAt.toLocalDateTime());
        }

        return usuario;
    }
}