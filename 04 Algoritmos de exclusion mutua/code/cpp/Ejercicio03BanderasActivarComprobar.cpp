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

std::atomic<bool> flag0{false};
std::atomic<bool> flag1{false};
std::atomic<int> contador{0};
std::atomic<bool> detener{false};
Barrera banderasActivadas(2);
std::mutex mutexSalida;

void imprimir(const std::string& mensaje) {
    std::lock_guard<std::mutex> bloqueo(mutexSalida);
    std::cout << mensaje << std::endl;
}

// consulta la bandera del otro proceso
bool banderaDelOtro(int id) {
    return id == 0 ? flag1.load() : flag0.load();
}

// coloca la bandera y despues comprueba la del otro proceso
void proceso(int id) {
    if (id == 0) {
        flag0.store(true);
    } else {
        flag1.store(true);
    }

    banderasActivadas.esperar();

    while (banderaDelOtro(id) && !detener.load()) {
        std::this_thread::yield();
    }

    if (banderaDelOtro(id)) {
        return;
    }

    std::ostringstream entrada;
    entrada << "P" << id << " entra a la seccion critica";
    imprimir(entrada.str());
    contador.fetch_add(1);

    std::ostringstream salida;
    salida << "P" << id << " sale con contador = " << contador.load();
    imprimir(salida.str());
}

int main() {
    imprimir("prueba de banderas activar y comprobar");

    std::thread p0(proceso, 0);
    std::thread p1(proceso, 1);

    std::this_thread::sleep_for(std::chrono::milliseconds(200));
    detener.store(true);

    p0.join();
    p1.join();

    imprimir("contador obtenido: " + std::to_string(contador.load()));
    imprimir("resultado: ambos procesos quedan esperando cuando las dos banderas valen true");
}
