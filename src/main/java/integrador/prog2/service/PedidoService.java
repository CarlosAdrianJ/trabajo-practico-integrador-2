package integrador.prog2.service;

import integrador.prog2.dao.PedidoDAO;
import integrador.prog2.entities.Pedido;
import integrador.prog2.entities.Producto;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;
import integrador.prog2.exception.BusinessException;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

import java.util.List;
import java.util.Map;

public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public PedidoService() {
        this.pedidoDAO = new PedidoDAO();
        this.usuarioService = new UsuarioService();
        this.productoService = new ProductoService();
    }

    public List<Pedido> listar() {
        return pedidoDAO.listar();
    }

    public Pedido buscarPorId(Long id) {
        validarId(id);

        return pedidoDAO.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", id));
    }

    public Pedido buscarPorIdConDetalles(Long id) {
        validarId(id);

        return pedidoDAO.buscarPorIdConDetalles(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", id));
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        validarId(usuarioId);

        usuarioService.buscarPorId(usuarioId);

        return pedidoDAO.listarPorUsuario(usuarioId);
    }

    public Pedido crear(Long usuarioId, FormaPago formaPago, Map<Long, Integer> productosCantidades) {
        validarId(usuarioId);
        validarFormaPago(formaPago);

        if (productosCantidades == null || productosCantidades.isEmpty()) {
            throw new ValidationException("El pedido debe tener al menos un producto.");
        }

        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        Pedido pedido = new Pedido(usuario, formaPago);

        for (Map.Entry<Long, Integer> item : productosCantidades.entrySet()) {
            Long productoId = item.getKey();
            Integer cantidad = item.getValue();

            validarId(productoId);
            validarCantidad(cantidad);

            Producto producto = productoService.buscarPorId(productoId);

            if (producto.getDisponible() == null || !producto.getDisponible()) {
                throw new BusinessException("El producto no está disponible: " + producto.getNombre());
            }

            if (producto.getStock() < cantidad) {
                throw new BusinessException(
                        "Stock insuficiente para el producto: " + producto.getNombre()
                                + ". Stock disponible: " + producto.getStock()
                                + ". Cantidad solicitada: " + cantidad
                );
            }

            pedido.addDetallePedido(cantidad, producto.getPrecio(), producto);
        }

        if (pedido.getDetalles().isEmpty()) {
            throw new ValidationException("El pedido debe tener al menos un detalle.");
        }

        pedido.setTotal(pedido.calcularTotal());

        return pedidoDAO.crearConDetalles(pedido);
    }

    public void actualizarEstado(Long pedidoId, Estado estado) {
        validarId(pedidoId);
        validarEstado(estado);

        buscarPorId(pedidoId);

        pedidoDAO.actualizarEstado(pedidoId, estado);
    }

    public void actualizarFormaPago(Long pedidoId, FormaPago formaPago) {
        validarId(pedidoId);
        validarFormaPago(formaPago);

        buscarPorId(pedidoId);

        pedidoDAO.actualizarFormaPago(pedidoId, formaPago);
    }

    public void eliminar(Long pedidoId) {
        validarId(pedidoId);

        buscarPorId(pedidoId);

        pedidoDAO.eliminar(pedidoId);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id debe ser un número mayor que cero.");
        }
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ValidationException("La cantidad debe ser mayor que cero.");
        }
    }

    private void validarFormaPago(FormaPago formaPago) {
        if (formaPago == null) {
            throw new ValidationException("Debe seleccionar una forma de pago válida.");
        }
    }

    private void validarEstado(Estado estado) {
        if (estado == null) {
            throw new ValidationException("Debe seleccionar un estado válido.");
        }
    }
}