package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.DetallePedido;
import integrador.prog2.entities.Producto;
import integrador.prog2.exception.BusinessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {

    public void crear(DetallePedido detalle, Long pedidoId, Connection con) {
        String sql = """
                INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, subtotal)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, pedidoId);
            ps.setLong(2, detalle.getProducto().getId());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getSubtotal());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    detalle.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new BusinessException("Error creando detalle de pedido.", e);
        }
    }

    public List<DetallePedido> listarPorPedidoId(Long pedidoId) {
        String sql = """
                SELECT 
                    d.id,
                    d.cantidad,
                    d.subtotal,
                    d.producto_id,
                    p.nombre AS producto_nombre,
                    p.precio AS producto_precio
                FROM detalle_pedido d
                JOIN producto p ON d.producto_id = p.id
                WHERE d.pedido_id = ?
                AND d.eliminado = false
                ORDER BY d.id
                """;

        List<DetallePedido> detalles = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, pedidoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapearDetalle(rs));
                }
            }

            return detalles;

        } catch (SQLException e) {
            throw new BusinessException("Error listando detalles del pedido.", e);
        }
    }

    public void eliminarPorPedidoId(Long pedidoId, Connection con) {
        String sql = """
                UPDATE detalle_pedido
                SET eliminado = true
                WHERE pedido_id = ?
                AND eliminado = false
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, pedidoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException("Error eliminando detalles del pedido.", e);
        }
    }

    private DetallePedido mapearDetalle(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getLong("producto_id"));
        producto.setNombre(rs.getString("producto_nombre"));
        producto.setPrecio(rs.getDouble("producto_precio"));

        DetallePedido detalle = new DetallePedido();
        detalle.setId(rs.getLong("id"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setSubtotal(rs.getDouble("subtotal"));
        detalle.setProducto(producto);

        return detalle;
    }
}