# Actividad 5 — Exclusión mutua con atomicidad

## Idea central

Esta actividad estudia el mismo problema que los algoritmos de exclusión mutua vistos antes: varios hilos comparten un recurso, pero **solo uno puede entrar a la sección crítica a la vez**.

En el programa hay tres hilos, un contador compartido y un cerrojo booleano:

```text
T0 ─┐
T1 ─┼──> lockFlag ──> sección crítica ──> contador
T2 ─┘
```

El cerrojo usa esta convención:

- `false`: libre
- `true`: ocupado

La propiedad que buscamos es:

```text
número de hilos dentro de la sección crítica <= 1
```

## ¿Por qué necesitamos un cerrojo?

Si dos hilos leen y modifican una variable compartida al mismo tiempo puede aparecer una **condición de carrera**. El problema clásico de un cerrojo ingenuo es separar estas dos acciones:

```text
1. comprobar si está libre
2. cambiarlo a ocupado
```

Puede ocurrir un cambio de contexto entre ambas y dos hilos podrían creer que obtuvieron el cerrojo.

## Test-and-Set con `getAndSet(true)`

`AtomicBoolean.getAndSet(true)` hace dos cosas como una sola operación atómica:

```text
valorAnterior = lockFlag
lockFlag = true
devolver valorAnterior
```

Por eso ningún otro hilo puede intercalarse entre la comprobación y el cambio.

| Estado anterior | Devuelve | Estado nuevo | Significado |
|---|---|---|---|
| `false` | `false` | `true` | estaba libre; el hilo adquirió el cerrojo |
| `true` | `true` | `true` | estaba ocupado; el hilo debe esperar |

El código de entrada es:

```java
while (lockFlag.getAndSet(true)) {
    Thread.onSpinWait();
}
```

Se lee así:

> Mientras el cerrojo ya estuviera ocupado, sigo esperando. Cuando `getAndSet(true)` devuelve `false`, yo fui quien lo adquirió.

Esta espera se llama **espera activa** o *busy waiting*: el hilo continúa comprobando el cerrojo en lugar de quedar bloqueado.

## Sección de entrada, crítica y salida

El flujo conceptual de cada hilo es:

```text
requestCS()
    ↓
adquirir lock
    ↓
SECCIÓN CRÍTICA
    contador++
    imprimir
    ↓
releaseCS()
    ↓
lock = false
```

`releaseCS()` vuelve a dejar el cerrojo libre para que otro hilo pueda entrar.

## Qué hacen las otras herramientas del programa

### `Thread`

Representa un hilo de ejecución. Los tres hilos pertenecen al mismo proceso Java y comparten `lockFlag` y `contador`.

### La lambda `() -> proceso(idFinal)`

Al crear un hilo:

```java
new Thread(() -> proceso(idFinal), "Hilo-" + id)
```

la lambda describe el trabajo que ejecutará el hilo. `start()` es lo que realmente inicia el hilo y provoca que se ejecute ese trabajo.

### `CountDownLatch`

`new CountDownLatch(1)` funciona como una puerta cerrada. Cada hilo llega a:

```java
inicio.await();
```

y espera. Después `main` ejecuta:

```java
inicio.countDown();
```

El contador interno pasa de `1` a `0` y los tres hilos pueden comenzar a competir aproximadamente al mismo tiempo. Esto sirve para hacer visible la concurrencia; **no es el mecanismo de exclusión mutua**.

### `join()`

Después de liberar el inicio, `main` hace:

```java
for (Thread hilo : hilos) {
    hilo.join();
}
```

`join()` no inicia ni ejecuta `proceso()`. Solo hace que el hilo principal espere hasta que cada hilo haya terminado.

## Ejecución mental con dos hilos

Estado inicial:

```text
lockFlag = false
contador = 0
```

1. `T0` ejecuta `getAndSet(true)` y recibe `false`: adquiere el lock.
2. `T1` ejecuta `getAndSet(true)` y recibe `true`: espera en el `while`.
3. `T0` incrementa el contador dentro de la sección crítica.
4. `T0` libera el lock poniéndolo en `false`.
5. En uno de sus intentos, `T1` recibe `false`, adquiere el lock y entra.

Así los intentos pueden ocurrir concurrentemente, pero la sección crítica queda serializada.

## Resultado esperado

Hay tres hilos y cada uno entra cinco veces:

```text
3 hilos × 5 incrementos = 15
```

Por eso el programa debe terminar con un contador igual a `15`.

## Nota sobre `AtomicInteger`

El código usa `AtomicInteger` y `incrementAndGet()`, que ya realizan el incremento de forma atómica. Para entender la actividad, el mecanismo que se está estudiando es el **cerrojo implementado con `AtomicBoolean.getAndSet(true)`**. Si el contador fuera un `int` normal, el cerrojo también podría proteger correctamente `contador++` siempre que ese acceso permaneciera dentro de la sección crítica.

## Resumen para explicar

```text
Test-and-Set = comprobar + ocupar el lock de forma atómica

false -> el hilo ganó -> entra
true  -> otro hilo lo tiene -> espera activamente

release -> lock = false -> puede entrar otro hilo
```

**Idea final:** exclusión mutua responde a la pregunta **“¿quién puede entrar ahora?”**.

## Archivos

- [`Actividad 5.pdf`](./Actividad%205.pdf)
- [`Ejercicio01TestAndSet.java`](./code/java/Ejercicio01TestAndSet.java)
- [`Ejercicio01TestAndSet.java` ultracomentado](./code%20ultra%20commented/java/Ejercicio01TestAndSet.java)
