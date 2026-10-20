# Actividad 12 — Implementación de monitores

Dos programas sencillos, cada uno en su archivo y con main() independiente.

```bash
cd 'code/java'
javac ProductorConsumidorMonitor.java FilosofosMonitor.java
java ProductorConsumidorMonitor
java FilosofosMonitor
```

El primero crea 2 productores, 2 consumidores y un búfer circular de 5 elementos. Cada productor deposita 20 valores únicos y cada consumidor retira 20. Al final imprime true si los 40 valores se consumieron exactamente una vez.

El segundo crea 5 filósofos y un monitor con estados PENSANDO, HAMBRIENTO y COMIENDO. Cada uno come 20 veces. Usa test(i) para permitir que vecinos no coman simultáneamente.

Para diez ejecuciones (Linux/macOS):

```bash
for i in {1..10}; do java ProductorConsumidorMonitor | tail -1; done
for i in {1..10}; do java FilosofosMonitor | tail -5; done
```

Reporte: latex/, con capturas reales latex/figuras/12_productor_consumidor.png y 12_filosofos_monitor.png. El formato LaTeX se comparte en ../latex_compartido/.
