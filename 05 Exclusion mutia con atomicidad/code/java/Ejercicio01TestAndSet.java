import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Ejercicio01TestAndSet {
    private static final int HILOS = 3;
    private static final int ITERACIONES = 5;
    private static final AtomicBoolean lockFlag = new AtomicBoolean(false);
    private static final AtomicInteger contador = new AtomicInteger(0);
    private static final CountDownLatch inicio = new CountDownLatch(1);

    // solicita acceso a la seccion critica mediante getAndSet
    private static void requestCS(int id) {
        while (lockFlag.getAndSet(true)) {
            Thread.onSpinWait();
        }
    }

    // libera el cerrojo al salir de la seccion critica
    private static void releaseCS(int id) {
        lockFlag.getAndSet(false);
    }

    // ejecuta las entradas del hilo a la seccion critica
    private static void proceso(int id) {
        try {
            inicio.await();

            for (int iteracion = 1; iteracion <= ITERACIONES; iteracion++) {
                requestCS(id);

                int valor = contador.incrementAndGet();
                System.out.println("Hilo " + id
                        + " entra a la seccion critica en la iteracion " + iteracion
                        + ", contador = " + valor);

                releaseCS(id);
            }
        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] hilos = new Thread[HILOS];

        System.out.println("prueba de exclusion mutua con test-and-set");

        for (int id = 0; id < HILOS; id++) {
            int idFinal = id;
            hilos[id] = new Thread(() -> proceso(idFinal), "Hilo-" + id);
            hilos[id].start();
        }

        inicio.countDown();

        for (Thread hilo : hilos) {
            hilo.join();
        }

        System.out.println("contador esperado: " + (HILOS * ITERACIONES));
        System.out.println("contador obtenido: " + contador.get());
        System.out.println("resultado: getAndSet garantiza exclusion mutua con un cerrojo booleano");
    }
}
