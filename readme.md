# Sistema de Gestión de Tienda (Experimental)

Una sencilla aplicación de escritorio para gestionar el inventario de una tienda, construida con un cliente JavaFX y un backend en Node.js (Express) conectado a una base de datos MySQL.

> **Estado:** Este proyecto se encuentra actualmente en fase experimental. La funcionalidad principal para gestionar productos está implementada, pero las características para pedidos, clientes y proveedores aún están en desarrollo.

---

## Tecnologías Utilizadas

*   **Backend**: Node.js, Express.js, MySQL2
*   **Frontend**: Java, JavaFX, Maven
*   **Base de datos**: MySQL

---

## Prerrequisitos

Antes de comenzar, asegúrate de tener lo siguiente instalado en tu sistema:

*   [Node.js](https://nodejs.org/) (que incluye npm)
*   [Java Development Kit (JDK)](https://adoptium.net/) (Versión 21 o superior)
*   [Apache Maven](https://maven.apache.org/download.cgi)
*   Una instancia en ejecución del servidor [MySQL](https://www.mysql.com/)

---

## Configuración e Instalación

Sigue estos pasos para configurar el proyecto localmente después de clonar el repositorio.

### 1. Configuración del Backend (`node-backend`)

Primero, configura el servidor Node.js que proporciona la API para la aplicación.

1.  **Navega al directorio del backend:**
    ```bash
    cd node-backend
    ```

2.  **Crea el archivo de entorno:**
    Crea un archivo llamado `.env` en la raíz de `node-backend`. Este archivo almacenará tus credenciales de la base de datos.

    ```
    # .env

    # Configuración del Servidor
    PORT=3000

    # Configuración de la Base de Datos
    DB_HOST=localhost
    DB_USER=tu_usuario_db
    DB_PASSWORD=tu_contraseña_db
    DB_NAME=tu_nombre_db
    ```

3.  **Instala las dependencias:**
    ```bash
    npm install
    ```

### 2. Configuración del Frontend (`javafx-client`)

El frontend es un proyecto de Maven. Todas las dependencias se descargarán automáticamente la primera vez que ejecutes la aplicación. No se requiere ninguna configuración adicional.

---

## Ejecutar la Aplicación

Necesitas ejecutar tanto el backend como el frontend simultáneamente en dos terminales separadas.

### 1. Iniciar el Servidor Backend

En tu primera terminal, navega al directorio del backend y ejecuta:

```bash
cd node-backend
npm run dev
```
La API se estará ejecutando en `http://localhost:3000`. Mantén esta terminal abierta.

### 2. Iniciar la Interfaz Gráfica del Frontend

En una **terminal nueva y separada**, navega al directorio del frontend y ejecuta:

```bash
cd javafx-client
mvn javafx:run
```
La ventana de la aplicación JavaFX se iniciará y se conectará al backend en ejecución.