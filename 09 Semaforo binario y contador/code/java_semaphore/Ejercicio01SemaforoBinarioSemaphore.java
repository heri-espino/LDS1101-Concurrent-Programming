import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Ejercicio01SemaforoBinarioSemaphore {

    // cuenta bancaria compartida por todos los hilos
    static class CuentaBancaria {
        private double saldo;

        public CuentaBancaria(double saldoInicial) {
            saldo = saldoInicial;
        }

        public double consultarSaldo() {
            return saldo;
        }

        public void depositar(double cantidad) {
            saldo += cantidad;
        }

        public void retirar(double cantidad) {
            saldo -= cantidad;
        }
    }

    // semaforo con un solo permiso
    private static final Semaphore mutex = new Semaphore(1);

    // sirve solamente para comprobar cuantos hilos entran al mismo tiempo
    private static final AtomicInteger hilosEnSeccionCritica =
            new AtomicInteger(0);

    private static final AtomicInteger maximoSimultaneo =
            new AtomicInteger(0);

    private static final CuentaBancaria cuenta =
            new CuentaBancaria(1000.0);

    private static void operacion(String tipo, double cantidad) {

        boolean permisoAdquirido = false;

        try {
            // solicita el unico permiso
            mutex.acquire();
            permisoAdquirido = true;

            // desde aqui comienza la seccion critica
            int simultaneos = hilosEnSeccionCritica.incrementAndGet();
            maximoSimultaneo.accumulateAndGet(simultaneos, Math::max);

            String nombre = Thread.currentThread().getName();

            System.out.printf(
                    "%s entra | saldo antes = $%.2f%n",
                    nombre,
                    cuenta.consultarSaldo()
            );

            // simula que la operacion tarda
            Thread.sleep(120);

            if (tipo.equals("deposito")) {
                cuenta.depositar(cantidad);
                System.out.printf(
                        "%s deposita $%.2f%n",
                        nombre,
                        cantidad
                );
            } else {
                cuenta.retirar(cantidad);
                System.out.printf(
                        "%s retira $%.2f%n",
                        nombre,
                        cantidad
                );
            }

            System.out.printf(
                    "%s sale | saldo despues = $%.2f%n",
                    nombre,
                    cuenta.consultarSaldo()
            );

            hilosEnSeccionCritica.decrementAndGet();

        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();

        } finally {

            // solamente libera si realmente obtuvo el permiso
            if (permisoAdquirido) {
                mutex.release();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Thread[] hilos = {
                new Thread(() -> operacion("deposito", 200), "Hilo A"),
                new Thread(() -> operacion("retiro", 150), "Hilo B"),
                new Thread(() -> operacion("deposito", 300), "Hilo C"),
                new Thread(() -> operacion("retiro", 100), "Hilo D"),
                new Thread(() -> operacion("deposito", 50), "Hilo E")
        };

        System.out.println("=== Semaforo binario con Semaphore ===");
        System.out.printf(
                "Saldo inicial: $%.2f%n",
                cuenta.consultarSaldo()
        );

        for (Thread hilo : hilos) {
            hilo.start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        System.out.println("Saldo final esperado: $1300.00");

        System.out.printf(
                "Saldo final obtenido: $%.2f%n",
                cuenta.consultarSaldo()
        );

        System.out.println(
                "Maximo de hilos en la seccion critica: "
                        + maximoSimultaneo.get()
        );
    }
}