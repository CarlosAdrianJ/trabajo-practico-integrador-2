package integrador.prog2.service;

import integrador.prog2.data.MemoriaDatos;
import integrador.prog2.entities.DetallePedido;
import integrador.prog2.entities.Pedido;
import integrador.prog2.entities.Producto;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;
import integrador.prog2.exception.BusinessException;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PedidoService {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public PedidoService() {
        this.usuarioService = new UsuarioService();
        this.productoService = new ProductoService();
    }

    public List<Pedido> listar() {
        return MemoriaDatos.PEDIDOS.stream()
                .filter(pedido -> !pedido.isEliminado())
                .sorted(Comparator.comparing(Pedido::getId))
                .toList();
    }

    public Pedido buscarPorId(Long id) {
        validarId(id);

        return MemoriaDatos.PEDIDOS.stream()
                .filter(pedido -> !pedido.isEliminado())
                .filter(pedido -> pedido.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Pedido", id));
    }

    public Pedido buscarPorIdConDetalles(Long id) {
        return buscarPorId(id);
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        validarId(usuarioId);

        usuarioService.buscarPorId(usuarioId);

        return MemoriaDatos.PEDIDOS.stream()
                .filter(pedido -> !pedido.isEliminado())
                .filter(pedido -> pedido.getUsuario() != null)
                .filter(pedido -> pedido.getUsuario().getId().equals(usuarioId))
                .sorted(Comparator.comparing(Pedido::getId))
                .toList();
    }

    public Pedido crear(Long usuarioId, FormaPago formaPago, Map<Long, Integer> productosCantidades) {
        validarId(usuarioId);
        validarFormaPago(formaPago);

        if (productosCantidades == null || productosCantidades.isEmpty()) {
            throw new ValidationException("El pedido debe tener al menos un producto.");
        }

        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        Pedido pedido = new Pedido(usuario, formaPago);
        pedido.setId(MemoriaDatos.siguienteIdPedido());

        for (Map.Entry<Long, Integer> item : productosCantidades.entrySet()) {
            Long productoId = item.getKey();
            Integer cantidad = item.getValue();

            validarId(productoId);
            validarCantidad(cantidad);

            Producto producto = productoService.buscarPorId(productoId);

            if (producto.getDisponible() == null || !producto.getDisponible()) {
                throw new BusinessException("El producto no esta disponible: " + producto.getNombre());
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

        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setId(MemoriaDatos.siguienteIdDetallePedido());
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
        }

        pedido.setTotal(pedido.calcularTotal());
        MemoriaDatos.PEDIDOS.add(pedido);

        return pedido;
    }

    public void actualizarEstado(Long pedidoId, Estado estado) {
        validarId(pedidoId);
        validarEstado(estado);

        Pedido pedido = buscarPorId(pedidoId);
        pedido.setEstado(estado);
    }

    public void actualizarFormaPago(Long pedidoId, FormaPago formaPago) {
        validarId(pedidoId);
        validarFormaPago(formaPago);

        Pedido pedido = buscarPorId(pedidoId);
        pedido.setFormaPago(formaPago);
    }

    public void eliminar(Long pedidoId) {
        validarId(pedidoId);

        Pedido pedido = buscarPorId(pedidoId);
        pedido.setEliminado(true);

        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setEliminado(true);
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id debe ser un numero mayor que cero.");
        }
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ValidationException("La cantidad debe ser mayor que cero.");
        }
    }

    private void validarFormaPago(FormaPago formaPago) {
        if (formaPago == null) {
            throw new ValidationException("Debe seleccionar una forma de pago valida.");
        }
    }

    private void validarEstado(Estado estado) {
        if (estado == null) {
            throw new ValidationException("Debe seleccionar un estado valido.");
        }
    }
}
