public class VentaBoletos {
    static class Evento {
        private final String nombre;
        private int disponibles;
        private static int totalVentas = 0;

        Evento(String nombre, int disponibles) {
            this.nombre = nombre;
            this.disponibles = disponibles;
        }

        public synchronized void reservarMetodo() {
            if (disponibles > 0) {
                disponibles--;
                registrarVenta();
                System.out.println(nombre + " (metodo): vendido");
            }
        }

        public void reservarBloque() {
            synchronized (this) {
                if (disponibles > 0) {
                    disponibles--;
                    registrarVenta();
                    System.out.println(nombre + " (bloque): vendido");
                }
            }
        }

        private static synchronized void registrarVenta() {
            totalVentas++;
        }

        public int disponibles() {
            return disponibles;
        }

        public static int totalVentas() {
            return totalVentas;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Evento concierto = new Evento("Concierto", 6);
        Evento teatro = new Evento("Teatro", 6);
        Thread[] hilos = new Thread[20];

        for (int i = 0; i < 10; i++) {
            hilos[i] = new Thread(() -> concierto.reservarMetodo());
            hilos[i + 10] = new Thread(() -> teatro.reservarBloque());
        }

        for (Thread hilo : hilos) hilo.start();
        for (Thread hilo : hilos) hilo.join();

        System.out.println("Boletos concierto restantes: " + concierto.disponibles());
        System.out.println("Boletos teatro restantes: " + teatro.disponibles());
        System.out.println("Ventas totales: " + Evento.totalVentas());
    }
}
