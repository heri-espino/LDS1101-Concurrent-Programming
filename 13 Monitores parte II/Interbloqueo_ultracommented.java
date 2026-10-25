// la barrera permite que cada hilo tenga su primer monitor antes de continuar
import java.util.concurrent.CountDownLatch;

public class Interbloqueo_ultracommented {
    // cada hilo toma el monitor de su propia celda primero
    static final CountDownLatch listos = new CountDownLatch(2);

    // Cell contiene un valor y un monitor asociado al objeto
    static class Cell {
        String nombre;
        int valor;

        Cell(String nombre, int valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public synchronized void swap(Cell otra) {
            System.out.println(Thread.currentThread().getName()
                    + " tiene monitor " + nombre);
            try {
                Thread.sleep(100);
                // sleep facilita el escenario pedido y la barrera lo vuelve reproducible
                listos.countDown();
                listos.await();
                System.out.println(Thread.currentThread().getName()
                        + " espera monitor " + otra.nombre);

                // el segundo synchronized intenta adquirir el monitor del otro objeto
                synchronized (otra) {
                    int temporal = valor;
                    valor = otra.valor;
                    otra.valor = temporal;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // la espera tiene limite para mostrar el bloqueo sin dejar el programa abierto
    public static void main(String[] args) throws InterruptedException {
        Cell p = new Cell("p", 10);
        Cell q = new Cell("q", 20);
        Thread a = new Thread(() -> p.swap(q), "Hilo A");
        Thread b = new Thread(() -> q.swap(p), "Hilo B");
        a.setDaemon(true);
        b.setDaemon(true);
        a.start();
        b.start();

        a.join(600);
        b.join(600);
        System.out.println("Interbloqueo detectado: " + (a.isAlive() && b.isAlive()));
        System.out.println("A tiene p y espera q; B tiene q y espera p");
    }
}
