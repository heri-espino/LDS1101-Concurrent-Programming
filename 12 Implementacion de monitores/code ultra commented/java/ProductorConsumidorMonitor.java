// version explicada: el programa es igual al de code/java
public class ProductorConsumidorMonitor {
    // el monitor controla el buffer completo
    static class Buffer {
        private final int[] datos = new int[5];
        private final int[] vecesConsumido = new int[41];
        private int inBuf = 0, outBuf = 0, cantidad = 0;

        // solo un hilo ejecuta este metodo a la vez
        public synchronized void deposit(int valor) throws InterruptedException {
            while (cantidad == datos.length) wait();
            datos[inBuf] = valor;
            inBuf = (inBuf + 1) % datos.length;
            cantidad++;
            System.out.println("Deposita " + valor + " | en buffer: " + cantidad);
            notifyAll();
        }

        // si no hay elementos espera sin ocupar el monitor
        public synchronized int fetch() throws InterruptedException {
            while (cantidad == 0) wait();
            int valor = datos[outBuf];
            outBuf = (outBuf + 1) % datos.length;
            cantidad--;
            vecesConsumido[valor]++;
            System.out.println("Retira " + valor + " | en buffer: " + cantidad);
            notifyAll();
            return valor;
        }

        public synchronized boolean todoCorrecto() {
            if (cantidad != 0) return false;
            for (int i = 1; i <= 40; i++) {
                if (vecesConsumido[i] != 1) return false;
            }
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Buffer buffer = new Buffer();
        Thread[] hilos = new Thread[4];

        for (int i = 0; i < 2; i++) {
            final int productor = i;
            hilos[i] = new Thread(() -> {
                try {
                    for (int j = 1; j <= 20; j++) {
                        buffer.deposit(productor * 20 + j);
                        Thread.sleep((int) (Math.random() * 20));
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }, "Productor " + (i + 1));
        }

        for (int i = 2; i < 4; i++) {
            hilos[i] = new Thread(() -> {
                try {
                    for (int j = 1; j <= 20; j++) {
                        buffer.fetch();
                        Thread.sleep((int) (Math.random() * 20));
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }, "Consumidor " + (i - 1));
        }

        for (Thread hilo : hilos) hilo.start();
        for (Thread hilo : hilos) hilo.join();
        System.out.println("40 elementos consumidos exactamente una vez: " + buffer.todoCorrecto());
    }
}
