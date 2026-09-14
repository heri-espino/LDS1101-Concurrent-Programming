// importa el cerrojo de inicio para coordinar la prueba
import java.util.concurrent.CountDownLatch;
// importa el tipo booleano con operaciones atomicas
import java.util.concurrent.atomic.AtomicBoolean;
// importa el contador con operaciones atomicas
import java.util.concurrent.atomic.AtomicInteger;

// declara la clase principal del ejercicio
public class Ejercicio01TestAndSet {
    // define la cantidad de hilos de la prueba
    private static final int HILOS = 3;
    // define la cantidad de entradas de cada hilo
    private static final int ITERACIONES = 5;
    // declara el cerrojo booleano compartido e inicialmente libre
    private static final AtomicBoolean lockFlag = new AtomicBoolean(false);
    // declara el contador compartido
    private static final AtomicInteger contador = new AtomicInteger(0);
    // declara el mecanismo para iniciar la prueba al mismo tiempo
    private static final CountDownLatch inicio = new CountDownLatch(1);

    // solicita acceso a la seccion critica mediante getAndSet
    private static void requestCS(int id) {
        // repite mientras getAndSet encuentre el cerrojo ocupado
        while (lockFlag.getAndSet(true)) {
            // conserva la espera activa hasta que el cerrojo quede libre
            Thread.onSpinWait();
        }
    }

    // libera el cerrojo al salir de la seccion critica
    private static void releaseCS(int id) {
        // cambia el cerrojo booleano a false
        lockFlag.getAndSet(false);
    }

    // ejecuta las entradas del hilo a la seccion critica
    private static void proceso(int id) {
        // inicia el bloque que atiende una interrupcion
        try {
            // espera hasta que main libere el inicio de la prueba
            inicio.await();

            // repite cinco veces el acceso a la seccion critica
            for (int iteracion = 1; iteracion <= ITERACIONES; iteracion++) {
                // solicita el cerrojo
                requestCS(id);

                // incrementa el contador dentro de la seccion critica
                int valor = contador.incrementAndGet();
                // muestra el hilo y el valor obtenido
                System.out.println("Hilo " + id
                        + " entra a la seccion critica en la iteracion " + iteracion
                        + ", contador = " + valor);

                // libera el cerrojo
                releaseCS(id);
            }
        // captura la interrupcion del hilo
        } catch (InterruptedException excepcion) {
            // conserva la marca de interrupcion
            Thread.currentThread().interrupt();
        }
    }

    // declara el punto de entrada del programa
    public static void main(String[] args) throws InterruptedException {
        // crea el arreglo de hilos
        Thread[] hilos = new Thread[HILOS];

        // muestra el nombre de la prueba
        System.out.println("prueba de exclusion mutua con test-and-set");

        // recorre los identificadores de los hilos
        for (int id = 0; id < HILOS; id++) {
            // copia el identificador para la tarea del hilo
            int idFinal = id;
            // crea el hilo con su identificador
            hilos[id] = new Thread(() -> proceso(idFinal), "Hilo-" + id);
            // inicia el hilo creado
            hilos[id].start();
        }

        // permite que los hilos intenten entrar al mismo tiempo
        inicio.countDown();

        // recorre los hilos creados
        for (Thread hilo : hilos) {
            // espera a que termine el hilo actual
            hilo.join();
        }

        // muestra el valor esperado del contador
        System.out.println("contador esperado: " + (HILOS * ITERACIONES));
        // muestra el valor obtenido del contador
        System.out.println("contador obtenido: " + contador.get());
        // explica el resultado de la prueba
        System.out.println("resultado: getAndSet garantiza exclusion mutua con un cerrojo booleano");
    }
}
