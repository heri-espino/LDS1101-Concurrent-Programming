import java.util.concurrent.atomic.AtomicInteger;

class CountingSemaphore {
    int value;

    public CountingSemaphore(int initValue) {
        value = initValue;
    }

    // P() solicita uno de los permisos disponibles
    public synchronized void P() {
        value--;

        if (value < 0) {
            Util.mywait(this);
        }
    }

    // V() devuelve un permiso y despierta a un hilo si existe espera
    public synchronized void V() {
        value++;

        if (value <= 0) {
            notify();
        }
    }
}

class TareaImpresion implements Runnable {
    // estos contadores solo comprueban el resultado y no conceden permisos
    private static final AtomicInteger IMPRIMIENDO = new AtomicInteger(0);
    private static final AtomicInteger MAXIMO_SIMULTANEO = new AtomicInteger(0);

    private final CountingSemaphore impresoras;
    private final long tiempoImpresion;

    public TareaImpresion(CountingSemaphore impresoras, long tiempoImpresion) {
        this.impresoras = impresoras;
        this.tiempoImpresion = tiempoImpresion;
    }

    @Override
    public void run() {
        // P() equivale a solicitar una impresora con acquire()
        impresoras.P();

        try {
            int simultaneos = IMPRIMIENDO.incrementAndGet();
            MAXIMO_SIMULTANEO.accumulateAndGet(simultaneos, Math::max);

            if (simultaneos > 3) {
                throw new IllegalStateException(
                        "Hay mas de tres hilos imprimiendo al mismo tiempo");
            }

            String nombre = Thread.currentThread().getName();
            System.out.println(nombre + " comienza a imprimir | simultaneos = "
                    + simultaneos);

            Util.mysleep(tiempoImpresion);

            System.out.println(nombre + " termina de imprimir");
        } finally {
            IMPRIMIENDO.decrementAndGet();
            // V() equivale a liberar la impresora con release()
            impresoras.V();
        }
    }

    public static int getMaximoSimultaneo() {
        return MAXIMO_SIMULTANEO.get();
    }
}

public class Ejercicio02SemaforoContador {
    public static void main(String[] args) throws InterruptedException {
        CountingSemaphore impresoras = new CountingSemaphore(3);
        Thread[] hilos = new Thread[8];

        System.out.println("=== Semaforo contador: tres impresoras ===");

        for (int id = 0; id < hilos.length; id++) {
            hilos[id] = new Thread(
                    new TareaImpresion(impresoras, 350),
                    "Hilo " + (id + 1));
        }

        for (Thread hilo : hilos) {
            hilo.start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        System.out.println("Maximo de hilos imprimiendo al mismo tiempo: "
                + TareaImpresion.getMaximoSimultaneo());
        System.out.println("Limite permitido por el semaforo: 3");
    }
}
