public class MetodosThread {
    public static void main(String[] args) throws InterruptedException {
        Thread durmiendo = new Thread(() -> {
            try {
                System.out.println("Hilo durmiendo");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("interrupt(): interrumpio el sleep");
            }
        });

        durmiendo.start();
        Thread.sleep(100);
        durmiendo.interrupt();
        durmiendo.join();

        Thread hilo = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("yield(): paso " + i);
                Thread.yield();
            }
        });
        hilo.start();
        hilo.join();

        Object monitor = new Object();
        System.out.println("holdsLock() fuera: " + Thread.holdsLock(monitor));
        synchronized (monitor) {
            System.out.println("holdsLock() dentro: " + Thread.holdsLock(monitor));
        }
    }
}
