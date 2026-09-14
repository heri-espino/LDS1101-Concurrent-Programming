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
// declara la bandera del proceso cero
std::atomic<bool> flag0{false};
// declara la bandera del proceso uno
std::atomic<bool> flag1{false};
// declara el contador compartido
std::atomic<int> contador{0};
// declara la barrera despues de comprobar las banderas
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

// consulta la bandera del otro proceso
bool banderaDelOtro(int id) {
    // devuelve la bandera contraria al identificador recibido
    return id == 0 ? flag1.load() : flag0.load();
}

// simula trabajo dentro de la seccion critica
void pausa() {
    // detiene el hilo durante cinco milisegundos
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
}

// comprueba la bandera y despues activa la propia
void proceso(int id) {
    // repite el acceso a la seccion critica
    for (int iteracion = 1; iteracion <= ITERACIONES; ++iteracion) {
        // espera mientras la bandera del otro proceso este activa
        while (banderaDelOtro(id)) {
            // cede el procesador a otro hilo
            std::this_thread::yield();
        }

        // fuerza que ambos procesos comprueben false antes de activar su bandera
        despuesDeComprobar.esperar();

        // activa la bandera del proceso correspondiente
        if (id == 0) {
            // anuncia que el proceso cero quiere entrar
            flag0.store(true);
        } else {
            // anuncia que el proceso uno quiere entrar
            flag1.store(true);
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

        // libera la bandera del proceso correspondiente
        if (id == 0) {
            // libera la bandera cero
            flag0.store(false);
        } else {
            // libera la bandera uno
            flag1.store(false);
        }
    }
}

// declara el punto de entrada del programa
int main() {
    // muestra el nombre de la prueba
    imprimir("prueba de banderas comprobar y activar");

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
    // explica el resultado de la prueba
    imprimir("resultado: los dos procesos pueden entrar a la vez");
}
