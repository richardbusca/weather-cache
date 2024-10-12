# Documentación de la API de Meteorología

## Descripción General
La API de Meteorología está diseñada para recuperar y almacenar datos meteorológicos para ubicaciones geográficas predefinidas en una base de datos Redis. La API obtiene información meteorológica cada 5 minutos de un servicio meteorológico externo y proporciona una interfaz RESTful para acceder a estos datos.

## Características
- **Recuperación Programada de Datos**: Cada 5 minutos, la API obtiene datos meteorológicos para todas las ubicaciones registradas y los almacena en Redis.
- **Acceso a Datos**: Los usuarios pueden recuperar la información meteorológica para una ubicación específica a través de una solicitud GET.

## Arquitectura
La API está construida utilizando Ktor y sigue una arquitectura modular:
- **Tareas Programadas**: Implementadas utilizando la clase `ScheduledTask`, responsable de la recuperación periódica de datos.
- **Servicio Meteorológico**: Interactúa con la API meteorológica externa para obtener datos del clima.
- **Servicio Redis**: Maneja el almacenamiento en caché de los datos meteorológicos en Redis.
- **Registro de Tareas**: Administra las tareas programadas para su ejecución periódica.

## Endpoints de la API

### GET /weather/{location}
Recupera la información meteorológica almacenada para la ubicación especificada.

#### Parámetros
- `location` (string): El nombre de la ubicación geográfica (por ejemplo, "USA", "UK").

  **Localidades aceptadas**: CL (Chile), CH (Suiza), NZ (Nueva Zelanda), AU (Australia), UK (Reino Unido), USA (Estados Unidos).

  **Nota**: Si se envía una ubicación que no esté en la lista de localidades aceptadas, la API devolverá un error 400.

#### Respuestas
- **200 OK**: Datos meteorológicos recuperados exitosamente.
    - **Cuerpo de la Respuesta**: Un objeto JSON que contiene información meteorológica.

- **404 Not Found**: Los datos de la ubicación solicitada no están disponibles en la caché.
    - **Cuerpo de la Respuesta**: Un mensaje de error indicando que la ubicación no fue encontrada.

- **400 Bad Request**: La ubicación solicitada no es válida.
  - **Cuerpo de la Respuesta**: Un mensaje de error indicando que la ubicación no es aceptada.

#### Ejemplo de Solicitud
```
GET /weather/USA
```

#### Ejemplo de Respuesta
```json
{
  "precipitationIntensity": 1,
  "temperature": 23,
  "windSpeed": 6.31
}
```

## Programación de Tareas
Los datos meteorológicos se actualizan cada 5 minutos. La programación se gestiona a través de la clase `WeatherScheduledTask`, que implementa la clase abstracta `ScheduledTask`.

### Configuración
- **Retraso Inicial**: Configurado a través de la configuración de la aplicación para especificar cuánto tiempo esperar antes de la primera ejecución de la tarea.
- **Periodo de Ejecución**: Configurado para repetirse cada 5 minutos (600,000 milisegundos).

### Manejo de Errores
La aplicación maneja excepciones en tiempo de ejecución durante la obtención de datos y registra mensajes de error para la solución de problemas.

## Instrucciones de Configuración
Para ejecutar la API de Meteorología:
1. Clona el repositorio.
2. Configura un servidor Redis.
3. Actualiza la configuración de la aplicación con las claves API necesarias y los detalles del servidor Redis.
4. Inicia la aplicación Ktor.
