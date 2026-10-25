public class InterbloqueoSolucion_ultracommented {
    // cada celda tiene un valor y un monitor
    static class Cell {
        // este monitor resuelve el caso raro de codigos iguales
        private static final Object empate = new Object();
        // se usa para mostrar p y q en la salida
        String nombre;
        // almacena el dato compartido que se intercambiara
        int valor;

        Cell(String nombre, int valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        // el metodo decide el orden antes de tomar los monitores
        public void swap(Cell otra) {
            // obtenemos el codigo de identidad del primer objeto
            int a = System.identityHashCode(this);
            // obtenemos el codigo del otro objeto
            int b = System.identityHashCode(otra);

            // con codigos iguales necesitamos un cerrojo de desempate
            if (a == b) {
                // solo uno puede ordenar y tomar ambos monitores en caso de empate
                synchronized (empate) {
                    synchronized (this) {
                        synchronized (otra) {
                            doSwap(otra);
                        }
                    }
                }
                return;
            }

            // siempre elegimos primero el objeto con el codigo menor
            Cell primero = a < b ? this : otra;
            // el segundo objeto es el que tiene el codigo mayor
            Cell segundo = a < b ? otra : this;

            // ambos hilos solicitan el mismo primer monitor
            synchronized (primero) {
                try {
                    // la pausa demuestra que el orden fijo funciona aun esperando
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // despues tomamos el segundo monitor sin crear espera circular
                synchronized (segundo) {
                    // el objeto de codigo menor realiza el intercambio
                    primero.doSwap(segundo);
                }
            }
        }

        // solo debe ejecutarse cuando se tienen los dos monitores
        private void doSwap(Cell otra) {
            // guardamos uno de los valores para no perderlo
            int temporal = valor;
            // copiamos el segundo valor en el primero
            valor = otra.valor;
            // completamos el intercambio usando el temporal
            otra.valor = temporal;
            System.out.println("Intercambio completado: "
                    + nombre + "=" + valor + ", "
                    + otra.nombre + "=" + otra.valor);
        }
    }

    // dos hilos intercambian simultaneamente las mismas celdas
    public static void main(String[] args) throws InterruptedException {
        Cell p = new Cell("p", 10);
        Cell q = new Cell("q", 20);
        Thread a = new Thread(() -> p.swap(q), "Hilo A");
        Thread b = new Thread(() -> q.swap(p), "Hilo B");
        // iniciamos el primer intercambio
        a.start();
        // iniciamos el segundo intercambio
        b.start();
        // esperamos que termine el hilo a
        a.join();
        // esperamos que termine el hilo b
        b.join();

        // comprobamos que los dos hilos terminaron
        System.out.println("Ambos hilos terminaron: " + (!a.isAlive() && !b.isAlive()));
        System.out.println("Valores finales: p=" + p.valor + ", q=" + q.valor);
    }
}
