package integrador.prog2.service;

import integrador.prog2.data.MemoriaDatos;
import integrador.prog2.entities.PerfilUsuario;
import integrador.prog2.entities.Usuario;
import integrador.prog2.exception.BusinessException;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

import java.util.Comparator;
import java.util.List;

public class PerfilUsuarioService {

    private final UsuarioService usuarioService;

    public PerfilUsuarioService() {
        this.usuarioService = new UsuarioService();
    }

    public List<PerfilUsuario> listar() {
        return MemoriaDatos.PERFILES.stream()
                .filter(perfil -> !perfil.isEliminado())
                .sorted(Comparator.comparing(PerfilUsuario::getId))
                .toList();
    }

    public PerfilUsuario buscarPorId(Long id) {
        validarId(id);

        return MemoriaDatos.PERFILES.stream()
                .filter(perfil -> !perfil.isEliminado())
                .filter(perfil -> perfil.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Perfil de usuario", id));
    }

    public PerfilUsuario buscarPorDni(String dni) {
        validarDni(dni);

        String dniNormalizado = dni.trim();

        return MemoriaDatos.PERFILES.stream()
                .filter(perfil -> !perfil.isEliminado())
                .filter(perfil -> perfil.getDni().equalsIgnoreCase(dniNormalizado))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Perfil de usuario con DNI", null));
    }

    public PerfilUsuario crear(
            String direccion,
            String ciudad,
            String dni,
            String observaciones
    ) {
        validarDireccion(direccion);
        validarCiudad(ciudad);
        validarDni(dni);

        String dniNormalizado = dni.trim();

        if (existeDni(dniNormalizado)) {
            throw new BusinessException("Ya existe un perfil de usuario con ese DNI.");
        }

        PerfilUsuario perfil = new PerfilUsuario(
                direccion.trim(),
                ciudad.trim(),
                dniNormalizado,
                normalizarTextoOpcional(observaciones)
        );
        perfil.setId(MemoriaDatos.siguienteIdPerfil());

        MemoriaDatos.PERFILES.add(perfil);

        return perfil;
    }

    public PerfilUsuario crearYAsociarAUsuario(
            Long usuarioId,
            String direccion,
            String ciudad,
            String dni,
            String observaciones
    ) {
        validarId(usuarioId);
        validarDireccion(direccion);
        validarCiudad(ciudad);
        validarDni(dni);

        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        if (usuario.getPerfilUsuario() != null && !usuario.getPerfilUsuario().isEliminado()) {
            throw new BusinessException("El usuario ya tiene un perfil asociado.");
        }

        String dniNormalizado = dni.trim();

        if (existeDni(dniNormalizado)) {
            throw new BusinessException("Ya existe un perfil de usuario con ese DNI.");
        }

        PerfilUsuario perfil = new PerfilUsuario(
                direccion.trim(),
                ciudad.trim(),
                dniNormalizado,
                normalizarTextoOpcional(observaciones)
        );
        perfil.setId(MemoriaDatos.siguienteIdPerfil());

        MemoriaDatos.PERFILES.add(perfil);
        usuario.setPerfilUsuario(perfil);

        return perfil;
    }

    public PerfilUsuario actualizar(
            Long id,
            String nuevaDireccion,
            String nuevaCiudad,
            String nuevoDni,
            String nuevasObservaciones
    ) {
        validarId(id);

        PerfilUsuario perfil = buscarPorId(id);

        if (nuevaDireccion != null && !nuevaDireccion.trim().isEmpty()) {
            perfil.setDireccion(nuevaDireccion.trim());
        }

        if (nuevaCiudad != null && !nuevaCiudad.trim().isEmpty()) {
            perfil.setCiudad(nuevaCiudad.trim());
        }

        if (nuevoDni != null && !nuevoDni.trim().isEmpty()) {
            validarDni(nuevoDni);

            String dniNormalizado = nuevoDni.trim();

            if (existeDniEnOtroPerfil(dniNormalizado, id)) {
                throw new BusinessException("Ya existe otro perfil de usuario con ese DNI.");
            }

            perfil.setDni(dniNormalizado);
        }

        if (nuevasObservaciones != null) {
            perfil.setObservaciones(normalizarTextoOpcional(nuevasObservaciones));
        }

        return perfil;
    }

    public void eliminar(Long id) {
        validarId(id);

        PerfilUsuario perfil = buscarPorId(id);
        perfil.setEliminado(true);

        MemoriaDatos.USUARIOS.stream()
                .filter(usuario -> usuario.getPerfilUsuario() != null)
                .filter(usuario -> usuario.getPerfilUsuario().getId().equals(id))
                .forEach(usuario -> usuario.setPerfilUsuario(null));
    }

    public boolean existeDni(String dni) {
        validarDni(dni);

        String dniNormalizado = dni.trim();

        return MemoriaDatos.PERFILES.stream()
                .filter(perfil -> !perfil.isEliminado())
                .anyMatch(perfil -> perfil.getDni().equalsIgnoreCase(dniNormalizado));
    }

    private boolean existeDniEnOtroPerfil(String dni, Long idPerfil) {
        return MemoriaDatos.PERFILES.stream()
                .filter(perfil -> !perfil.isEliminado())
                .filter(perfil -> !perfil.getId().equals(idPerfil))
                .anyMatch(perfil -> perfil.getDni().equalsIgnoreCase(dni));
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id debe ser un numero mayor que cero.");
        }
    }

    private void validarDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new ValidationException("La direccion del perfil no puede estar vacia.");
        }
    }

    private void validarCiudad(String ciudad) {
        if (ciudad == null || ciudad.trim().isEmpty()) {
            throw new ValidationException("La ciudad del perfil no puede estar vacia.");
        }
    }

    private void validarDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new ValidationException("El DNI del perfil no puede estar vacio.");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }
}
