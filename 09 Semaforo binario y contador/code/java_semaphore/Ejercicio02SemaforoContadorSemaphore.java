import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Ejercicio02SemaforoContadorSemaphore {

    // tres permisos porque existen tres impresoras
    private static final Semaphore impresoras =
            new Semaphore(3);

    // sirven solamente para comprobar cuantos hilos imprimen
    private static final AtomicInteger imprimiendo =
            new AtomicInteger(0);

    private static final AtomicInteger maximoSimultaneo =
            new AtomicInteger(0);

    private static void imprimir() {

        boolean permisoAdquirido = false;

        try {
            // solicita una de las tres impresoras
            impresoras.acquire();
            permisoAdquirido = true;

            int simultaneos = imprimiendo.incrementAndGet();

            maximoSimultaneo.accumulateAndGet(
                    simultaneos,
                    Math::max
            );

            String nombre = Thread.currentThread().getName();

            System.out.println(
                    nombre
                            + " comienza a imprimir | simultaneos = "
                            + simultaneos
            );

            // simula el tiempo de impresion
            Thread.sleep(350);

            System.out.println(
                    nombre + " termina de imprimir"
            );

            imprimiendo.decrementAndGet();

        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();

        } finally {

            // devuelve la impresora
            if (permisoAdquirido) {
                impresoras.release();
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Thread[] hilos = new Thread[8];

        System.out.println(
                "=== Semaforo contador con Semaphore ==="
        );

        for (int id = 0; id < hilos.length; id++) {

            hilos[id] = new Thread(
                    () -> imprimir(),
                    "Hilo " + (id + 1)
            );
        }

        for (Thread hilo : hilos) {
            hilo.start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        System.out.println(
                "Maximo de hilos imprimiendo al mismo tiempo: "
                        + maximoSimultaneo.get()
        );

        System.out.println(
                "Limite permitido por el semaforo: 3"
        );
    }
}