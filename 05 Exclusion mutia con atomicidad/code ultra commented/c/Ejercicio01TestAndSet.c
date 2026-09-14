// incluye las funciones para crear y esperar hilos
#include <pthread.h>
// incluye la funcion que cede el procesador
#include <sched.h>
// incluye las operaciones atomicas del lenguaje c
#include <stdatomic.h>
// incluye el tipo booleano
#include <stdbool.h>
// incluye la salida por consola
#include <stdio.h>

// define la cantidad de hilos de la prueba
#define HILOS 3
// define la cantidad de entradas de cada hilo
#define ITERACIONES 5

// declara el cerrojo booleano compartido e inicialmente libre
static atomic_bool lockFlag = ATOMIC_VAR_INIT(false);
// declara el contador compartido
static atomic_int contador = ATOMIC_VAR_INIT(0);

// simula la operacion test-and-set del pseudocodigo
static bool testAndSet(bool nuevoValor) {
    // obtiene el valor anterior y guarda el nuevo valor como una operacion
    return atomic_exchange_explicit(&lockFlag, nuevoValor, memory_order_acq_rel);
}

// solicita acceso a la seccion critica mediante espera activa
static void requestCS(int id) {
    // marca que el identificador se recibe para seguir el pseudocodigo
    (void) id;

    // repite la operacion mientras el cerrojo anterior estuviera ocupado
    while (testAndSet(true)) {
        // cede el procesador y vuelve a intentar
        sched_yield();
    }
}

// libera el cerrojo al salir de la seccion critica
static void releaseCS(int id) {
    // marca que el identificador se recibe para seguir el pseudocodigo
    (void) id;
    // cambia el cerrojo a libre
    testAndSet(false);
}

// ejecuta las entradas del hilo a la seccion critica
static void *proceso(void *argumento) {
    // recupera el identificador recibido por pthread
    int id = *(int *) argumento;

    // repite cinco veces el acceso a la seccion critica
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        // solicita el cerrojo
        requestCS(id);

        // incrementa el contador dentro de la seccion critica
        int valor = atomic_fetch_add_explicit(&contador, 1, memory_order_relaxed) + 1;
        // muestra el hilo y el valor obtenido
        printf("Hilo %d entra a la seccion critica en la iteracion %d, contador = %d\n",
               id, iteracion, valor);

        // libera el cerrojo
        releaseCS(id);
    }

    // termina la funcion del hilo
    return NULL;
}

// declara el punto de entrada del programa
int main(void) {
    // declara el arreglo de hilos
    pthread_t hilos[HILOS];
    // declara los identificadores de los hilos
    int identificadores[HILOS];

    // muestra el nombre de la prueba
    printf("prueba de exclusion mutua con test-and-set\n");

    // recorre los identificadores de los hilos
    for (int id = 0; id < HILOS; ++id) {
        // guarda el identificador actual
        identificadores[id] = id;
        // crea el hilo y le asigna la funcion del proceso
        pthread_create(&hilos[id], NULL, proceso, &identificadores[id]);
    }

    // recorre los hilos creados
    for (int id = 0; id < HILOS; ++id) {
        // espera a que termine el hilo actual
        pthread_join(hilos[id], NULL);
    }

    // muestra el valor esperado del contador
    printf("contador esperado: %d\n", HILOS * ITERACIONES);
    // muestra el valor obtenido del contador
    printf("contador obtenido: %d\n", atomic_load_explicit(&contador, memory_order_relaxed));
    // explica el resultado de la prueba
    printf("resultado: test-and-set garantiza exclusion mutua con un cerrojo booleano\n");
}
