# Actividad 14 — Paso de mensajes con UDP y TCP

Se utiliza **Java 17 o superior**, sin librerías adicionales. Los programas están separados en cuatro archivos para que el código sea fácil de leer y explicar. El documento `enunciado.md` conserva las instrucciones del PDF original recibido en la conversación.

## Compilar Java

Desde la raíz del repositorio:

```bash
cd '14 Paso de mensajes'
javac *.java
```

Las versiones `_ultracommented.java` contienen el mismo programa explicado mediante comentarios breves en español (sin acentos en los comentarios), y también pueden compilarse con `javac *.java`.

## UDP — dos terminales

Abre **Terminal 1**, inicia el servidor y déjalo esperando:

```bash
java ServidorUDP
```

Abre **Terminal 2** en la misma carpeta y ejecuta:

```bash
java ClienteUDP
```

Escribe `Hola servidor`, después cualquier otro mensaje y finalmente `SALIR`. Verás `Mensaje recibido: ...` para cada mensaje. El servidor muestra también la IP y el puerto de origen. `SALIR` se confirma antes de cerrar ambos programas.

UDP no establece conexión ni garantiza la entrega. Para evitar una espera ilimitada si el servidor no está activo, el cliente fija un tiempo de espera de **3 segundos**; si vence, Java informa `SocketTimeoutException`.

## TCP — dos terminales

**Terminal 1:**

```bash
java ServidorTCP
```

**Terminal 2:**

```bash
java ClienteTCP
```

Introduce exactamente estas solicitudes, cada una seguida de Enter:

```text
REGISTRAR calculadora 7000
BUSCAR calculadora
BUSCAR inventario
SALIR
```

Respuestas respectivas: `Servicio registrado`, `Servicio encontrado en el puerto 7000`, `Servicio no encontrado` y `Conexion cerrada`. `SALIR` cierra la conexión y el servidor; para otra sesión, vuelve a iniciar ambos programas. El directorio vive en memoria durante la ejecución del servidor.

Si comienzas el cliente TCP sin el servidor activo, la conexión falla normalmente con `ConnectException: Connection refused`.

## Pruebas reproducibles

Con Python 3 instalado, desde esta carpeta:

```bash
python3 experiment001/run_tests.py
```

El script compila los ocho `.java`, crea **tres sesiones independientes UDP y tres TCP** (procesos de servidor/cliente reales), comprueba las respuestas y guarda los registros en `experiment001/results/`, junto con la tabla `tabla_pruebas.csv`. No sustituye las capturas de las terminales del estudiante.

## Compilar el reporte LaTeX

Necesitas `latexmk` y TeX Live o MacTeX con los paquetes del formato compartido `../latex_compartido/estilo.sty`:

```bash
latexmk -pdf -interaction=nonstopmode -halt-on-error -outdir=build Actividad14_Hernandez_Andrea_Espino_Heriberto.tex
cp build/Actividad14_Hernandez_Andrea_Espino_Heriberto.pdf .
```

En PowerShell, usa `Copy-Item build/Actividad14_Hernandez_Andrea_Espino_Heriberto.pdf .` en lugar de `cp`.

El PDF final queda al lado del `.tex` y `build/` conserva los archivos auxiliares versionados por GitHub Actions.

## Evidencia y entrega

El reporte deja espacios para seis capturas **reales**, en `figures/udp_codigo.png`, `figures/udp_servidor.png`, `figures/udp_cliente.png`, `figures/tcp_codigo.png`, `figures/tcp_servidor.png` y `figures/tcp_cliente.png`. Ejecuta manualmente ambos protocolos en terminales separadas y captura el resultado desde tu computadora. No se incluyen capturas inventadas.

El material de referencia y los resultados están en `enunciado.md` y `experiment001/`. El original `Actividad 14.pdf` fue proporcionado en la conversación; se conserva con el paquete local de entrega. El reporte utiliza la portada UDLAP compartida, sin copiar su plantilla a esta carpeta.
