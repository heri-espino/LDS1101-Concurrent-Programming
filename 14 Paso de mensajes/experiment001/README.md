# experiment001 — Comunicación local UDP y TCP

**Objetivo.** Verificar que cuatro procesos Java independientes se comunican de acuerdo con las instrucciones de la Actividad 14, con el menor código adicional posible.

**Qué se hizo.** Se implementó un servidor/cliente UDP en el puerto 5000 y un directorio de servicios TCP en el puerto 6000. Después se ejecutaron tres sesiones completas de cada protocolo. Se prueban confirmaciones UDP, registros y búsquedas TCP y la terminación por `SALIR`.

**Por qué.** La actividad exige al menos tres ejecuciones por implementación, una tabla comparativa y observaciones sobre los métodos bloqueantes. Las pruebas utilizan sockets reales de `localhost`, no simulaciones.

**Cómo reproducir.** Desde `14 Paso de mensajes/`, con Java 17 y Python 3 instalados, ejecuta:

```bash
python3 experiment001/run_tests.py
```

El script compila código normal y ultracomentado, inicia el servidor mediante un proceso del sistema, envía líneas al cliente, verifica los resultados, espera el cierre y guarda la salida real de ambos procesos en `results/`. Los archivos `*_cliente.txt` y `*_servidor.txt` son registros de consola reales. La tabla `tabla_pruebas.csv` es la fuente de la tabla del reporte. Los puertos de origen del cliente cambian entre ejecuciones.

**Hallazgos.** En la ejecución local inicial se completaron las seis sesiones y se obtuvieron las respuestas esperadas, con cierre tras `SALIR` y sin pérdida observada de mensajes en `localhost`. Esto no demuestra fiabilidad general de UDP en redes con pérdidas.

**Próximo paso.** Tomar capturas desde dos terminales reales y agregarlas a `figures/`; recompilar el PDF. Como ampliación opcional, estudiar pérdidas de datagramas o varios clientes TCP simultáneos (no requeridos en esta actividad).
