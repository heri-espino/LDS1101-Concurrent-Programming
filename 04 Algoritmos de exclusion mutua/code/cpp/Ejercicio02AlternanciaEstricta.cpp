#include <atomic>
#include <chrono>
#include <iostream>
#include <mutex>
#include <sstream>
#include <thread>

const int ITERACIONES = 3;
std::atomic<int> turn{0};
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

// aplica la alternancia estricta con turn
void proceso(int id) {
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        while (turn.load() != id) {
            std::this_thread::yield();
        }

        std::ostringstream entrada;
        entrada << "P" << id << " entra con turn = " << turn.load();
        imprimir(entrada.str());

        int valorLeido = contador.load();
        pausa();
        contador.store(valorLeido + 1);

        std::ostringstream salida;
        salida << "P" << id << " sale con contador = " << contador.load();
        imprimir(salida.str());
        turn.store(1 - id);
    }
}

int main() {
    imprimir("prueba de alternancia estricta");

    std::thread p0(proceso, 0);
    std::thread p1(proceso, 1);

    p0.join();
    p1.join();

    imprimir("contador esperado: " + std::to_string(ITERACIONES * 2));
    imprimir("contador obtenido: " + std::to_string(contador.load()));
    imprimir("resultado: conserva exclusion mutua pero puede bloquear por falta de progreso");
}
