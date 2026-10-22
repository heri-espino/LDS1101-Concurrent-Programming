// version explicada: el programa es igual al de code/java
// importa la clase que administra permisos
import java.util.concurrent.Semaphore;

public class FilosofosComensales_ultracommented {
    static final int N = 5;
    static final int VECES = 3;
    static final Semaphore[] tenedores = new Semaphore[N];
    static boolean mejorada;

    // cada hilo ejecuta esta rutina
    static void filosofo(int i) {
        for (int vuelta = 1; vuelta <= VECES; vuelta++) {
            try {
                System.out.println("Filosofo " + i + " pensando");
                Thread.sleep(30);

                int primero = i;
                int segundo = (i + 1) % N;
                if (mejorada && i == 0) {
                    primero = (i + 1) % N;
                    segundo = i;
                }

                tenedores[primero].acquire();
                System.out.println("Filosofo " + i + " toma tenedor " + primero);
                Thread.sleep(100);
                tenedores[segundo].acquire();
                System.out.println("Filosofo " + i + " comiendo (vuelta " + vuelta + ")");
                Thread.sleep(40);
                tenedores[segundo].release();
                tenedores[primero].release();
                System.out.println("Filosofo " + i + " libera tenedores " + primero + " y " + segundo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        mejorada = args.length > 0 && args[0].equals("mejorada");
        System.out.println(mejorada ? "VERSION MEJORADA" : "VERSION ORIGINAL");
        for (int i = 0; i < N; i++) tenedores[i] = new Semaphore(1);

        Thread[] hilos = new Thread[N];
        for (int i = 0; i < N; i++) {
            final int id = i;
            hilos[i] = new Thread(() -> filosofo(id));
            hilos[i].setDaemon(true); // permite terminar la demostracion si hay interbloqueo
            hilos[i].start();
        }

        long limite = System.currentTimeMillis() + 2000;
        for (Thread hilo : hilos) hilo.join(Math.max(1, limite - System.currentTimeMillis()));
        int pendientes = 0;
        for (Thread hilo : hilos) if (hilo.isAlive()) pendientes++;
        System.out.println("Filosofos sin terminar: " + pendientes);
        if (pendientes > 0) System.out.println("Posible interbloqueo: revisar los tenedores");
    }
}
