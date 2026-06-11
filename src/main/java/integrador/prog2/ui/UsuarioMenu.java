package integrador.prog2.ui;

import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Rol;
import integrador.prog2.service.UsuarioService;

import java.util.List;

public class UsuarioMenu {

    private final LectorConsola lector;
    private final UsuarioService usuarioService;

    public UsuarioMenu(LectorConsola lector) {
        this.lector = lector;
        this.usuarioService = new UsuarioService();
    }

    public void mostrar() {
        int opcion;

        do {
            mostrarOpciones();
            opcion = lector.leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    ManejadorErroresConsola.ejecutar(this::listarUsuarios);
                    lector.pausar();
                    break;
                case 2:
                    ManejadorErroresConsola.ejecutar(this::crearUsuario);
                    lector.pausar();
                    break;
                case 3:
                    ManejadorErroresConsola.ejecutar(this::editarUsuario);
                    lector.pausar();
                    break;
                case 4:
                    ManejadorErroresConsola.ejecutar(this::eliminarUsuario);
                    lector.pausar();
                    break;
                case 0:
                    System.out.println("Volviendo al menu principal...");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente nuevamente.");
                    break;
            }

        } while (opcion != 0);
    }

    private void mostrarOpciones() {
        System.out.println();
        System.out.println("=== GESTION DE USUARIOS ===");
        System.out.println("1. Listar usuarios");
        System.out.println("2. Crear usuario");
        System.out.println("3. Editar usuario");
        System.out.println("4. Eliminar usuario");
        System.out.println("0. Volver");
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listar();

        System.out.println();
        System.out.println("=== LISTADO DE USUARIOS ===");

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios cargados.");
            return;
        }

        for (Usuario usuario : usuarios) {
            System.out.println("--------------------------------");
            mostrarUsuario(usuario);
        }

        System.out.println("--------------------------------");
    }

    private void crearUsuario() {
        System.out.println();
        System.out.println("=== CREAR USUARIO ===");

        String nombre = lector.leerTextoNoVacio("Nombre: ");
        String apellido = lector.leerTextoNoVacio("Apellido: ");
        String mail;

        while (true) {
            mail = lector.leerTextoNoVacio("Mail: ");

            try {
                usuarioService.validarMailDisponible(mail);
                break;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                System.out.println("Ingrese otro mail o escriba 0 para cancelar.");

                String opcion = lector.leerTexto("Continuar con otro mail: ");

                if ("0".equals(opcion.trim())) {
                    System.out.println("Operacion cancelada.");
                    return;
                }
            }
        }
        String celular = lector.leerTexto("Celular: ");
        String contrasena = lector.leerTextoNoVacio("Contraseña: ");
        Rol rol = leerRolObligatorio();

        Usuario usuarioCreado = usuarioService.crear(
                nombre,
                apellido,
                mail,
                celular,
                contrasena,
                rol
        );

        System.out.println();
        System.out.println("Usuario creado correctamente.");
        System.out.println("ID generado: " + usuarioCreado.getId());
    }

    private void editarUsuario() {
        System.out.println();
        System.out.println("=== EDITAR USUARIO ===");

        listarUsuarios();

        Long id = lector.leerLong("Ingrese el id del usuario a editar: ");

        Usuario usuarioActual = usuarioService.buscarPorId(id);

        System.out.println();
        System.out.println("Usuario actual:");
        mostrarUsuario(usuarioActual);

        System.out.println();
        System.out.println("Ingrese los nuevos datos. Presione ENTER para mantener el valor actual.");

        String nuevoNombre = lector.leerTexto("Nuevo nombre: ");
        String nuevoApellido = lector.leerTexto("Nuevo apellido: ");
        String nuevoMail = lector.leerTexto("Nuevo mail: ");
        String nuevoCelular = lector.leerTexto("Nuevo celular: ");
        String nuevaContrasena = lector.leerTexto("Nueva contraseña: ");
        Rol nuevoRol = leerRolOpcional();

        Usuario usuarioActualizado = usuarioService.actualizar(
                id,
                nuevoNombre,
                nuevoApellido,
                nuevoMail,
                nuevoCelular,
                nuevaContrasena,
                nuevoRol
        );

        System.out.println();
        System.out.println("Usuario actualizado correctamente.");
        mostrarUsuario(usuarioActualizado);
    }

    private void eliminarUsuario() {
        System.out.println();
        System.out.println("=== ELIMINAR USUARIO ===");

        listarUsuarios();

        Long id = lector.leerLong("Ingrese el id del usuario a eliminar: ");

        Usuario usuario = usuarioService.buscarPorId(id);

        System.out.println();
        System.out.println("Usuario seleccionado:");
        mostrarUsuario(usuario);

        boolean confirma = lector.confirmar("¿Confirma la eliminacion logica de este usuario?");

        if (!confirma) {
            System.out.println("Operacion cancelada.");
            return;
        }

        usuarioService.eliminar(id);

        System.out.println("Usuario eliminado correctamente.");
    }

    private Rol leerRolObligatorio() {
        while (true) {
            System.out.println();
            System.out.println("Seleccione rol:");
            System.out.println("1. ADMIN");
            System.out.println("2. USUARIO");

            int opcion = lector.leerEntero("Opcion: ");

            switch (opcion) {
                case 1:
                    return Rol.ADMIN;
                case 2:
                    return Rol.USUARIO;
                default:
                    System.out.println("Opcion invalida. Intente nuevamente.");
                    break;
            }
        }
    }

    private Rol leerRolOpcional() {
        System.out.println();
        System.out.println("Seleccione nuevo rol:");
        System.out.println("1. ADMIN");
        System.out.println("2. USUARIO");
        System.out.println("0. Mantener valor actual");

        int opcion = lector.leerEntero("Opcion: ");

        return switch (opcion) {
            case 1 -> Rol.ADMIN;
            case 2 -> Rol.USUARIO;
            case 0 -> null;
            default -> throw new IllegalArgumentException("Opcion invalida para rol.");
        };
    }

    private void mostrarUsuario(Usuario usuario) {
        System.out.println("ID: " + usuario.getId());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Apellido: " + usuario.getApellido());
        System.out.println("Mail: " + usuario.getMail());
        System.out.println("Celular: " + mostrarTexto(usuario.getCelular()));
        System.out.println("Rol: " + usuario.getRol());
        if (usuario.getPerfilUsuario() != null) {
            System.out.println("Perfil asociado: ID " + usuario.getPerfilUsuario().getId());
            System.out.println("DNI perfil: " + mostrarTexto(usuario.getPerfilUsuario().getDni()));
            System.out.println("Ciudad perfil: " + mostrarTexto(usuario.getPerfilUsuario().getCiudad()));
        } else {
            System.out.println("Perfil asociado: Sin perfil");
        }
    }

    private String mostrarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "-";
        }
        return texto;
    }
}