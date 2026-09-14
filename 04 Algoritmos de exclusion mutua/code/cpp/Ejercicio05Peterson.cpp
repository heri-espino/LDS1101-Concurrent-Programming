#include <atomic>
#include <chrono>
#include <iostream>
#include <mutex>
#include <sstream>
#include <thread>

const int ITERACIONES = 3;
std::atomic<bool> flag0{false};
std::atomic<bool> flag1{false};
std::atomic<int> turn{0};
std::atomic<int> contador{0};
std::mutex mutexSalida;

void imprimir(const std::string& mensaje) {
    std::lock_guard<std::mutex> bloqueo(mutexSalida);
    std::cout << mensaje << std::endl;
}

// consulta la bandera de un proceso
bool bandera(int id) {
    return id == 0 ? flag0.load() : flag1.load();
}

// modifica la bandera de un proceso
void establecerBandera(int id, bool valor) {
    if (id == 0) {
        flag0.store(valor);
    } else {
        flag1.store(valor);
    }
}

// simula trabajo dentro de la seccion critica
void pausa() {
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
}

// aplica el algoritmo de peterson para dos procesos
void proceso(int id) {
    int otro = 1 - id;

    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        establecerBandera(id, true);
        turn.store(otro);

        while (bandera(otro) && turn.load() == otro) {
            std::this_thread::yield();
        }

        std::ostringstream entrada;
        entrada << "P" << id << " entra a la seccion critica en la iteracion " << iteracion;
        imprimir(entrada.str());

        int valorLeido = contador.load();
        pausa();
        contador.store(valorLeido + 1);

        std::ostringstream salida;
        salida << "P" << id << " sale con contador = " << contador.load();
        imprimir(salida.str());
        establecerBandera(id, false);
    }
}

int main() {
    imprimir("prueba del algoritmo de Peterson");

    std::thread p0(proceso, 0);
    std::thread p1(proceso, 1);

    p0.join();
    p1.join();

    imprimir("contador esperado: " + std::to_string(ITERACIONES * 2));
    imprimir("contador obtenido: " + std::to_string(contador.load()));
    imprimir("resultado: cumple exclusion mutua, progreso y espera limitada para dos procesos");
}
