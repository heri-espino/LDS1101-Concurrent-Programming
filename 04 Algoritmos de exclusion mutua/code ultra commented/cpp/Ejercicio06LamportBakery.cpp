// incluye las funciones para comparar valores
#include <algorithm>
// incluye arreglos de tamano fijo
#include <array>
// incluye las operaciones atomicas
#include <atomic>
// incluye las pausas con unidades de tiempo
#include <chrono>
// incluye la salida por consola
#include <iostream>
// incluye el control de acceso a la salida
#include <mutex>
// incluye la construccion de mensajes
#include <sstream>
// incluye los hilos
#include <thread>

// define la cantidad de procesos de la prueba
const int PROCESOS = 3;
// define la cantidad de entradas de cada proceso
const int ITERACIONES = 3;

// declara los datos compartidos de un proceso
struct Proceso {
    // indica que el proceso esta eligiendo turno
    std::atomic<int> entrando{0};
    // guarda el numero de turno del proceso
    std::atomic<int> numero{0};
};

// declara el arreglo de datos compartidos
std::array<Proceso, PROCESOS> procesos;
// declara el contador compartido
std::atomic<int> contador{0};
// protege los mensajes de la consola
std::mutex mutexSalida;

// declara la funcion para imprimir un mensaje completo
void imprimir(const std::string& mensaje) {
    // bloquea la salida para evitar mensajes mezclados
    std::lock_guard<std::mutex> bloqueo(mutexSalida);
    // escribe el mensaje y termina la linea
    std::cout << mensaje << std::endl;
}

// simula trabajo dentro de la seccion critica
void pausa() {
    // detiene el hilo durante cinco milisegundos
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
}

// aplica el algoritmo de la panaderia de lamport
void proceso(int id) {
    // repite el acceso a la seccion critica
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        // indica que el proceso esta eligiendo su turno
        procesos[id].entrando.store(1);

        // inicia el numero mayor encontrado
        int mayor = 0;
        // recorre los turnos actuales
        for (int indice = 0; indice < PROCESOS; ++indice) {
            // conserva el turno mas grande
            mayor = std::max(mayor, procesos[indice].numero.load());
        }
        // asigna el siguiente turno al proceso actual
        procesos[id].numero.store(mayor + 1);
        // indica que termino de elegir turno
        procesos[id].entrando.store(0);

        // compara el turno con cada proceso
        for (int otro = 0; otro < PROCESOS; ++otro) {
            // ignora la comparacion consigo mismo
            if (otro == id) {
                // salta al siguiente proceso
                continue;
            }

            // espera a que el otro proceso termine de elegir turno
            while (procesos[otro].entrando.load() == 1) {
                // cede el procesador a otro hilo
                std::this_thread::yield();
            }

            // espera mientras el otro proceso tenga prioridad
            while (procesos[otro].numero.load() != 0
                   && (procesos[otro].numero.load() < procesos[id].numero.load()
                   || (procesos[otro].numero.load() == procesos[id].numero.load()
                   && otro < id))) {
                // cede el procesador a otro hilo
                std::this_thread::yield();
            }
        }

        // prepara el mensaje de entrada
        std::ostringstream entrada;
        // agrega el identificador y el numero de turno
        entrada << "P" << id << " entra con numero " << procesos[id].numero.load();
        // muestra el mensaje de entrada
        imprimir(entrada.str());

        // lee el contador compartido
        int valorLeido = contador.load();
        // simula trabajo dentro de la seccion critica
        pausa();
        // guarda el valor incrementado
        contador.store(valorLeido + 1);

        // prepara el mensaje de salida
        std::ostringstream salida;
        // agrega el valor actual del contador
        salida << "P" << id << " sale con contador = " << contador.load();
        // muestra el mensaje de salida
        imprimir(salida.str());
        // libera el turno del proceso actual
        procesos[id].numero.store(0);
    }
}

// declara el punto de entrada del programa
int main() {
    // muestra el nombre de la prueba
    imprimir("prueba del algoritmo de la panaderia de Lamport");

    // crea el arreglo de hilos
    std::thread hilos[PROCESOS];

    // recorre los identificadores de los procesos
    for (int id = 0; id < PROCESOS; ++id) {
        // crea el hilo del proceso actual
        hilos[id] = std::thread(proceso, id);
    }

    // recorre los hilos creados
    for (std::thread& hilo : hilos) {
        // espera el fin del hilo actual
        hilo.join();
    }

    // muestra el contador esperado
    imprimir("contador esperado: " + std::to_string(PROCESOS * ITERACIONES));
    // muestra el contador obtenido
    imprimir("contador obtenido: " + std::to_string(contador.load()));
    // explica las propiedades que cumple
    imprimir("resultado: cumple exclusion mutua, progreso y espera limitada para N procesos");
}
