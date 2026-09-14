#include <atomic>
#include <chrono>
#include <condition_variable>
#include <iostream>
#include <mutex>
#include <sstream>
#include <thread>

class Barrera {
public:
    explicit Barrera(int participantes)
        : participantes(participantes) {}

    void esperar() {
        std::unique_lock<std::mutex> bloqueo(mutex);
        int generacionActual = generacion;

        if (++esperando == participantes) {
            esperando = 0;
            ++generacion;
            condicion.notify_all();
        } else {
            condicion.wait(bloqueo, [&] {
                return generacion != generacionActual;
            });
        }
    }

private:
    int participantes;
    int esperando = 0;
    int generacion = 0;
    std::mutex mutex;
    std::condition_variable condicion;
};

const int ITERACIONES = 3;
std::atomic<int> cerrojo{0};
std::atomic<int> contador{0};
Barrera despuesDeComprobar(2);
std::mutex mutexSalida;

void imprimir(const std::string& mensaje) {
    std::lock_guard<std::mutex> bloqueo(mutexSalida);
    std::cout << mensaje << std::endl;
}

// simula trabajo dentro de la seccion critica
void pausa() {
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
}

// aplica el algoritmo de variable cerrojo
void proceso(int id) {
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        while (cerrojo.load() == 1) {
            std::this_thread::yield();
        }

        // fuerza el interleaving que muestra la falla del algoritmo
        despuesDeComprobar.esperar();

        cerrojo.store(1);

        std::ostringstream entrada;
        entrada << "P" << id << " entra a la seccion critica en la iteracion " << iteracion;
        imprimir(entrada.str());

        int valorLeido = contador.load();
        pausa();
        contador.store(valorLeido + 1);

        std::ostringstream salida;
        salida << "P" << id << " sale con contador = " << contador.load();
        imprimir(salida.str());
        cerrojo.store(0);
    }
}

int main() {
    imprimir("prueba de variable cerrojo");

    std::thread p0(proceso, 0);
    std::thread p1(proceso, 1);

    p0.join();
    p1.join();

    imprimir("contador esperado: " + std::to_string(ITERACIONES * 2));
    imprimir("contador obtenido: " + std::to_string(contador.load()));
    imprimir("resultado: comprobar y modificar cerrojo son dos operaciones separadas");
}
