public class InterbloqueoSolucion_ultracommented {
    // los dos hilos deben adquirir los monitores en el mismo orden
    static class Cell {
        // si los codigos de identidad empatan se usa un monitor extra
        private static final Object empate = new Object();
        String nombre;
        int valor;

        Cell(String nombre, int valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public void swap(Cell otra) {
            // identityHashCode da el codigo para decidir el primer monitor
            int a = System.identityHashCode(this);
            int b = System.identityHashCode(otra);

            if (a == b) {
                synchronized (empate) {
            // doSwap cambia los valores mientras ambos monitores estan ocupados
                    synchronized (this) {
                        synchronized (otra) {
                            doSwap(otra);
                        }
                    }
                }
                return;
            }

            Cell primero = a < b ? this : otra;
            Cell segundo = a < b ? otra : this;

            synchronized (primero) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (segundo) {
                    primero.doSwap(segundo);
                }
            }
        }

        private void doSwap(Cell otra) {
            int temporal = valor;
            valor = otra.valor;
            otra.valor = temporal;
            System.out.println("Intercambio completado: "
                    + nombre + "=" + valor + ", "
                    + otra.nombre + "=" + otra.valor);
        }
    }

    // start crea los hilos y join espera que ambos finalicen
    public static void main(String[] args) throws InterruptedException {
        Cell p = new Cell("p", 10);
        Cell q = new Cell("q", 20);
        Thread a = new Thread(() -> p.swap(q), "Hilo A");
        Thread b = new Thread(() -> q.swap(p), "Hilo B");
        a.start();
        b.start();
        a.join();
        b.join();

        System.out.println("Ambos hilos terminaron: " + (!a.isAlive() && !b.isAlive()));
        System.out.println("Valores finales: p=" + p.valor + ", q=" + q.valor);
    }
}
