# Actividad 11 — Filósofos comensales

Un solo programa contiene ambas versiones. Usa cinco filósofos, cinco semáforos binarios y tres intentos de comer por filósofo. La pausa tras tomar el primer tenedor hace visible el interbloqueo.

```bash
cd 'code/java'
javac FilosofosComensales.java
java FilosofosComensales
java FilosofosComensales mejorada
```

Para repetir cinco veces cada versión (Linux/macOS):

```bash
for i in {1..5}; do java FilosofosComensales; done
for i in {1..5}; do java FilosofosComensales mejorada; done
```

La versión original puede dejar los cinco hilos bloqueados. Para que el ejemplo no quede ejecutándose indefinidamente se usan hilos daemon y una espera máxima de dos segundos; **la espera máxima no soluciona el interbloqueo**. En la mejorada, únicamente el filósofo cero invierte el orden de toma de tenedores.

El reporte está en `latex/`. Coloca tus capturas reales en `latex/figuras/11_original.png` y `11_mejorada.png`.
