// importa contadores atomicos usados solamente para comprobar la concurrencia
import java.util.concurrent.atomic.AtomicInteger;

// declara el semaforo contador siguiendo la estructura del pseudocodigo de clase
class CountingSemaphore {
    // guarda los permisos y puede ser negativo cuando existen hilos esperando
    int value;

    // recibe la cantidad inicial de recursos disponibles
    public CountingSemaphore(int initValue) {
        // copia el valor inicial del semaforo
        value = initValue;
    }

    // solicita uno de los permisos disponibles
    public synchronized void P() {
        // descuenta un permiso por la solicitud actual
        value--;

        // comprueba si ya no habia un recurso disponible
        if (value < 0) {
            // bloquea el hilo como indica el pseudocodigo
            Util.mywait(this);
        }
    }

    // devuelve un permiso
    public synchronized void V() {
        // aumenta el contador porque un recurso fue liberado
        value++;

        // un valor menor o igual que cero indica que hay espera
        if (value <= 0) {
            // despierta a uno de los hilos bloqueados
            notify();
        }
    }
}

// declara la tarea ejecutada por cada hilo que desea imprimir
class TareaImpresion implements Runnable {
    // cuenta cuantos hilos tienen impresora en este instante
    // solo verifica el resultado y no concede permisos
    private static final AtomicInteger IMPRIMIENDO =
            new AtomicInteger(0);
    // conserva el mayor numero de impresiones simultaneas
    private static final AtomicInteger MAXIMO_SIMULTANEO =
            new AtomicInteger(0);

    // guarda el semaforo compartido
    private final CountingSemaphore impresoras;
    // guarda el tiempo usado para simular la impresion
    private final long tiempoImpresion;

    // recibe el semaforo y el tiempo de trabajo
    public TareaImpresion(CountingSemaphore impresoras, long tiempoImpresion) {
        // conserva el semaforo compartido
        this.impresoras = impresoras;
        // conserva el tiempo de impresion
        this.tiempoImpresion = tiempoImpresion;
    }

    // implementa el trabajo de cada hilo
    @Override
    public void run() {
        // solicita una impresora antes de usarla
        // P corresponde conceptualmente a acquire
        impresoras.P();

        // garantiza la devolucion del permiso
        try {
            // registra que este hilo ya tiene una impresora
            int simultaneos = IMPRIMIENDO.incrementAndGet();
            // actualiza el maximo observado
            MAXIMO_SIMULTANEO.accumulateAndGet(simultaneos, Math::max);

            // comprueba que nunca se superen las tres impresoras
            if (simultaneos > 3) {
                // reporta una falla del control de recursos
                throw new IllegalStateException(
                        "Hay mas de tres hilos imprimiendo al mismo tiempo");
            }

            // obtiene el nombre del hilo
            String nombre = Thread.currentThread().getName();
            // informa el inicio y la cantidad simultanea
            System.out.println(nombre + " comienza a imprimir | simultaneos = "
                    + simultaneos);

            // simula el tiempo durante el que la impresora esta ocupada
            Util.mysleep(tiempoImpresion);

            // informa que el hilo termino
            System.out.println(nombre + " termina de imprimir");
        // ejecuta siempre la liberacion del recurso
        } finally {
            // registra que el hilo dejo de imprimir
            IMPRIMIENDO.decrementAndGet();
            // devuelve el permiso
            // V corresponde conceptualmente a release
            impresoras.V();
        }
    }

    // devuelve el maximo observado
    public static int getMaximoSimultaneo() {
        // entrega el valor de comprobacion
        return MAXIMO_SIMULTANEO.get();
    }
}

// declara el programa principal de la parte del semaforo contador
public class Ejercicio02SemaforoContador {
    // declara el punto de entrada
    public static void main(String[] args) throws InterruptedException {
        // crea el semaforo con tres permisos por las tres impresoras
        CountingSemaphore impresoras = new CountingSemaphore(3);
        // reserva espacio para los ocho hilos solicitados
        Thread[] hilos = new Thread[8];

        // muestra el nombre de la prueba
        System.out.println("=== Semaforo contador: tres impresoras ===");

        // crea los ocho hilos
        for (int id = 0; id < hilos.length; id++) {
            // asocia a cada hilo una tarea que comparte el semaforo
            hilos[id] = new Thread(
                    // simula 350 milisegundos de impresion
                    new TareaImpresion(impresoras, 350),
                    // asigna un nombre legible
                    "Hilo " + (id + 1));
        }

        // inicia todos los hilos
        for (Thread hilo : hilos) {
            // permite que compitan por los tres permisos
            hilo.start();
        }

        // espera a todos los hilos
        for (Thread hilo : hilos) {
            // evita terminar main antes de que acaben las impresiones
            hilo.join();
        }

        // muestra el maximo real de impresiones simultaneas
        System.out.println("Maximo de hilos imprimiendo al mismo tiempo: "
                + TareaImpresion.getMaximoSimultaneo());
        // muestra el limite impuesto por el semaforo
        System.out.println("Limite permitido por el semaforo: 3");
    }
}
