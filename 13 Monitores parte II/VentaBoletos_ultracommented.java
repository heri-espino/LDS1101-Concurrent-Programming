public class VentaBoletos_ultracommented {
    // cada objeto evento tiene sus propios boletos disponibles
    static class Evento {
        // guarda el nombre del evento
        private final String nombre;
        // cuenta los boletos disponibles de este evento
        private int disponibles;
        // este contador es compartido por todos los eventos
        private static int totalVentas = 0;

        // inicializamos el nombre y la cantidad de boletos
        Evento(String nombre, int disponibles) {
            this.nombre = nombre;
            this.disponibles = disponibles;
        }

        // el metodo bloquea el monitor del evento durante toda la reserva
        public synchronized void reservarMetodo() {
            // solo se puede reservar si quedan boletos
            if (disponibles > 0) {
                // descontamos un boleto del evento actual
                disponibles--;
                // tambien actualizamos la suma de ventas global
                registrarVenta();
                System.out.println(nombre + " (metodo): vendido");
            }
        }

        // aqui el metodo no esta sincronizado completo
        public void reservarBloque() {
            // este bloque toma el mismo monitor de instancia
            synchronized (this) {
                // solo se puede reservar si quedan boletos
                if (disponibles > 0) {
                    // descontamos un boleto del evento actual
                    disponibles--;
                    // tambien actualizamos la suma de ventas global
                    registrarVenta();
                    System.out.println(nombre + " (bloque): vendido");
                }
            }
        }

        // un metodo static synchronized usa el monitor de Evento.class
        private static synchronized void registrarVenta() {
            // agregamos una venta al contador compartido
            totalVentas++;
        }

        // permite consultar cuantos boletos quedaron despues del join
        public int disponibles() {
            return disponibles;
        }

        // permite conocer el total de ambos eventos
        public static int totalVentas() {
            return totalVentas;
        }
    }

    // creamos dos eventos diferentes y varios hilos compradores
    public static void main(String[] args) throws InterruptedException {
        // cada evento comienza con seis boletos
        Evento concierto = new Evento("Concierto", 6);
        // cada evento comienza con seis boletos
        Evento teatro = new Evento("Teatro", 6);
        // preparamos veinte intentos de compra simultaneos
        Thread[] hilos = new Thread[20];

        // diez hilos intentan comprar para cada evento
        for (int i = 0; i < 10; i++) {
            // para concierto usamos el metodo sincronizado
            hilos[i] = new Thread(() -> concierto.reservarMetodo());
            // para teatro usamos el bloque synchronized(this)
            hilos[i + 10] = new Thread(() -> teatro.reservarBloque());
        }

        // iniciamos los intentos de compra
        for (Thread hilo : hilos) hilo.start();
        // esperamos a todos antes de consultar los resultados
        for (Thread hilo : hilos) hilo.join();

        System.out.println("Boletos concierto restantes: " + concierto.disponibles());
        System.out.println("Boletos teatro restantes: " + teatro.disponibles());
        System.out.println("Ventas totales: " + Evento.totalVentas());
    }
}
