# Actividad 9 - Semáforo binario y contador

## Objetivo

Implementar y comparar dos tipos de semáforo para coordinar varios hilos:

- **semáforo binario:** controla un único permiso
- **semáforo contador:** controla varios permisos del mismo tipo

La implementación conserva la estructura mostrada en el pseudocódigo de clase. No se sustituye por `java.util.concurrent.Semaphore`: se mantienen `value`, `P()`, `V()`, `synchronized`, `Util.mywait(this)` y `notify()`.

| Concepto | Pseudocódigo | Java habitual | Función |
|---|---|---|---|
| solicitar permiso | `P()` | `acquire()` | intenta obtener un permiso |
| liberar permiso | `V()` | `release()` | devuelve un permiso |

## Parte 1 - Semáforo binario

Se simula una cuenta bancaria compartida con saldo inicial de **$1000** y cinco hilos que realizan depósitos y retiros. `true` significa permiso disponible y `false` permiso ocupado.

El método `P()` espera mientras `value == false` y después cambia `value` a `false`. El método `V()` cambia `value` a `true` y ejecuta `notify()`.

La cuenta bancaria no usa `synchronized` en sus operaciones: la exclusión mutua la proporciona el semáforo porque cada hilo ejecuta `P()` antes de consultar o modificar el saldo y `V()` al salir.

La prueba verifica que el máximo de hilos modificando el saldo al mismo tiempo sea **1**.

## Parte 2 - Semáforo contador

Se simula un laboratorio con **3 impresoras** y **8 hilos** que desean imprimir. El semáforo contador inicia con `value = 3`.

Cada `P()` decrementa `value`; si el resultado es negativo, el hilo se bloquea. Cada `V()` incrementa `value`; si el resultado es menor o igual que cero, se despierta a un hilo bloqueado. Esta estructura reproduce el pseudocódigo de las diapositivas.

La prueba verifica que nunca existan más de **3 hilos imprimiendo simultáneamente**.

## Relación con las actividades anteriores

- **exclusión mutua:** el semáforo binario permite que solo un hilo use la cuenta
- **sincronización:** un hilo puede quedar bloqueado hasta que otro libere un permiso
- **espera bloqueada:** a diferencia de Test-and-Set, el hilo usa `wait()` en vez de mantenerse en espera activa
- **hilos:** varios `Thread` compiten por permisos sobre recursos compartidos

## Comparación

| Aspecto | Semáforo binario | Semáforo contador |
|---|---|---|
| valor inicial de la actividad | 1 (`true`) | 3 |
| permisos disponibles | 1 | 3 |
| recurso controlado | una cuenta bancaria | tres impresoras |
| máximo de hilos simultáneos | 1 | 3 |
| bloqueo | cuando el único permiso está ocupado | cuando no queda un permiso disponible |

## Estructura

```text
09 Semaforo binario y contador/
  Actividad 9.pdf
  readme.md
  slides/
  code/
    java/
      Util.java
      Ejercicio01SemaforoBinario.java
      Ejercicio02SemaforoContador.java
  code ultra commented/
    java/
      Util.java
      Ejercicio01SemaforoBinario.java
      Ejercicio02SemaforoContador.java
  latex/
    Actividad9_Hernandez-Andrea_Espino-Heriberto.tex
    figuras/
    template/
    build/
```

## Ejecutar

Desde `code/java`:

```bash
javac *.java
java Ejercicio01SemaforoBinario
java Ejercicio02SemaforoContador
```

La primera ejecución debe terminar con saldo **$1300.00** y máximo simultáneo **1**. La segunda debe reportar máximo simultáneo **3**.
