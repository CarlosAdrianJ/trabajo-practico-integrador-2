package integrador.prog2.ui;

import java.util.Scanner;

public class LectorConsola {

    private final Scanner scanner;

    public LectorConsola(Scanner scanner) {
        this.scanner = scanner;
    }

    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);

            if (scanner.hasNextInt()) {
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            }

            System.out.println("Debe ingresar un numero entero valido.");
            scanner.nextLine();
        }
    }

    public Long leerLong(String mensaje) {
        while (true) {
            System.out.print(mensaje);

            if (scanner.hasNextLong()) {
                Long valor = scanner.nextLong();
                scanner.nextLine();
                return valor;
            }

            System.out.println("Debe ingresar un numero valido.");
            scanner.nextLine();
        }
    }

    public Double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);

            if (scanner.hasNextDouble()) {
                Double valor = scanner.nextDouble();
                scanner.nextLine();
                return valor;
            }

            System.out.println("Debe ingresar un número decimal valido.");
            scanner.nextLine();
        }
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    public String leerTextoNoVacio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine();

            if (texto != null && !texto.trim().isEmpty()) {
                return texto.trim();
            }

            System.out.println("El texto no puede estar vacío.");
        }
    }

    public boolean confirmar(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (S/N): ");
            String respuesta = scanner.nextLine().trim().toUpperCase();

            if (respuesta.equals("S")) {
                return true;
            }

            if (respuesta.equals("N")) {
                return false;
            }

            System.out.println("Debe ingresar S para confirmar o N para cancelar.");
        }
    }

    public void pausar() {
        System.out.println();
        System.out.print("Presione ENTER para continuar...");
        scanner.nextLine();
    }
}