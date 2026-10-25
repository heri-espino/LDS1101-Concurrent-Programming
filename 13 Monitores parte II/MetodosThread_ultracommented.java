public class MetodosThread_ultracommented {
    // aqui se muestran los tres metodos solicitados
    public static void main(String[] args) throws InterruptedException {
        // creamos un hilo que entrara en sleep
        Thread durmiendo = new Thread(() -> {
            try {
                System.out.println("Hilo durmiendo");
                // el hilo intenta dormir durante dos segundos
                Thread.sleep(2000);
            // la interrupcion despierta al hilo de sleep
            } catch (InterruptedException e) {
                System.out.println("interrupt(): interrumpio el sleep");
            }
        });

        // arranca el hilo que va a dormir
        durmiendo.start();
        // el principal espera un momento antes de interrumpirlo
        Thread.sleep(100);
        // interrupt envia una solicitud de interrupcion
        durmiendo.interrupt();
        // el principal espera que termine el hilo interrumpido
        durmiendo.join();

        // creamos otro hilo para mostrar yield
        Thread hilo = new Thread(() -> {
            // repetimos tres pasos de ejecucion
            for (int i = 1; i <= 3; i++) {
                System.out.println("yield(): paso " + i);
                // yield sugiere ceder cpu pero no obliga al planificador
                Thread.yield();
            }
        });
        // arrancamos el ejemplo de yield
        hilo.start();
        // esperamos a que termine antes del siguiente ejemplo
        hilo.join();

        // cualquier objeto de java cuenta con un monitor intrinseco
        Object monitor = new Object();
        // fuera de synchronized el hilo no tiene este monitor
        System.out.println("holdsLock() fuera: " + Thread.holdsLock(monitor));
        // al entrar al bloque se adquiere el monitor
        synchronized (monitor) {
            // dentro del bloque holdsLock devuelve true
            System.out.println("holdsLock() dentro: " + Thread.holdsLock(monitor));
        }
    }
}
