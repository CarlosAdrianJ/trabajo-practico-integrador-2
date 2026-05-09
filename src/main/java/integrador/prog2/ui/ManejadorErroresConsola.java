package integrador.prog2.ui;

import integrador.prog2.exception.BusinessException;
import integrador.prog2.exception.EntityNotFoundException;
import integrador.prog2.exception.ValidationException;

public class ManejadorErroresConsola {

    private ManejadorErroresConsola() {
    }

    public static void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (ValidationException e) {
            mostrarError("Error de validación", e.getMessage());
        } catch (EntityNotFoundException e) {
            mostrarError("Registro no encontrado", e.getMessage());
        } catch (BusinessException e) {
            mostrarError("Error del sistema", e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarError("Entrada inválida", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado", "Ocurrió un error no previsto.");
        }
    }

    private static void mostrarError(String titulo, String mensaje) {
        System.out.println();
        System.out.println("[" + titulo + "]");
        System.out.println(mensaje);
    }
}