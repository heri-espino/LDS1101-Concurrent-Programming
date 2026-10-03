import java.util.concurrent.Semaphore;

public class Ejercicio01ProductorConsumidor {

    private static final int[] buffer = new int[5];
    private static int inBuf = 0;
    private static int outBuf = 0;

    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore espacios = new Semaphore(5);
    private static final Semaphore elementos = new Semaphore(0);

    private static void producir(int elemento) {
        try {
            espacios.acquire(); // espera si el buffer esta lleno
            mutex.acquire();    // entra al buffer

            buffer[inBuf] = elemento;
            System.out.println("Productor deposita " + elemento + " en " + inBuf);
            inBuf = (inBuf + 1) % 5;

            mutex.release();
            elementos.release(); // ahora hay un elemento mas

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void consumir() {
        try {
            elementos.acquire(); // espera si el buffer esta vacio
            mutex.acquire();     // entra al buffer

            int elemento = buffer[outBuf];
            System.out.println("Consumidor retira " + elemento + " de " + outBuf);
            outBuf = (outBuf + 1) % 5;

            mutex.release();
            espacios.release(); // ahora hay un espacio mas

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread productor = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                producir(i);
            }
        });

        Thread consumidor = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                consumir();
            }
        });

        productor.start();
        consumidor.start();

        productor.join();
        consumidor.join();
    }
}