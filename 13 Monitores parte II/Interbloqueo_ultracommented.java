// permite coordinar cuando los dos hilos intentaran el segundo monitor
import java.util.concurrent.CountDownLatch;

public class Interbloqueo_ultracommented {
    // la barrera espera a que ambos posean primero monitores distintos
    static final CountDownLatch listos = new CountDownLatch(2);

    // cada celda es un objeto con un monitor propio
    static class Cell {
        // identificamos al objeto como p o q
        String nombre;
        // este es el dato que se intenta intercambiar
        int valor;

        // inicializamos cada celda
        Cell(String nombre, int valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        // al entrar se toma automaticamente el monitor de this
        public synchronized void swap(Cell otra) {
            System.out.println(Thread.currentThread().getName()
                    + " tiene monitor " + nombre);
            try {
                // la pausa facilita observar el interbloqueo
                Thread.sleep(100);
                // informa que el hilo ya tomo su primer monitor
                listos.countDown();
                // espera al otro hilo sin liberar el monitor obtenido
                listos.await();
                System.out.println(Thread.currentThread().getName()
                        + " espera monitor " + otra.nombre);

                // el hilo necesita ahora el monitor que ya tiene el otro
                synchronized (otra) {
                    // si pudiera entrar aqui comenzaria el intercambio
                    int temporal = valor;
                    valor = otra.valor;
                    otra.valor = temporal;
                }
            // java exige manejar la interrupcion de sleep y await
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // creamos dos celdas y dos hilos
    public static void main(String[] args) throws InterruptedException {
        // los valores iniciales permiten comprobar un intercambio
        Cell p = new Cell("p", 10);
        // los valores iniciales permiten comprobar un intercambio
        Cell q = new Cell("q", 20);
        // el hilo a toma primero el monitor de p
        Thread a = new Thread(() -> p.swap(q), "Hilo A");
        // el hilo b toma primero el monitor de q
        Thread b = new Thread(() -> q.swap(p), "Hilo B");
        // los hilos daemon permiten cerrar la demostracion del bloqueo
        a.setDaemon(true);
        // el segundo hilo tambien se marca como daemon
        b.setDaemon(true);
        // inicia el hilo a
        a.start();
        // inicia el hilo b
        b.start();

        // esperamos un tiempo limitado y revisamos el estado
        a.join(600);
        // esperamos sin dejar el programa bloqueado para siempre
        b.join(600);
        // si ambos siguen vivos el ejemplo quedo interbloqueado
        System.out.println("Interbloqueo detectado: " + (a.isAlive() && b.isAlive()));
        System.out.println("A tiene p y espera q; B tiene q y espera p");
    }
}
