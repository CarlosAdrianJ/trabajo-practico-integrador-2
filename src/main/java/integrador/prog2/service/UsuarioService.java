package integrador.prog2.service;

import integrador.prog2.dao.UsuarioDAO;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Rol;
import integrador.prog2.exception.BusinessException;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

import java.util.List;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> listar() {
        return usuarioDAO.listar();
    }

    public Usuario buscarPorId(Long id) {
        validarId(id);

        return usuarioDAO.buscarPorId(id)
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

        if (usuarioDAO.existeMail(mailNormalizado)) {
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

        return usuarioDAO.crear(usuario);
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

            if (usuarioDAO.existeMailEnOtroUsuario(mailNormalizado, id)) {
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

        return usuarioDAO.actualizar(usuario);
    }

    public void eliminar(Long id) {
        validarId(id);

        buscarPorId(id);

        usuarioDAO.eliminar(id);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id debe ser un número mayor que cero.");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre del usuario no puede estar vacío.");
        }
    }

    private void validarApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ValidationException("El apellido del usuario no puede estar vacío.");
        }
    }

    private void validarMail(String mail) {
        if (mail == null || mail.trim().isEmpty()) {
            throw new ValidationException("El mail del usuario no puede estar vacío.");
        }

        String mailNormalizado = mail.trim();

        if (!mailNormalizado.contains("@") || !mailNormalizado.contains(".")) {
            throw new ValidationException("El mail ingresado no tiene un formato válido.");
        }
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new ValidationException("La contraseña no puede estar vacía.");
        }
    }

    private void validarRol(Rol rol) {
        if (rol == null) {
            throw new ValidationException("Debe seleccionar un rol válido.");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }
    public void validarMailDisponible(String mail) {
        validarMail(mail);

        String mailNormalizado = mail.trim().toLowerCase();

        if (usuarioDAO.existeMail(mailNormalizado)) {
            throw new BusinessException("Ya existe un usuario con ese mail.");
        }
    }
}