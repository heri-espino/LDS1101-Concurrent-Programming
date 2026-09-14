#include <pthread.h>
#include <sched.h>
#include <stdatomic.h>
#include <stdbool.h>
#include <stdio.h>

#define HILOS 3
#define ITERACIONES 5

static atomic_bool lockFlag = ATOMIC_VAR_INIT(false);
static atomic_int contador = ATOMIC_VAR_INIT(0);

// simula la operacion test-and-set del pseudocodigo
static bool testAndSet(bool nuevoValor) {
    return atomic_exchange_explicit(&lockFlag, nuevoValor, memory_order_acq_rel);
}

// solicita acceso a la seccion critica mediante espera activa
static void requestCS(int id) {
    (void) id;

    while (testAndSet(true)) {
        sched_yield();
    }
}

// libera el cerrojo al salir de la seccion critica
static void releaseCS(int id) {
    (void) id;
    testAndSet(false);
}

// ejecuta las entradas del hilo a la seccion critica
static void *proceso(void *argumento) {
    int id = *(int *) argumento;

    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        requestCS(id);

        int valor = atomic_fetch_add_explicit(&contador, 1, memory_order_relaxed) + 1;
        printf("Hilo %d entra a la seccion critica en la iteracion %d, contador = %d\n",
               id, iteracion, valor);

        releaseCS(id);
    }

    return NULL;
}

int main(void) {
    pthread_t hilos[HILOS];
    int identificadores[HILOS];

    printf("prueba de exclusion mutua con test-and-set\n");

    for (int id = 0; id < HILOS; ++id) {
        identificadores[id] = id;
        pthread_create(&hilos[id], NULL, proceso, &identificadores[id]);
    }

    for (int id = 0; id < HILOS; ++id) {
        pthread_join(hilos[id], NULL);
    }

    printf("contador esperado: %d\n", HILOS * ITERACIONES);
    printf("contador obtenido: %d\n", atomic_load_explicit(&contador, memory_order_relaxed));
    printf("resultado: test-and-set garantiza exclusion mutua con un cerrojo booleano\n");
}
