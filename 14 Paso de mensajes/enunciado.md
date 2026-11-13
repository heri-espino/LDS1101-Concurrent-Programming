# Actividad 14: paso de mensajes con UDP y TCP

Transcripción de los requisitos del PDF entregado en esta conversación (3 páginas).

**Objetivo.** Analizar los elementos del paso de mensajes e implementar la comunicación entre procesos locales mediante sockets UDP y TCP en Java.

## Parte 1. Análisis del paso de mensajes

En cada implementación identificar emisor y receptor, mensaje, canal, operaciones de envío/recepción, direccionamiento, operaciones bloqueantes y no bloqueantes y tipo de comunicación (síncrona, asíncrona o condicional).

## Parte 2. UDP

- Servidor: `DatagramSocket` en puerto **5000**; usa `receive()`; informa mensaje, dirección y puerto del cliente; responde `Mensaje recibido: [mensaje]`; atiende hasta `SALIR`.
- Cliente: conecta mediante `localhost:5000`; lee del teclado, codifica en bytes, crea `DatagramPacket`, envía, espera respuesta y la muestra.
- Ejecutar servidor y cliente como programas independientes, en terminales separadas.

## Parte 3. TCP

- Servidor: `ServerSocket` en puerto **6000**; usa `accept()`; guarda servicios en `HashMap`; procesa `REGISTRAR nombre puerto`, `BUSCAR nombre` y `SALIR` y contesta si se registró, se encontró o no existe.
- Cliente: se conecta a `localhost:6000`, envía solicitudes escritas por teclado, espera y muestra respuesta; cierra el socket con `SALIR`.
- Ejemplo: `REGISTRAR calculadora 7000` -> `Servicio registrado`; `BUSCAR calculadora` -> `Servicio encontrado en el puerto 7000`; `BUSCAR inventario` -> `Servicio no encontrado`.

## Parte 4. Pruebas y comparación

- Al menos **tres ejecuciones UDP y tres TCP** con tabla de: prueba, protocolo, mensaje enviado, respuesta y resultado.
- Responder: (1) qué métodos bloquean; (2) por qué UDP no usa `accept()`; (3) diferencias entre `DatagramSocket`, `Socket` y `ServerSocket`; (4) protocolo elegido para directorio; (5) comportamiento al iniciar cliente antes del servidor.

## Entregables

Código fuente de los cuatro programas Java, capturas reales en terminales separadas, tabla de pruebas, respuestas a las cinco preguntas y un diagrama sencillo del intercambio de mensajes para cada implementación.
