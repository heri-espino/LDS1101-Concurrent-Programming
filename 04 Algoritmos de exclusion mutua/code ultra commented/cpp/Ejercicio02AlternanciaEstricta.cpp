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

// simula trabajo dentro de la seccion critica
void pausa() {
    // detiene el hilo durante cinco milisegundos
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
}

// aplica la alternancia estricta con turn
void proceso(int id) {
    // repite el acceso a la seccion critica
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        // espera hasta recibir el turno propio
        while (turn.load() != id) {
            // cede el procesador a otro hilo
            std::this_thread::yield();
        }

        // prepara el mensaje de entrada
        std::ostringstream entrada;
        // agrega el identificador y el turno
        entrada << "P" << id << " entra con turn = " << turn.load();
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
        // entrega el turno al otro proceso
        turn.store(1 - id);
    }
}

// declara el punto de entrada del programa
int main() {
    // muestra el nombre de la prueba
    imprimir("prueba de alternancia estricta");

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
    // explica las propiedades observadas
    imprimir("resultado: conserva exclusion mutua pero puede bloquear por falta de progreso");
}
