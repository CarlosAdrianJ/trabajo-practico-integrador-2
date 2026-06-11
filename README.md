# Sistema de Pedidos Food Store

Proyecto integrador de **Programación II** desarrollado en **Java**.

Food Store es una aplicación de consola para administrar categorías, productos, usuarios, perfiles y pedidos.  
El sistema funciona con **colecciones en memoria** y datos iniciales precargados en el código.

## Tecnologías utilizadas

- Java 21
- Programación orientada a objetos
- Colecciones Java
- Aplicación de consola
- Manejo de excepciones personalizadas

## Estructura del proyecto

```txt
java/
└── integrador/
    └── prog2/
        ├── Main.java
        ├── data/
        │   └── MemoriaDatos.java
        ├── entities/
        ├── enums/
        ├── exception/
        ├── service/
        └── ui/
```

## Funcionamiento general

La aplicación está organizada por capas:

```txt
UI -> Service -> Data -> Entities
```

- `ui`: contiene los menús de consola.
- `service`: contiene la lógica de negocio y validaciones.
- `data`: contiene las colecciones y los datos precargados.
- `entities`: contiene las clases principales del sistema.
- `enums`: contiene roles, estados y formas de pago.
- `exception`: contiene excepciones personalizadas.

## Datos en memoria

Los datos se almacenan en listas dentro de:

```txt
java/integrador/prog2/data/MemoriaDatos.java
```

Al iniciar el programa se cargan datos de ejemplo:

- Categorías.
- Productos.
- Usuarios.
- Perfiles de usuario.
- Pedidos.

Los cambios realizados durante la ejecución existen solo mientras el programa está abierto.  
Al cerrar y volver a ejecutar, se cargan nuevamente los datos iniciales.

## Funcionalidades principales

### Categorías

- Crear categoría.
- Listar categorías.
- Buscar por ID.
- Actualizar categoría.
- Eliminar mediante baja lógica.

### Productos

- Crear producto.
- Listar productos.
- Buscar por ID.
- Actualizar producto.
- Eliminar mediante baja lógica.
- Controlar stock y disponibilidad.
- Asociar producto a una categoría.

### Usuarios

- Crear usuario.
- Listar usuarios.
- Buscar por ID.
- Actualizar usuario.
- Eliminar mediante baja lógica.
- Asignar rol.

### Perfiles de usuario

- Crear perfil.
- Consultar perfil.
- Actualizar perfil.
- Asociar perfil a un usuario.

### Pedidos

- Crear pedido.
- Agregar productos al pedido.
- Calcular total.
- Descontar stock.
- Consultar pedidos.
- Actualizar estado.
- Registrar forma de pago.

## Baja lógica

El sistema no elimina físicamente los objetos de las listas.  
Cuando se elimina un registro, se marca como:

```java
eliminado = true;
```

Luego, los listados y búsquedas principales ignoran los elementos eliminados.

## Requisitos

- Java 21 o superior.
- No requiere base de datos.
- No requiere configuración externa.

## Ejecución desde IntelliJ IDEA

1. Abrir IntelliJ IDEA.
2. Seleccionar `Open`.
3. Abrir la carpeta del proyecto.
4. Verificar que el SDK sea Java 21 o superior.
5. Ejecutar el archivo:

```txt
java/integrador/prog2/Main.java
```

## Ejecución desde terminal

Desde la raíz del proyecto:

```bash
mkdir -p out
javac -d out $(find java -name "*.java")
java -cp out integrador.prog2.Main
```

## Ejecución desde Windows PowerShell

Desde la raíz del proyecto:

```powershell
mkdir out
javac -d out (Get-ChildItem -Recurse -Filter *.java -Path .\java).FullName
java -cp out integrador.prog2.Main
```

## Menú principal

Al ejecutar el sistema se muestra:

```txt
=== SISTEMA DE PEDIDOS FOOD STORE ===
1. Categorias
2. Productos
3. Usuarios
4. Pedidos
5. Perfiles de usuario
0. Salir
```

## Modificar datos iniciales

Para cambiar los datos precargados, editar:

```txt
java/integrador/prog2/data/MemoriaDatos.java
```

Dentro del método:

```java
private static void cargarDatosIniciales()
```

Ahí se pueden modificar o agregar categorías, productos, usuarios, perfiles y pedidos iniciales.
