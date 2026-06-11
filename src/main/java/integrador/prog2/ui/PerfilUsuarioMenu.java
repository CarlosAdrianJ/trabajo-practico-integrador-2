package integrador.prog2.ui;

import integrador.prog2.entities.PerfilUsuario;
import integrador.prog2.entities.Usuario;
import integrador.prog2.service.PerfilUsuarioService;
import integrador.prog2.service.UsuarioService;

import java.util.List;

public class PerfilUsuarioMenu {

    private final LectorConsola lector;
    private final PerfilUsuarioService perfilUsuarioService;
    private final UsuarioService usuarioService;

    public PerfilUsuarioMenu(LectorConsola lector) {
        this.lector = lector;
        this.perfilUsuarioService = new PerfilUsuarioService();
        this.usuarioService = new UsuarioService();
    }

    public void mostrar() {
        int opcion;

        do {
            mostrarOpciones();
            opcion = lector.leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    ManejadorErroresConsola.ejecutar(this::listarPerfiles);
                    lector.pausar();
                    break;
                case 2:
                    ManejadorErroresConsola.ejecutar(this::buscarPerfilPorDni);
                    lector.pausar();
                    break;
                case 3:
                    ManejadorErroresConsola.ejecutar(this::crearPerfilYAsociarAUsuario);
                    lector.pausar();
                    break;
                case 4:
                    ManejadorErroresConsola.ejecutar(this::editarPerfil);
                    lector.pausar();
                    break;
                case 5:
                    ManejadorErroresConsola.ejecutar(this::eliminarPerfil);
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
        System.out.println("=== GESTION DE PERFILES DE USUARIO ===");
        System.out.println("1. Listar perfiles");
        System.out.println("2. Buscar perfil por DNI");
        System.out.println("3. Crear perfil y asociar a usuario");
        System.out.println("4. Editar perfil");
        System.out.println("5. Eliminar perfil");
        System.out.println("0. Volver");
    }

    private void listarPerfiles() {
        List<PerfilUsuario> perfiles = perfilUsuarioService.listar();

        System.out.println();
        System.out.println("=== LISTADO DE PERFILES DE USUARIO ===");

        if (perfiles.isEmpty()) {
            System.out.println("No hay perfiles cargados.");
            return;
        }

        for (PerfilUsuario perfil : perfiles) {
            System.out.println("--------------------------------");
            mostrarPerfil(perfil);
        }

        System.out.println("--------------------------------");
    }

    private void buscarPerfilPorDni() {
        System.out.println();
        System.out.println("=== BUSCAR PERFIL POR DNI ===");

        String dni = lector.leerTextoNoVacio("Ingrese el DNI: ");

        PerfilUsuario perfil = perfilUsuarioService.buscarPorDni(dni);

        System.out.println();
        System.out.println("Perfil encontrado:");
        mostrarPerfil(perfil);
    }

    private void crearPerfilYAsociarAUsuario() {
        System.out.println();
        System.out.println("=== CREAR PERFIL Y ASOCIAR A USUARIO ===");

        listarUsuarios();

        Long usuarioId = lector.leerLong("Ingrese el ID del usuario al que desea asociar el perfil: ");

        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        System.out.println();
        System.out.println("Usuario seleccionado:");
        mostrarUsuarioResumen(usuario);

        String direccion = lector.leerTextoNoVacio("Direccion: ");
        String ciudad = lector.leerTextoNoVacio("Ciudad: ");
        String dni = leerDniDisponibleParaCrear();
        String observaciones = lector.leerTexto("Observaciones: ");

        PerfilUsuario perfilCreado = perfilUsuarioService.crearYAsociarAUsuario(
                usuarioId,
                direccion,
                ciudad,
                dni,
                observaciones
        );

        System.out.println();
        System.out.println("Perfil creado y asociado correctamente.");
        System.out.println("ID generado: " + perfilCreado.getId());
        mostrarPerfil(perfilCreado);
    }

    private void editarPerfil() {
        System.out.println();
        System.out.println("=== EDITAR PERFIL DE USUARIO ===");

        listarPerfiles();

        Long id = lector.leerLong("Ingrese el ID del perfil a editar: ");

        PerfilUsuario perfilActual = perfilUsuarioService.buscarPorId(id);

        System.out.println();
        System.out.println("Perfil actual:");
        mostrarPerfil(perfilActual);

        System.out.println();
        System.out.println("Ingrese los nuevos datos. Presione ENTER para mantener el valor actual.");

        String nuevaDireccion = lector.leerTexto("Nueva direccion: ");
        String nuevaCiudad = lector.leerTexto("Nueva ciudad: ");
        String nuevoDni = lector.leerTexto("Nuevo DNI: ");
        String nuevasObservaciones = lector.leerTexto("Nuevas observaciones: ");

        PerfilUsuario perfilActualizado = perfilUsuarioService.actualizar(
                id,
                nuevaDireccion,
                nuevaCiudad,
                nuevoDni,
                nuevasObservaciones
        );

        System.out.println();
        System.out.println("Perfil actualizado correctamente.");
        mostrarPerfil(perfilActualizado);
    }

    private void eliminarPerfil() {
        System.out.println();
        System.out.println("=== ELIMINAR PERFIL DE USUARIO ===");

        listarPerfiles();

        Long id = lector.leerLong("Ingrese el ID del perfil a eliminar: ");

        PerfilUsuario perfil = perfilUsuarioService.buscarPorId(id);

        System.out.println();
        System.out.println("Perfil seleccionado:");
        mostrarPerfil(perfil);

        boolean confirma = lector.confirmar("¿Confirma la eliminacion logica de este perfil?");

        if (!confirma) {
            System.out.println("Operacion cancelada.");
            return;
        }

        perfilUsuarioService.eliminar(id);

        System.out.println("Perfil eliminado correctamente.");
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listar();

        System.out.println();
        System.out.println("=== USUARIOS DISPONIBLES ===");

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios cargados.");
            return;
        }

        for (Usuario usuario : usuarios) {
            System.out.println("--------------------------------");
            mostrarUsuarioResumen(usuario);
        }

        System.out.println("--------------------------------");
    }

    private String leerDniDisponibleParaCrear() {
        while (true) {
            String dni = lector.leerTextoNoVacio("DNI: ");

            try {
                if (perfilUsuarioService.existeDni(dni)) {
                    System.out.println("Ya existe un perfil con ese DNI.");
                    System.out.println("Ingrese otro DNI o escriba 0 para cancelar.");

                    String opcion = lector.leerTexto("Continuar con otro DNI: ");

                    if ("0".equals(opcion.trim())) {
                        throw new IllegalArgumentException("Operacion cancelada por el usuario.");
                    }

                } else {
                    return dni;
                }

            } catch (RuntimeException e) {
                if ("0".equals(dni.trim())) {
                    throw new IllegalArgumentException("Operacion cancelada por el usuario.");
                }

                throw e;
            }
        }
    }

    private void mostrarPerfil(PerfilUsuario perfil) {
        System.out.println("ID: " + perfil.getId());
        System.out.println("Direccion: " + mostrarTexto(perfil.getDireccion()));
        System.out.println("Ciudad: " + mostrarTexto(perfil.getCiudad()));
        System.out.println("DNI: " + mostrarTexto(perfil.getDni()));
        System.out.println("Observaciones: " + mostrarTexto(perfil.getObservaciones()));
    }

    private void mostrarUsuarioResumen(Usuario usuario) {
        System.out.println("ID: " + usuario.getId());
        System.out.println("Nombre: " + usuario.getNombre() + " " + usuario.getApellido());
        System.out.println("Mail: " + usuario.getMail());

        if (usuario.getPerfilUsuario() != null) {
            System.out.println("Perfil asociado: ID " + usuario.getPerfilUsuario().getId());
            System.out.println("DNI perfil: " + mostrarTexto(usuario.getPerfilUsuario().getDni()));
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