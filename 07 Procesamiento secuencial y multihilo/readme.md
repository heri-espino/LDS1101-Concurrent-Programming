# Actividad 7 - Procesamiento secuencial y multihilo

## Objetivo

Comparar el rendimiento de una implementación secuencial y una implementación con cuatro hilos al aumentar:

1. el tamaño del arreglo
2. la cantidad de trabajo realizada por cada elemento

La actividad pide arreglos de enteros entre 1 y 100 con tamaños de **40**, **100,000**, **1,000,000** y **10,000,000** elementos.

Para cada tamaño se comparan dos operaciones:

- **Suma sencilla:** acumular todos los elementos
- **Mayor procesamiento:** acumular `sqrt(x) * log(x + 1)` para cada elemento

Cada operación se ejecuta de dos maneras:

- **Secuencial:** un solo flujo procesa todo el arreglo
- **Multihilo:** cuatro hilos procesan secciones diferentes del arreglo usando `start()` y `join()`

Cada implementación se mide **10 veces** y se reporta el tiempo promedio. La generación del arreglo queda fuera de la medición.

## Idea conceptual

La versión multihilo divide el arreglo en cuatro intervalos:

```text
arreglo completo
|---------|---------|---------|---------|
   hilo 0    hilo 1    hilo 2    hilo 3
```

Cada hilo calcula un resultado parcial en una posición distinta. Después de llamar a `join()` sobre los cuatro hilos, el hilo principal combina los cuatro resultados.

No se necesita un cerrojo para los subtotales porque cada hilo escribe en una posición distinta del arreglo de resultados. `join()` garantiza que todos hayan terminado antes de leer y combinar esos subtotales.

## Qué se está comparando

La versión multihilo tiene trabajo adicional:

- crear objetos `Thread`
- iniciar los hilos
- planificarlos
- esperar con `join()`
- combinar resultados parciales

Ese costo puede ser mayor que el trabajo útil cuando el arreglo es pequeño o la operación es muy sencilla. Cuando el arreglo crece y cada elemento exige más cómputo, los cuatro hilos tienen más trabajo que repartir y el paralelismo puede compensar ese costo.

Por eso la pregunta de la actividad no es simplemente "¿multihilo es más rápido?", sino:

> ¿A partir de qué tamaño y complejidad el trabajo paralelo compensa el costo de manejar varios hilos?

## Medición

El programa utiliza `System.nanoTime()` y convierte el promedio a milisegundos.

La generación del arreglo se hace antes de iniciar el cronómetro:

```text
generar arreglo
      |
      v
iniciar cronómetro
      |
      v
ejecutar operación
      |
      v
detener cronómetro
```

Los tiempos dependen del procesador, la JVM y la carga del sistema, por lo que la tabla del reporte debe completarse con la ejecución realizada en la computadora usada para la evidencia.

## Verificación de resultados

La suma usa `long` y se compara exactamente.

La operación de mayor procesamiento usa `double`. Como el orden de suma cambia al dividir el arreglo entre hilos, pueden aparecer diferencias mínimas de redondeo de punto flotante. El programa las compara con una tolerancia numérica pequeña.

En Java, la expresión del ejercicio se implementa como:

```java
Math.sqrt(valor) * Math.log(valor + 1.0)
```

donde `Math.log` es el logaritmo natural.

## Estructura

```text
07 Procesamiento secuencial y multihilo/
  Actividad 7.pdf
  readme.md
  code/
    java/
      Ejercicio01ProcesamientoSecuencialMultihilo.java
  code ultra commented/
    java/
      Ejercicio01ProcesamientoSecuencialMultihilo.java
  latex/
    Actividad7_Hernandez_Andrea_Espino_Heriberto.tex
    figuras/
    template/
```

## Ejecutar

Desde `code/java`:

```bash
javac Ejercicio01ProcesamientoSecuencialMultihilo.java
java Ejercicio01ProcesamientoSecuencialMultihilo
```

La salida genera la tabla:

```text
Tamano | Operacion | Secuencial (ms) | Multihilo (ms) | Mas rapido
```

y termina confirmando que las versiones secuencial y multihilo produjeron resultados equivalentes.
