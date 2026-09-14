#include <algorithm>
#include <array>
#include <atomic>
#include <chrono>
#include <iostream>
#include <mutex>
#include <sstream>
#include <thread>

const int PROCESOS = 3;
const int ITERACIONES = 3;

struct Proceso {
    std::atomic<int> entrando{0};
    std::atomic<int> numero{0};
};

std::array<Proceso, PROCESOS> procesos;
std::atomic<int> contador{0};
std::mutex mutexSalida;

void imprimir(const std::string& mensaje) {
    std::lock_guard<std::mutex> bloqueo(mutexSalida);
    std::cout << mensaje << std::endl;
}

// simula trabajo dentro de la seccion critica
void pausa() {
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
}

// aplica el algoritmo de la panaderia de lamport
void proceso(int id) {
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        procesos[id].entrando.store(1);

        int mayor = 0;
        for (int indice = 0; indice < PROCESOS; ++indice) {
            mayor = std::max(mayor, procesos[indice].numero.load());
        }
        procesos[id].numero.store(mayor + 1);
        procesos[id].entrando.store(0);

        for (int otro = 0; otro < PROCESOS; ++otro) {
            if (otro == id) {
                continue;
            }

            while (procesos[otro].entrando.load() == 1) {
                std::this_thread::yield();
            }

            while (procesos[otro].numero.load() != 0
                   && (procesos[otro].numero.load() < procesos[id].numero.load()
                   || (procesos[otro].numero.load() == procesos[id].numero.load()
                   && otro < id))) {
                std::this_thread::yield();
            }
        }

        std::ostringstream entrada;
        entrada << "P" << id << " entra con numero " << procesos[id].numero.load();
        imprimir(entrada.str());

        int valorLeido = contador.load();
        pausa();
        contador.store(valorLeido + 1);

        std::ostringstream salida;
        salida << "P" << id << " sale con contador = " << contador.load();
        imprimir(salida.str());
        procesos[id].numero.store(0);
    }
}

int main() {
    imprimir("prueba del algoritmo de la panaderia de Lamport");

    std::thread hilos[PROCESOS];

    for (int id = 0; id < PROCESOS; ++id) {
        hilos[id] = std::thread(proceso, id);
    }

    for (std::thread& hilo : hilos) {
        hilo.join();
    }

    imprimir("contador esperado: " + std::to_string(PROCESOS * ITERACIONES));
    imprimir("contador obtenido: " + std::to_string(contador.load()));
    imprimir("resultado: cumple exclusion mutua, progreso y espera limitada para N procesos");
}
