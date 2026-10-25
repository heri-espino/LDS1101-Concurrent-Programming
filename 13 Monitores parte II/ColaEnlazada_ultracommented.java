// permite crear un conjunto que puede usarse desde varios hilos
import java.util.Collections;
// guarda los identificadores retirados sin repetirlos
import java.util.HashSet;
// representa un conjunto de elementos unicos
import java.util.Set;

public class ColaEnlazada_ultracommented {
    static class Nodo {
        // este atributo guarda el elemento depositado
        String dato;
        Nodo siguiente;

        Nodo(String dato) {
            // guarda el elemento recibido en el nodo
            this.dato = dato;
        }
    }

    // la cola es el recurso compartido y funciona como monitor
    static class Cola {
        private Nodo head;
        private Nodo tail;

        // solo un hilo a la vez puede insertar y cambiar los enlaces
        public synchronized void enqueue(String dato) {
            Nodo nuevo = new Nodo(dato);
            // si no hay ultimo nodo entonces la cola estaba vacia
            if (tail == null) head = nuevo;
            // si habia nodos conectamos el ultimo con el nuevo
            else tail.siguiente = nuevo;
            // el nuevo nodo se convierte en el ultimo
            tail = nuevo;
            System.out.println("Deposita " + dato);
            // despierta a los consumidores que esperan elementos
            notifyAll();
        }

        // solo un hilo a la vez puede retirar elementos
        public synchronized String dequeue() throws InterruptedException {
            // si esta vacia espera y libera temporalmente el monitor
            while (head == null) wait();
            // guardamos el dato antes de retirar el primer nodo
            String dato = head.dato;
            // el segundo nodo pasa a ser el primero
            head = head.siguiente;
            // si se retiro el ultimo elemento la cola queda vacia
            if (head == null) tail = null;
            System.out.println("Retira " + dato);
            // devuelve el elemento retirado al consumidor
            return dato;
        }
    }

    // creamos los cuatro hilos que compartiran la misma cola
    public static void main(String[] args) throws InterruptedException {
        Cola cola = new Cola();
        // el conjunto verifica los elementos retirados sin carreras
        Set<String> retirados = Collections.synchronizedSet(new HashSet<>());
        // guardamos las referencias de los dos productores y dos consumidores
        Thread[] hilos = new Thread[4];

        // creamos un hilo por cada uno de los dos productores
        for (int i = 0; i < 2; i++) {
            final int id = i + 1;
            hilos[i] = new Thread(() -> {
                for (int j = 1; j <= 10; j++) {
                    // cada productor deposita diez elementos en la cola
                    cola.enqueue("P" + id + "-" + j);
                }
            }, "Productor " + id);
        }

        // creamos los dos consumidores
        for (int i = 2; i < 4; i++) {
            hilos[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        // el consumidor extrae un dato y lo registra para comprobarlo
                        retirados.add(cola.dequeue());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Consumidor " + (i - 1));
        }

        // iniciamos los cuatro hilos
        for (Thread hilo : hilos) hilo.start();
        // esperamos a que todos terminen antes de verificar
        for (Thread hilo : hilos) hilo.join();

        // debe haber veinte elementos distintos despues de consumir
        boolean correcto = retirados.size() == 20;
        for (int p = 1; p <= 2; p++) {
            for (int i = 1; i <= 10; i++) {
                // comprueba que no falte ningun identificador esperado
                if (!retirados.contains("P" + p + "-" + i)) correcto = false;
            }
        }
        System.out.println("Elementos unicos retirados: " + retirados.size());
        System.out.println("Sin perdidas ni duplicados: " + correcto);
    }
}
