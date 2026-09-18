# Actividad 6 — Sincronización de procesos e hilos en Java

## Idea central

Esta actividad cambia de problema. En la actividad anterior queríamos impedir que dos hilos entraran a la misma sección crítica. Aquí queremos **imponer un orden entre tareas**.

```text
Exclusión mutua:  ¿quién puede entrar?
Sincronización:   ¿cuándo puede continuar?
```

La actividad tiene dos partes:

```text
1. Hilos:    start() + join()
2. Procesos: ProcessBuilder.start() + waitFor()
```

---

## 1. Sincronización de hilos con `join()`

El programa representa una dependencia:

```text
Hilo 1: obtener datos
          ↓
Hilo 2: procesar datos
          ↓
Hilo 3: generar reporte
```

El Hilo 2 necesita el resultado del Hilo 1, y el Hilo 3 necesita el resultado del Hilo 2. Por eso no basta con arrancar los tres y confiar en el planificador.

### `Runnable`: la tarea

`Runnable` representa **qué trabajo debe ejecutarse**. Una clase que implementa `Runnable` debe proporcionar el método:

```java
public void run() {
    // trabajo del hilo
}
```

Conviene separar mentalmente:

```text
Runnable = qué trabajo hacer
Thread   = quién ejecuta ese trabajo
```

Por ejemplo:

```java
Thread hilo1 = new Thread(new TareaObtenerDatos(), "Hilo 1");
```

`new TareaObtenerDatos()` crea la tarea; `new Thread(...)` crea el hilo que la ejecutará.

### ¿Era obligatorio crear clases `Runnable`?

No. También podría escribirse con una lambda:

```java
Thread hilo = new Thread(() -> obtenerDatos());
```

Esa lambda también puede usarse como un `Runnable`. Las clases explícitas de la actividad hacen más visible la separación entre **tarea** e **hilo**.

### `@Override`

`Runnable` declara un método `run()`. Cuando la clase escribe:

```java
@Override
public void run() {
    ...
}
```

`@Override` indica al compilador que ese método está implementando o sobrescribiendo un método definido por una interfaz o clase padre. También ayuda a detectar errores de nombre o firma.

### `start()`

`start()` inicia un nuevo hilo. La JVM hace que ese hilo ejecute su `run()`.

```text
hilo.start()
     ↓
nuevo flujo de ejecución
     ↓
run()
```

Crear un objeto `Thread` no basta: hasta llamar a `start()` no comienza la ejecución concurrente del trabajo.

### `join()`

`join()` significa:

> El hilo que llama a `join()` espera hasta que el hilo indicado termine.

Por eso:

```java
hilo1.start();
hilo1.join();

hilo2.start();
hilo2.join();

hilo3.start();
hilo3.join();
```

se lee como:

```text
inicia Hilo 1
espera a que termine
inicia Hilo 2
espera a que termine
inicia Hilo 3
espera a que termine
```

y garantiza:

```text
H1 termina -> H2 puede iniciar -> H2 termina -> H3 puede iniciar
```

Sin los `join()`, los tres hilos podrían ejecutarse en un orden no garantizado y una tarea podría intentar usar datos todavía no producidos.

### `Thread.sleep(...)`

En esta actividad las pausas solo **simulan que una tarea tarda**. No son el mecanismo que impone el orden. El orden lo establecen los `join()`.

---

## 2. Sincronización de procesos con `waitFor()`

Ahora ya no tenemos tres hilos dentro del mismo proceso. El programa principal crea **otro proceso Java independiente**.

```text
Proceso principal
       │
       └── crea/inicia ──> Proceso hijo
```

### `ProcessBuilder`

`ProcessBuilder` describe cómo crear un proceso externo: qué ejecutable usar y qué archivo o comando ejecutar.

```java
ProcessBuilder constructorProceso = new ProcessBuilder(...);
```

En este punto el proceso hijo todavía no está ejecutándose.

### `ProcessBuilder.start()`

```java
Process procesoHijo = constructorProceso.start();
```

Esta llamada sí crea e inicia el proceso externo. El objeto `Process` representa al proceso hijo desde el programa principal.

### `inheritIO()`

```java
constructorProceso.inheritIO();
```

hace que el proceso hijo use la misma consola para entrada y salida, de modo que sus mensajes puedan verse junto con los del proceso principal.

### `waitFor()`

```java
int codigoSalida = procesoHijo.waitFor();
```

hace que el hilo principal espere hasta que el **proceso hijo termine**. Cuando termina, devuelve su código de salida.

Convencionalmente:

```text
0     -> terminación correcta
otro  -> algún tipo de error o condición especial
```

El flujo queda:

```text
principal: start() ─────────> hijo comienza
principal: waitFor()          hijo trabaja
principal: espera             hijo termina
principal: continúa <──────── código de salida
```

---

## `join()` vs `waitFor()`

| Concepto | `join()` | `waitFor()` |
|---|---|---|
| Espera a | un hilo | un proceso |
| Objeto | `Thread` | `Process` |
| Efecto | bloquea al hilo llamador hasta que termine el hilo objetivo | bloquea al hilo llamador hasta que termine el proceso hijo |
| Uso en la actividad | ordenar obtener → procesar → reportar | esperar a que termine el proceso externo |

Los dos expresan la misma idea general:

```text
no continúes hasta que la ejecución de la que dependo haya terminado
```

## Hilos vs procesos en esta actividad

```text
Proceso Java
├── hilo principal
├── Hilo 1
├── Hilo 2
└── Hilo 3
```

Los hilos viven dentro del mismo proceso y pueden compartir variables. En cambio, el segundo ejercicio crea otro proceso Java con su propio espacio de ejecución; la coordinación se hace mediante `Process`/`waitFor()` en lugar de compartir directamente las variables del primer programa.

## Resumen para explicar

```text
Runnable  -> define la tarea
run()     -> contiene el trabajo
start()   -> inicia el hilo y provoca la ejecución de run()
join()    -> espera a que termine un hilo

ProcessBuilder -> prepara un proceso externo
start()        -> inicia ese proceso
waitFor()      -> espera a que termine
```

**Idea final:** sincronización responde a la pregunta **“¿qué debe terminar antes de que yo pueda continuar?”**.

## Archivos

- [`Actividad 6.pdf`](./Actividad%206.pdf)
- [`Ejercicio01SincronizacionHilos.java`](./code/java/Ejercicio01SincronizacionHilos.java)
- [`Ejercicio02ProcesoHijo.java`](./code/java/Ejercicio02ProcesoHijo.java)
- [`Ejercicio02SincronizacionProcesos.java`](./code/java/Ejercicio02SincronizacionProcesos.java)
- [Versiones ultracomentadas](./code%20ultra%20commented/java/)
