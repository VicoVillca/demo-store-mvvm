# Android Compose MVVM con Room e Importación a través de Excel

Este es un proyecto base para Android desarrollado íntegramente de manera declarativa con **Jetpack Compose** bajo la arquitectura **MVVM (Model-View-ViewModel)**. 

La aplicación permite la visualización de un registro de productos y proporciona una funcionalidad para la carga masiva y actualización de registros (Productos y Categorías) directamente desde archivos `.xls` o `.xlsx` escaneados en almacenamiento local, validados desde la versión mínima solicitada (**API Nivel 26 - Android 8.0 Oreo**).

## 🚀 Características Principales

1. **Arquitectura MVVM**: Separación estricta de las reglas y estructura mediante `ViewModel` y Corrutinas, protegiendo a la interfaz (`ProductsScreen`) de manejar conversiones engorrosas y estados directamente.
2. **Room Database**: Los datos persistentes son guardados localmente haciendo uso de una capa robusta con inserciones asíncronas no bloqueantes.
    - Se contemplan dos Tablas relacionales lógicas: `Product` y `Category`.
    - La inserción usa `OnConflictStrategy.REPLACE`, lo cual garantiza eficacia al registrar productos o importarlos repetitivamente (actualizará el producto de forma automática si este comparte ID y atributos).
3. **Lectura Nivel 26 con Android SAF**: Utiliza `ActivityResultContracts.OpenDocumentTree()` y `DocumentFile` (nativo de AndroidX) permitiendo la selección visual de carpetas donde residen hojas de cálculo. No requiere dependencias extremas que rompan sistemas antiguos. 
4. **Apache POI**: Componente central (`ExcelHelper`) encargado de leer ambos tipos de documento (`.xls` y `.xlsx`). Convierte las grillas (`Sheets`) a objetos `Kotlin Data Classes`.

## 🗃️ Estructura del Archivo de Plantilla Excel

El archivo Excel debe contener **estrictamente** las siguientes dos hojas (ten cuidado con las mayúsculas/minúsculas de la etiqueta de la hoja):

### Hoja 1: `Datos`
Debe englobar las propiedades principales del producto. El asistente leerá a partir del orden de estas columnas (y asume que la **Fila 0 / Fila 1 es el Encabezado**):
- Columna 1 (0): `Código` (String/Long)
- Columna 2 (1): `Nombre` (String)
- Columna 3 (2): `Precio` (Numérico / Double)
- Columna 4 (3): `Stock` (Entero)
- Columna 5 (4): `ID de Categoría` (Entero)

### Hoja 2: `Categorias`
Aquí se define el mapeo para poder ligar un modelo relacional de las categorías.
- Columna 1 (0): `ID` (Entero)
- Columna 2 (1): `Descripción` (String)

## 🛠️ Tecnologías y Versiones Utilizadas

- **Min SDK:** 26 (Android 8.0). Garantiza la ejecución óptima sin penalizar ni forzar métodos inseguros o parches no oficiales.
- **Compose Tooling BoM**: `2024.09.00`.
- **Room Component**: `2.6.1` manejado bajo *KSP (Kotlin Symbol Processing)* y `ksp-2.0.21` para mayor rendimiento de compilación cruzada.
- **Coroutines & Flow**: Manejo de eventos de carga y mensajes por parte del `StateFlow`.
- **Apache POI**: `org.apache.poi:poi:5.2.3` / `poi-ooxml:5.2.3`.

## 📦 Cómo Correr o Instalar el Proyecto

1. Clona/Abre el repositorio a través de Android Studio. *(Este código cuenta con Gradle 8.13).*
2. Asegúrate de tener seleccionado y creado un emulador virtual (AVD) de al menos API 26 o bien usar un celular físico a través de cable / Wireless Debugging.
3. Arroja una plantilla válida de Excel dentro de la ruta que prefieras en el dispositivo (ej. carpeta `Descargas` o `Documentos`).
4. Pulsa el botón "Play" (`run app`) en tu IDE y dale permisos desde el dispositivo cuando lo solicite.

> ✨ ¡Sientete libre de bifurcar, clonar o escalar esta plantilla! Cuentas con todo lo que necesitas para construir una gestión robusta.