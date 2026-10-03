import java.util.concurrent.Semaphore;

public class Ejercicio02LectoresEscritores {

    private static int baseDatos = 0;
    private static int numReaders = 0;

    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore wlock = new Semaphore(1);

    private static void startRead() throws InterruptedException {
        mutex.acquire();

        numReaders++;

        if (numReaders == 1) {
            wlock.acquire();
        }

        mutex.release();
    }

    private static void endRead() throws InterruptedException {
        mutex.acquire();

        numReaders--;

        if (numReaders == 0) {
            wlock.release();
        }

        mutex.release();
    }

    private static void startWrite() throws InterruptedException {
        wlock.acquire();
    }

    private static void endWrite() {
        wlock.release();
    }

    private static void leer() {
        try {
            startRead();

            String nombre = Thread.currentThread().getName();
            System.out.println(nombre + " lee: " + baseDatos);

            Thread.sleep(500);

            endRead();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void escribir(int valor) {
        try {
            startWrite();

            String nombre = Thread.currentThread().getName();

            baseDatos = valor;
            System.out.println(nombre + " escribe: " + baseDatos);

            Thread.sleep(500);

            endWrite();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread lector1 = new Thread(() -> leer(), "Lector 1");
        Thread lector2 = new Thread(() -> leer(), "Lector 2");
        Thread lector3 = new Thread(() -> leer(), "Lector 3");

        Thread escritor1 = new Thread(() -> escribir(100), "Escritor 1");
        Thread escritor2 = new Thread(() -> escribir(200), "Escritor 2");

        lector1.start();
        escritor1.start();
        lector2.start();
        lector3.start();
        escritor2.start();

        lector1.join();
        lector2.join();
        lector3.join();
        escritor1.join();
        escritor2.join();
    }
}