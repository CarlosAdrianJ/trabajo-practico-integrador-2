package integrador.prog2.data;

import integrador.prog2.entities.Categoria;
import integrador.prog2.entities.DetallePedido;
import integrador.prog2.entities.Pedido;
import integrador.prog2.entities.PerfilUsuario;
import integrador.prog2.entities.Producto;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.FormaPago;
import integrador.prog2.enums.Rol;

import java.util.ArrayList;
import java.util.List;

public final class MemoriaDatos {

    public static final List<Categoria> CATEGORIAS = new ArrayList<>();
    public static final List<Producto> PRODUCTOS = new ArrayList<>();
    public static final List<Usuario> USUARIOS = new ArrayList<>();
    public static final List<PerfilUsuario> PERFILES = new ArrayList<>();
    public static final List<Pedido> PEDIDOS = new ArrayList<>();

    private static Long secuenciaCategoria = 1L;
    private static Long secuenciaProducto = 1L;
    private static Long secuenciaUsuario = 1L;
    private static Long secuenciaPerfil = 1L;
    private static Long secuenciaPedido = 1L;
    private static Long secuenciaDetallePedido = 1L;

    static {
        cargarDatosIniciales();
    }

    private MemoriaDatos() {
    }

    public static Long siguienteIdCategoria() {
        return secuenciaCategoria++;
    }

    public static Long siguienteIdProducto() {
        return secuenciaProducto++;
    }

    public static Long siguienteIdUsuario() {
        return secuenciaUsuario++;
    }

    public static Long siguienteIdPerfil() {
        return secuenciaPerfil++;
    }

    public static Long siguienteIdPedido() {
        return secuenciaPedido++;
    }

    public static Long siguienteIdDetallePedido() {
        return secuenciaDetallePedido++;
    }

    private static void cargarDatosIniciales() {
        Categoria bebidas = new Categoria("Bebidas", "Bebidas frias y calientes");
        bebidas.setId(siguienteIdCategoria());

        Categoria comidas = new Categoria("Comidas", "Platos principales y comidas rapidas");
        comidas.setId(siguienteIdCategoria());

        Categoria postres = new Categoria("Postres", "Opciones dulces para despues de comer");
        postres.setId(siguienteIdCategoria());

        CATEGORIAS.add(bebidas);
        CATEGORIAS.add(comidas);
        CATEGORIAS.add(postres);

        Producto agua = new Producto(
                "Agua mineral",
                900.0,
                "Botella de agua sin gas",
                30,
                null,
                true,
                bebidas
        );
        agua.setId(siguienteIdProducto());

        Producto hamburguesa = new Producto(
                "Hamburguesa completa",
                4500.0,
                "Hamburguesa con queso, lechuga y tomate",
                15,
                null,
                true,
                comidas
        );
        hamburguesa.setId(siguienteIdProducto());

        Producto torta = new Producto(
                "Porcion de torta",
                2500.0,
                "Porcion individual de torta",
                10,
                null,
                true,
                postres
        );
        torta.setId(siguienteIdProducto());

        PRODUCTOS.add(agua);
        PRODUCTOS.add(hamburguesa);
        PRODUCTOS.add(torta);

        Usuario admin = new Usuario(
                "Admin",
                "Sistema",
                "admin@foodstore.com",
                "2610000000",
                "admin123",
                Rol.ADMIN
        );
        admin.setId(siguienteIdUsuario());

        Usuario cliente = new Usuario(
                "Juan",
                "Perez",
                "juan.perez@mail.com",
                "2611111111",
                "1234",
                Rol.USUARIO
        );
        cliente.setId(siguienteIdUsuario());

        USUARIOS.add(admin);
        USUARIOS.add(cliente);

        PerfilUsuario perfilCliente = new PerfilUsuario(
                "Av. San Martin 123",
                "Rosario",
                "30111222",
                "Cliente precargado"
        );
        perfilCliente.setId(siguienteIdPerfil());
        cliente.setPerfilUsuario(perfilCliente);
        PERFILES.add(perfilCliente);

        Pedido pedido = new Pedido(cliente, FormaPago.EFECTIVO);
        pedido.setId(siguienteIdPedido());
        pedido.addDetallePedido(2, agua.getPrecio(), agua);
        pedido.addDetallePedido(1, hamburguesa.getPrecio(), hamburguesa);

        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setId(siguienteIdDetallePedido());
        }

        pedido.setTotal(pedido.calcularTotal());
        PEDIDOS.add(pedido);

        agua.setStock(agua.getStock() - 2);
        hamburguesa.setStock(hamburguesa.getStock() - 1);
    }
}
