import java.util.concurrent.Semaphore;

public class Ejercicio02SemaforoContadorSemaphore {

    // hay tres impresoras, por eso hay tres permisos
    private static final Semaphore impresoras = new Semaphore(3);

    private static void imprimir() {
        try {
            // pide una impresora
            impresoras.acquire();

            String nombre = Thread.currentThread().getName();

            System.out.println(nombre + " comienza a imprimir");

            // simula que imprimir tarda
            Thread.sleep(1000);

            System.out.println(nombre + " termina de imprimir");

            // devuelve la impresora
            impresoras.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread[] hilos = new Thread[8];

        for (int i = 0; i < hilos.length; i++) {
            hilos[i] = new Thread(() -> imprimir(),"Hilo " + (i + 1));
        }

        for (Thread hilo : hilos) {
            hilo.start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }
    }
} 