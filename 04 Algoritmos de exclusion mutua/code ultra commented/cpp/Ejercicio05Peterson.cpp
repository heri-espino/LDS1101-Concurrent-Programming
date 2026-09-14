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

// define el numero de repeticiones de cada proceso
const int ITERACIONES = 3;
// declara la bandera del proceso cero
std::atomic<bool> flag0{false};
// declara la bandera del proceso uno
std::atomic<bool> flag1{false};
// declara el turno compartido
std::atomic<int> turn{0};
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

// consulta la bandera de un proceso
bool bandera(int id) {
    // devuelve la bandera solicitada
    return id == 0 ? flag0.load() : flag1.load();
}

// modifica la bandera de un proceso
void establecerBandera(int id, bool valor) {
    // selecciona la bandera del proceso cero
    if (id == 0) {
        // guarda el valor de la bandera cero
        flag0.store(valor);
    } else {
        // guarda el valor de la bandera uno
        flag1.store(valor);
    }
}

// simula trabajo dentro de la seccion critica
void pausa() {
    // detiene el hilo durante cinco milisegundos
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
}

// aplica el algoritmo de peterson para dos procesos
void proceso(int id) {
    // calcula el identificador del proceso contrario
    int otro = 1 - id;

    // repite el acceso a la seccion critica
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        // anuncia la intencion de entrar
        establecerBandera(id, true);
        // cede la prioridad al otro proceso
        turn.store(otro);

        // espera mientras el otro quiera entrar y tenga prioridad
        while (bandera(otro) && turn.load() == otro) {
            // cede el procesador a otro hilo
            std::this_thread::yield();
        }

        // prepara el mensaje de entrada
        std::ostringstream entrada;
        // agrega el identificador y la iteracion
        entrada << "P" << id << " entra a la seccion critica en la iteracion " << iteracion;
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
        // anuncia que el proceso ya salio
        establecerBandera(id, false);
    }
}

// declara el punto de entrada del programa
int main() {
    // muestra el nombre de la prueba
    imprimir("prueba del algoritmo de Peterson");

    // crea el hilo del proceso cero
    std::thread p0(proceso, 0);
    // crea el hilo del proceso uno
    std::thread p1(proceso, 1);

    // espera el fin del proceso cero
    p0.join();
    // espera el fin del proceso uno
    p1.join();

    // muestra el contador esperado
    imprimir("contador esperado: " + std::to_string(ITERACIONES * 2));
    // muestra el contador obtenido
    imprimir("contador obtenido: " + std::to_string(contador.load()));
    // explica las propiedades que cumple
    imprimir("resultado: cumple exclusion mutua, progreso y espera limitada para dos procesos");
}
