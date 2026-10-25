import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ColaEnlazada {
    static class Nodo {
        String dato;
        Nodo siguiente;

        Nodo(String dato) {
            this.dato = dato;
        }
    }

    static class Cola {
        private Nodo head;
        private Nodo tail;

        public synchronized void enqueue(String dato) {
            Nodo nuevo = new Nodo(dato);
            if (tail == null) head = nuevo;
            else tail.siguiente = nuevo;
            tail = nuevo;
            System.out.println("Deposita " + dato);
            notifyAll();
        }

        public synchronized String dequeue() throws InterruptedException {
            while (head == null) wait();
            String dato = head.dato;
            head = head.siguiente;
            if (head == null) tail = null;
            System.out.println("Retira " + dato);
            return dato;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Cola cola = new Cola();
        Set<String> retirados = Collections.synchronizedSet(new HashSet<>());
        Thread[] hilos = new Thread[4];

        for (int i = 0; i < 2; i++) {
            final int id = i + 1;
            hilos[i] = new Thread(() -> {
                for (int j = 1; j <= 10; j++) {
                    cola.enqueue("P" + id + "-" + j);
                }
            }, "Productor " + id);
        }

        for (int i = 2; i < 4; i++) {
            hilos[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        retirados.add(cola.dequeue());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Consumidor " + (i - 1));
        }

        for (Thread hilo : hilos) hilo.start();
        for (Thread hilo : hilos) hilo.join();

        boolean correcto = retirados.size() == 20;
        for (int p = 1; p <= 2; p++) {
            for (int i = 1; i <= 15; i++) {
                if (!retirados.contains("P" + p + "-" + i)) correcto = false;
            }
        }
        System.out.println("Elementos unicos retirados: " + retirados.size());
        System.out.println("Sin perdidas ni duplicados: " + correcto);
    }
}
