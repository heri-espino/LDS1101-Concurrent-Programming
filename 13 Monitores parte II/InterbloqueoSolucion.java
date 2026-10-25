public class InterbloqueoSolucion {
    static class Cell {
        private static final Object empate = new Object();
        String nombre;
        int valor;

        Cell(String nombre, int valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public void swap(Cell otra) {
            int a = System.identityHashCode(this);
            int b = System.identityHashCode(otra);

            if (a == b) {
                synchronized (empate) {
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
