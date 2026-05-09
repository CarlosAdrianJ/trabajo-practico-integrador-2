package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.Categoria;
import integrador.prog2.entities.Producto;
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

public class ProductoDAO implements IBaseDAO<Producto> {

    @Override
    public Producto crear(Producto producto) {
        String sql = """
                INSERT INTO producto (nombre, precio, descripcion, stock, imagen, disponible, categoria_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setString(3, producto.getDescripcion());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getImagen());
            ps.setBoolean(6, producto.getDisponible());
            ps.setLong(7, producto.getCategoria().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setId(rs.getLong(1));
                }
            }

            return producto;

        } catch (SQLException e) {
            throw new BusinessException("Error creando producto.", e);
        }
    }

    @Override
    public List<Producto> listar() {
        String sql = """
                SELECT 
                    p.id,
                    p.nombre,
                    p.precio,
                    p.descripcion,
                    p.stock,
                    p.imagen,
                    p.disponible,
                    p.eliminado,
                    p.created_at,
                    c.id AS categoria_id,
                    c.nombre AS categoria_nombre,
                    c.descripcion AS categoria_descripcion
                FROM producto p
                JOIN categoria c ON p.categoria_id = c.id
                WHERE p.eliminado = false
                ORDER BY p.id
                """;

        List<Producto> productos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }

            return productos;

        } catch (SQLException e) {
            throw new BusinessException("Error listando productos.", e);
        }
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        String sql = """
                SELECT 
                    p.id,
                    p.nombre,
                    p.precio,
                    p.descripcion,
                    p.stock,
                    p.imagen,
                    p.disponible,
                    p.eliminado,
                    p.created_at,
                    c.id AS categoria_id,
                    c.nombre AS categoria_nombre,
                    c.descripcion AS categoria_descripcion
                FROM producto p
                JOIN categoria c ON p.categoria_id = c.id
                WHERE p.eliminado = false
                AND c.eliminado = false
                AND c.id = ?
                ORDER BY p.id
                """;

        List<Producto> productos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, categoriaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }

            return productos;

        } catch (SQLException e) {
            throw new BusinessException("Error listando productos por categoría.", e);
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        String sql = """
                SELECT 
                    p.id,
                    p.nombre,
                    p.precio,
                    p.descripcion,
                    p.stock,
                    p.imagen,
                    p.disponible,
                    p.eliminado,
                    p.created_at,
                    c.id AS categoria_id,
                    c.nombre AS categoria_nombre,
                    c.descripcion AS categoria_descripcion
                FROM producto p
                JOIN categoria c ON p.categoria_id = c.id
                WHERE p.id = ?
                AND p.eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProducto(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new BusinessException("Error buscando producto por id.", e);
        }
    }

    @Override
    public Producto actualizar(Producto producto) {
        String sql = """
                UPDATE producto
                SET nombre = ?,
                    precio = ?,
                    descripcion = ?,
                    stock = ?,
                    imagen = ?,
                    disponible = ?,
                    categoria_id = ?
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setString(3, producto.getDescripcion());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getImagen());
            ps.setBoolean(6, producto.getDisponible());
            ps.setLong(7, producto.getCategoria().getId());
            ps.setLong(8, producto.getId());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BusinessException("No se pudo actualizar el producto. Verifique que exista.");
            }

            return producto;

        } catch (SQLException e) {
            throw new BusinessException("Error actualizando producto.", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = """
                UPDATE producto
                SET eliminado = true
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new BusinessException("No se pudo eliminar el producto. Verifique que exista.");
            }

        } catch (SQLException e) {
            throw new BusinessException("Error eliminando producto.", e);
        }
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("categoria_id"));
        categoria.setNombre(rs.getString("categoria_nombre"));
        categoria.setDescripcion(rs.getString("categoria_descripcion"));

        Producto producto = new Producto();
        producto.setId(rs.getLong("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setStock(rs.getInt("stock"));
        producto.setImagen(rs.getString("imagen"));
        producto.setDisponible(rs.getBoolean("disponible"));
        producto.setCategoria(categoria);
        producto.setEliminado(rs.getBoolean("eliminado"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            producto.setCreatedAt(createdAt.toLocalDateTime());
        }

        return producto;
    }
}