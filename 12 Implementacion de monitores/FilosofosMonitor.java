public class FilosofosMonitor {
    static class Mesa {
        private final String[] estado = new String[5];
        private final int[] veces = new int[5];

        public Mesa() {
            for (int i = 0; i < 5; i++) estado[i] = "PENSANDO";
        }

        private void test(int i) {
            int izquierda = (i + 4) % 5;
            int derecha = (i + 1) % 5;
            if (estado[i].equals("HAMBRIENTO") &&
                    !estado[izquierda].equals("COMIENDO") &&
                    !estado[derecha].equals("COMIENDO")) {
                estado[i] = "COMIENDO";
            }
        }

        public synchronized void pedirComer(int i) throws InterruptedException {
            estado[i] = "HAMBRIENTO";
            test(i);
            while (!estado[i].equals("COMIENDO")) wait();
            System.out.println("Filosofo " + i + " comiendo");
        }

        public synchronized void terminarComer(int i) {
            estado[i] = "PENSANDO";
            veces[i]++;
            test((i + 4) % 5);
            test((i + 1) % 5);
            notifyAll();
        }

        public synchronized void mostrarResultados() {
            for (int i = 0; i < 5; i++) {
                System.out.println("Filosofo " + i + " comio " + veces[i] + " veces");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Mesa mesa = new Mesa();
        Thread[] hilos = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int id = i;
            hilos[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 20; j++) {
                        Thread.sleep((int) (Math.random() * 20)); // pensando
                        mesa.pedirComer(id);
                        Thread.sleep((int) (Math.random() * 20)); // comiendo
                        mesa.terminarComer(id);
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            hilos[i].start();
        }
        for (Thread hilo : hilos) hilo.join();
        mesa.mostrarResultados();
    }
}
