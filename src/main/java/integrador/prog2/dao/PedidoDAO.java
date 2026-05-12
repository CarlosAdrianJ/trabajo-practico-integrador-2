package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.DetallePedido;
import integrador.prog2.entities.Pedido;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;
import integrador.prog2.exception.BusinessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDAO implements IBaseDAO<Pedido> {

    private final DetallePedidoDAO detallePedidoDAO;
    private final ProductoDAO productoDAO;
    public PedidoDAO() {
        this.detallePedidoDAO = new DetallePedidoDAO();
        this.productoDAO = new ProductoDAO();
    }

    @Override
    public Pedido crear(Pedido pedido) {
        return crearConDetalles(pedido);
    }

    public Pedido crearConDetalles(Pedido pedido) {
        String sqlPedido = """
                INSERT INTO pedido (fecha, estado, total, forma_pago, usuario_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        Connection con = null;

        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);

            pedido.setTotal(pedido.calcularTotal());

            try (PreparedStatement ps = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, Date.valueOf(pedido.getFecha()));
                ps.setString(2, pedido.getEstado().name());
                ps.setDouble(3, pedido.getTotal());
                ps.setString(4, pedido.getFormaPago().name());
                ps.setLong(5, pedido.getUsuario().getId());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedido.setId(rs.getLong(1));
                    }
                }
            }

            for (DetallePedido detalle : pedido.getDetalles()) {
                detallePedidoDAO.crear(detalle, pedido.getId(), con);

                productoDAO.descontarStock(
                        detalle.getProducto().getId(),
                        detalle.getCantidad(),
                        con
                );
            }

            con.commit();
            return pedido;

        } catch (Exception e) {
            hacerRollback(con);
            throw new BusinessException("Error creando pedido. La operación fue revertida.", e);
        } finally {
            cerrarConexion(con);
        }
    }

    @Override
    public List<Pedido> listar() {
        String sql = """
                SELECT 
                    p.id,
                    p.fecha,
                    p.estado,
                    p.total,
                    p.forma_pago,
                    p.eliminado,
                    p.created_at,
                    u.id AS usuario_id,
                    u.nombre AS usuario_nombre,
                    u.apellido AS usuario_apellido,
                    u.mail AS usuario_mail
                FROM pedido p
                JOIN usuario u ON p.usuario_id = u.id
                WHERE p.eliminado = false
                ORDER BY p.id
                """;

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }

            return pedidos;

        } catch (SQLException e) {
            throw new BusinessException("Error listando pedidos.", e);
        }
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        String sql = """
                SELECT 
                    p.id,
                    p.fecha,
                    p.estado,
                    p.total,
                    p.forma_pago,
                    p.eliminado,
                    p.created_at,
                    u.id AS usuario_id,
                    u.nombre AS usuario_nombre,
                    u.apellido AS usuario_apellido,
                    u.mail AS usuario_mail
                FROM pedido p
                JOIN usuario u ON p.usuario_id = u.id
                WHERE p.id = ?
                AND p.eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPedido(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new BusinessException("Error buscando pedido por id.", e);
        }
    }

    public Optional<Pedido> buscarPorIdConDetalles(Long id) {
        Optional<Pedido> pedidoOpt = buscarPorId(id);

        if (pedidoOpt.isEmpty()) {
            return Optional.empty();
        }

        Pedido pedido = pedidoOpt.get();
        pedido.setDetalles(detallePedidoDAO.listarPorPedidoId(id));
        pedido.setTotal(pedido.calcularTotal());

        return Optional.of(pedido);
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        String sql = """
                SELECT 
                    p.id,
                    p.fecha,
                    p.estado,
                    p.total,
                    p.forma_pago,
                    p.eliminado,
                    p.created_at,
                    u.id AS usuario_id,
                    u.nombre AS usuario_nombre,
                    u.apellido AS usuario_apellido,
                    u.mail AS usuario_mail
                FROM pedido p
                JOIN usuario u ON p.usuario_id = u.id
                WHERE p.eliminado = false
                AND u.id = ?
                ORDER BY p.id
                """;

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }

            return pedidos;

        } catch (SQLException e) {
            throw new BusinessException("Error listando pedidos por usuario.", e);
        }
    }

    @Override
    public Pedido actualizar(Pedido pedido) {
        String sql = """
                UPDATE pedido
                SET estado = ?,
                    forma_pago = ?,
                    total = ?
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pedido.getEstado().name());
            ps.setString(2, pedido.getFormaPago().name());
            ps.setDouble(3, pedido.getTotal());
            ps.setLong(4, pedido.getId());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new BusinessException("No se pudo actualizar el pedido. Verifique que exista.");
            }

            return pedido;

        } catch (SQLException e) {
            throw new BusinessException("Error actualizando pedido.", e);
        }
    }

    public void actualizarEstado(Long pedidoId, Estado estado) {
        String sql = """
                UPDATE pedido
                SET estado = ?
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado.name());
            ps.setLong(2, pedidoId);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new BusinessException("No se pudo actualizar el estado del pedido.");
            }

        } catch (SQLException e) {
            throw new BusinessException("Error actualizando estado del pedido.", e);
        }
    }

    public void actualizarFormaPago(Long pedidoId, FormaPago formaPago) {
        String sql = """
                UPDATE pedido
                SET forma_pago = ?
                WHERE id = ?
                AND eliminado = false
                """;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, formaPago.name());
            ps.setLong(2, pedidoId);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new BusinessException("No se pudo actualizar la forma de pago del pedido.");
            }

        } catch (SQLException e) {
            throw new BusinessException("Error actualizando forma de pago del pedido.", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        Connection con = null;

        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);

            detallePedidoDAO.eliminarPorPedidoId(id, con);

            String sql = """
                    UPDATE pedido
                    SET eliminado = true
                    WHERE id = ?
                    AND eliminado = false
                    """;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, id);

                int filas = ps.executeUpdate();

                if (filas == 0) {
                    throw new BusinessException("No se pudo eliminar el pedido. Verifique que exista.");
                }
            }

            con.commit();

        } catch (Exception e) {
            hacerRollback(con);
            throw new BusinessException("Error eliminando pedido. La operación fue revertida.", e);
        } finally {
            cerrarConexion(con);
        }
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("usuario_id"));
        usuario.setNombre(rs.getString("usuario_nombre"));
        usuario.setApellido(rs.getString("usuario_apellido"));
        usuario.setMail(rs.getString("usuario_mail"));

        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setFecha(rs.getDate("fecha").toLocalDate());
        pedido.setEstado(Estado.valueOf(rs.getString("estado")));
        pedido.setTotal(rs.getDouble("total"));
        pedido.setFormaPago(FormaPago.valueOf(rs.getString("forma_pago")));
        pedido.setUsuario(usuario);
        pedido.setEliminado(rs.getBoolean("eliminado"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            pedido.setCreatedAt(createdAt.toLocalDateTime());
        }

        return pedido;
    }

    private void hacerRollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void cerrarConexion(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException ignored) {
            }
        }
    }
}