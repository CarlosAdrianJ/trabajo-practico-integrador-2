# Food Store Console

Sistema de gestión de pedidos de comida desarrollado como Trabajo Práctico Integrador de Programación 2.

El proyecto consiste en una aplicación de consola desarrollada en Java 21, utilizando Programación Orientada a Objetos, JDBC puro, patrón DAO, capa de servicios y base de datos MySQL.

## Tecnologías utilizadas

- Java 21
- Gradle
- MySQL
- MySQL Workbench
- JDBC
- IntelliJ IDEA

## Base de datos

El sistema utiliza una base de datos MySQL llamada:

```sql
basedatos_tpi2
```

Antes de ejecutar el proyecto, se debe crear la base de datos y las tablas ejecutando el archivo:

```text
schema.sql
```

Este archivo se encuentra en la raíz del proyecto.

## Configuración de conexión

La conexión a la base de datos se encuentra centralizada en la clase:

```text
src/main/java/integrador/prog2/config/ConexionDB.java
```

En esa clase se configuran los siguientes datos:

```java
private static final String URL = "jdbc:mysql://localhost:3306/basedatos_tpi2";
private static final String USUARIO = "usuario_tpi2";
private static final String PASSWORD = "Tpi2_2026!";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

Si el usuario o la contraseña de MySQL son diferentes, deben modificarse en esa clase antes de ejecutar el programa.

## Cómo ejecutar el proyecto desde IntelliJ IDEA

1. Abrir IntelliJ IDEA.

2. Seleccionar la opción:

```text
Open
```

3. Elegir la carpeta raíz del proyecto.

4. Esperar a que IntelliJ cargue el proyecto Gradle.

5. Verificar que el proyecto esté usando Java 21.

Para hacerlo:

```text
File > Project Structure > Project SDK
```

Debe estar seleccionado:

```text
Java 21
```

6. Verificar que MySQL esté iniciado.

Abrir MySQL Workbench y comprobar que exista la base de datos:

```sql
basedatos_tpi2
```

7. Ejecutar el archivo `schema.sql` desde MySQL Workbench para crear las tablas y cargar los datos iniciales.

8. En IntelliJ, abrir la clase:

```text
src/main/java/integrador/prog2/Main.java
```

9. Ejecutar el método `main`.

Se puede hacer clic en el botón verde que aparece al lado de:

```java
public static void main(String[] args)
```

10. Al iniciar, el sistema mostrará el menú principal en consola:

```text
=== SISTEMA DE PEDIDOS FOOD STORE ===
1. Categorías
2. Productos
3. Usuarios
4. Pedidos
0. Salir
```

Desde ese menú se puede acceder a la gestión de categorías, productos, usuarios y pedidos.

## Funcionalidades principales

El sistema permite:

- Crear, listar, editar y eliminar lógicamente categorías.
- Crear, listar, editar y eliminar lógicamente productos.
- Asociar productos a categorías.
- Crear, listar, editar y eliminar lógicamente usuarios.
- Crear pedidos con detalles.
- Asociar pedidos a usuarios.
- Asociar detalles de pedido a productos.
- Calcular subtotales y total del pedido.
- Actualizar estado del pedido.
- Actualizar forma de pago.
- Descontar stock al crear pedidos.
- Persistir toda la información en MySQL.

## Observaciones

El sistema utiliza baja lógica mediante el campo:

```text
eliminado
```

Por lo tanto, los registros no se eliminan físicamente de la base de datos, sino que se marcan como eliminados para conservar el historial.

## Ejecución esperada

Si la base de datos está creada correctamente y la conexión está bien configurada, el proyecto debe ejecutarse desde IntelliJ sin errores y mostrar el menú principal en consola.
