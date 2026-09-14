// incluye las operaciones atomicas
#include <atomic>
// incluye las pausas con unidades de tiempo
#include <chrono>
// incluye la variable de condicion
#include <condition_variable>
// incluye la salida por consola
#include <iostream>
// incluye el control de acceso a la salida
#include <mutex>
// incluye la construccion de mensajes
#include <sstream>
// incluye los hilos
#include <thread>

// declara la clase para sincronizar los dos procesos
class Barrera {
public:
    // recibe la cantidad de participantes de la barrera
    explicit Barrera(int participantes)
        // guarda la cantidad de participantes
        : participantes(participantes) {}

    // define el metodo que espera a los participantes
    void esperar() {
        // bloquea el acceso a los datos de la barrera
        std::unique_lock<std::mutex> bloqueo(mutex);
        // guarda la generacion observada por el participante
        int generacionActual = generacion;

        // aumenta la cantidad de participantes que llegaron
        if (++esperando == participantes) {
            // reinicia la cantidad para la siguiente ronda
            esperando = 0;
            // cambia la generacion de la barrera
            ++generacion;
            // despierta a los participantes que esperaban
            condicion.notify_all();
        } else {
            // espera hasta que cambie la generacion
            condicion.wait(bloqueo, [&] {
                // comprueba si la barrera avanzo
                return generacion != generacionActual;
            });
        }
    }

private:
    // guarda la cantidad de participantes requerida
    int participantes;
    // guarda la cantidad de participantes que llegaron
    int esperando = 0;
    // identifica la ronda actual de la barrera
    int generacion = 0;
    // protege los datos de la barrera
    std::mutex mutex;
    // permite esperar hasta completar la barrera
    std::condition_variable condicion;
};

// define el numero de repeticiones de cada proceso
const int ITERACIONES = 3;
// declara la variable cerrojo compartida
std::atomic<int> cerrojo{0};
// declara el contador compartido
std::atomic<int> contador{0};
// declara la barrera despues de comprobar el cerrojo
Barrera despuesDeComprobar(2);
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

// aplica el algoritmo de variable cerrojo
void proceso(int id) {
    // repite el acceso a la seccion critica
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        // espera mientras el cerrojo indique que esta ocupado
        while (cerrojo.load() == 1) {
            // cede el procesador a otro hilo
            std::this_thread::yield();
        }

        // fuerza el interleaving que muestra la falla del algoritmo
        despuesDeComprobar.esperar();
        // modifica el cerrojo despues de la comprobacion
        cerrojo.store(1);

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
        // libera el cerrojo
        cerrojo.store(0);
    }
}

// declara el punto de entrada del programa
int main() {
    // muestra el nombre de la prueba
    imprimir("prueba de variable cerrojo");

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
    // explica la causa de la falla
    imprimir("resultado: comprobar y modificar cerrojo son dos operaciones separadas");
}
