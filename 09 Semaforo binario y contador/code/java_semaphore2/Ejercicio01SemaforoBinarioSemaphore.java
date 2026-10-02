import java.util.concurrent.Semaphore;

public class Ejercicio01SemaforoBinarioSemaphore {

    private static double saldo = 1000.0;
    private static final Semaphore sem = new Semaphore(1);

    private static void depositar(double cantidad) {
        try {
            sem.acquire();

            String name = Thread.currentThread().getName();

            System.out.println(name + " entra | saldo = $" + saldo); 

            saldo += cantidad;

            System.out.println(name + " deposita $" + cantidad + " | saldo = $" + saldo);

            sem.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void retirar(double cantidad) {
        try {
            sem.acquire();

            String name = Thread.currentThread().getName();
            
            System.out.println(name + " entra | saldo = $" + saldo);

            saldo -= cantidad;

            System.out.println(name + " retira $" + cantidad + " | saldo = $" + saldo);

            sem.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread h1 = new Thread(() -> depositar(200), "Hilo 1");
        Thread h2 = new Thread(() -> retirar(150), "Hilo 2");
        Thread h3 = new Thread(() -> depositar(300), "Hilo 3");
        Thread h4 = new Thread(() -> retirar(100), "Hilo 4");
        Thread h5 = new Thread(() -> depositar(50), "Hilo 5");

        h1.start();
        h2.start();
        h3.start();
        h4.start();
        h5.start();

        h1.join();
        h2.join();
        h3.join();
        h4.join();
        h5.join();

        System.out.println("Saldo final: $" + saldo);
    }
}