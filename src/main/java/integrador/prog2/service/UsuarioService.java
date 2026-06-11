package integrador.prog2.service;

import integrador.prog2.data.MemoriaDatos;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Rol;
import integrador.prog2.exception.BusinessException;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

import java.util.Comparator;
import java.util.List;

public class UsuarioService {

    public List<Usuario> listar() {
        return MemoriaDatos.USUARIOS.stream()
                .filter(usuario -> !usuario.isEliminado())
                .sorted(Comparator.comparing(Usuario::getId))
                .toList();
    }

    public Usuario buscarPorId(Long id) {
        validarId(id);

        return MemoriaDatos.USUARIOS.stream()
                .filter(usuario -> !usuario.isEliminado())
                .filter(usuario -> usuario.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Usuario", id));
    }

    public Usuario crear(
            String nombre,
            String apellido,
            String mail,
            String celular,
            String contrasena,
            Rol rol
    ) {
        validarNombre(nombre);
        validarApellido(apellido);
        validarMail(mail);
        validarContrasena(contrasena);
        validarRol(rol);

        String mailNormalizado = mail.trim().toLowerCase();

        if (existeMail(mailNormalizado)) {
            throw new BusinessException("Ya existe un usuario con ese mail.");
        }

        Usuario usuario = new Usuario(
                nombre.trim(),
                apellido.trim(),
                mailNormalizado,
                normalizarTextoOpcional(celular),
                contrasena.trim(),
                rol
        );
        usuario.setId(MemoriaDatos.siguienteIdUsuario());

        MemoriaDatos.USUARIOS.add(usuario);

        return usuario;
    }

    public Usuario actualizar(
            Long id,
            String nuevoNombre,
            String nuevoApellido,
            String nuevoMail,
            String nuevoCelular,
            String nuevaContrasena,
            Rol nuevoRol
    ) {
        validarId(id);

        Usuario usuario = buscarPorId(id);

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            usuario.setNombre(nuevoNombre.trim());
        }

        if (nuevoApellido != null && !nuevoApellido.trim().isEmpty()) {
            usuario.setApellido(nuevoApellido.trim());
        }

        if (nuevoMail != null && !nuevoMail.trim().isEmpty()) {
            validarMail(nuevoMail);

            String mailNormalizado = nuevoMail.trim().toLowerCase();

            if (existeMailEnOtroUsuario(mailNormalizado, id)) {
                throw new BusinessException("Ya existe otro usuario con ese mail.");
            }

            usuario.setMail(mailNormalizado);
        }

        if (nuevoCelular != null) {
            usuario.setCelular(normalizarTextoOpcional(nuevoCelular));
        }

        if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
            usuario.setContrasena(nuevaContrasena.trim());
        }

        if (nuevoRol != null) {
            usuario.setRol(nuevoRol);
        }

        return usuario;
    }

    public void eliminar(Long id) {
        validarId(id);

        Usuario usuario = buscarPorId(id);
        usuario.setEliminado(true);
    }

    public void validarMailDisponible(String mail) {
        validarMail(mail);

        String mailNormalizado = mail.trim().toLowerCase();

        if (existeMail(mailNormalizado)) {
            throw new BusinessException("Ya existe un usuario con ese mail.");
        }
    }

    public boolean tienePerfil(Long usuarioId) {
        validarId(usuarioId);

        Usuario usuario = buscarPorId(usuarioId);

        return usuario.getPerfilUsuario() != null && !usuario.getPerfilUsuario().isEliminado();
    }

    public void validarUsuarioSinPerfil(Long usuarioId) {
        validarId(usuarioId);

        Usuario usuario = buscarPorId(usuarioId);

        if (usuario.getPerfilUsuario() != null && !usuario.getPerfilUsuario().isEliminado()) {
            throw new BusinessException("El usuario ya tiene un perfil asociado.");
        }
    }

    private boolean existeMail(String mail) {
        return MemoriaDatos.USUARIOS.stream()
                .filter(usuario -> !usuario.isEliminado())
                .anyMatch(usuario -> usuario.getMail().equalsIgnoreCase(mail));
    }

    private boolean existeMailEnOtroUsuario(String mail, Long idUsuario) {
        return MemoriaDatos.USUARIOS.stream()
                .filter(usuario -> !usuario.isEliminado())
                .filter(usuario -> !usuario.getId().equals(idUsuario))
                .anyMatch(usuario -> usuario.getMail().equalsIgnoreCase(mail));
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id debe ser un numero mayor que cero.");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre del usuario no puede estar vacio.");
        }
    }

    private void validarApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ValidationException("El apellido del usuario no puede estar vacio.");
        }
    }

    private void validarMail(String mail) {
        if (mail == null || mail.trim().isEmpty()) {
            throw new ValidationException("El mail del usuario no puede estar vacio.");
        }

        String mailNormalizado = mail.trim();

        if (!mailNormalizado.contains("@") || !mailNormalizado.contains(".")) {
            throw new ValidationException("El mail ingresado no tiene un formato valido.");
        }
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new ValidationException("La contraseña no puede estar vacia.");
        }
    }

    private void validarRol(Rol rol) {
        if (rol == null) {
            throw new ValidationException("Debe seleccionar un rol valido.");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }
}
