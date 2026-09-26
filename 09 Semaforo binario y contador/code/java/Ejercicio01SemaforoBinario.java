import java.util.concurrent.atomic.AtomicInteger;

class BinarySemaphore {
    boolean value;

    public BinarySemaphore(boolean initValue) {
        value = initValue;
    }

    // P() solicita el unico permiso del semaforo
    public synchronized void P() {
        while (value == false) {
            Util.mywait(this);
        }

        value = false;
    }

    // V() libera el permiso y despierta a un hilo bloqueado
    public synchronized void V() {
        value = true;
        notify();
    }
}

class CuentaBancaria {
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
        if (cantidad > saldo) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        saldo -= cantidad;
    }
}

class OperacionBancaria implements Runnable {
    public enum Tipo {
        DEPOSITO,
        RETIRO
    }

    // estos contadores solo comprueban el resultado y no controlan el acceso
    private static final AtomicInteger HILOS_EN_SECCION_CRITICA = new AtomicInteger(0);
    private static final AtomicInteger MAXIMO_SIMULTANEO = new AtomicInteger(0);

    private final BinarySemaphore mutex;
    private final CuentaBancaria cuenta;
    private final Tipo tipo;
    private final double cantidad;

    public OperacionBancaria(BinarySemaphore mutex, CuentaBancaria cuenta,
            Tipo tipo, double cantidad) {
        this.mutex = mutex;
        this.cuenta = cuenta;
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    @Override
    public void run() {
        // P() equivale a solicitar el permiso con acquire()
        mutex.P();

        try {
            int simultaneos = HILOS_EN_SECCION_CRITICA.incrementAndGet();
            MAXIMO_SIMULTANEO.accumulateAndGet(simultaneos, Math::max);

            if (simultaneos > 1) {
                throw new IllegalStateException(
                        "Mas de un hilo entro a la seccion critica");
            }

            double saldoAntes = cuenta.consultarSaldo();
            String nombre = Thread.currentThread().getName();

            System.out.printf("%s entra | saldo antes = $%.2f%n",
                    nombre, saldoAntes);

            // hace visible que los demas hilos deben esperar el permiso
            Util.mysleep(120);

            if (tipo == Tipo.DEPOSITO) {
                cuenta.depositar(cantidad);
                System.out.printf("%s deposita $%.2f%n", nombre, cantidad);
            } else {
                cuenta.retirar(cantidad);
                System.out.printf("%s retira $%.2f%n", nombre, cantidad);
            }

            System.out.printf("%s sale  | saldo despues = $%.2f%n",
                    nombre, cuenta.consultarSaldo());
        } finally {
            HILOS_EN_SECCION_CRITICA.decrementAndGet();
            // V() equivale a liberar el permiso con release()
            mutex.V();
        }
    }

    public static int getMaximoSimultaneo() {
        return MAXIMO_SIMULTANEO.get();
    }
}

public class Ejercicio01SemaforoBinario {
    public static void main(String[] args) throws InterruptedException {
        CuentaBancaria cuenta = new CuentaBancaria(1000.0);
        BinarySemaphore mutex = new BinarySemaphore(true);

        Thread[] hilos = {
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.DEPOSITO, 200.0), "Hilo A"),
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.RETIRO, 150.0), "Hilo B"),
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.DEPOSITO, 300.0), "Hilo C"),
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.RETIRO, 100.0), "Hilo D"),
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.DEPOSITO, 50.0), "Hilo E")
        };

        System.out.println("=== Semaforo binario: cuenta bancaria ===");
        System.out.printf("Saldo inicial: $%.2f%n", cuenta.consultarSaldo());

        for (Thread hilo : hilos) {
            hilo.start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        System.out.printf("Saldo final esperado: $1300.00%n");
        System.out.printf("Saldo final obtenido: $%.2f%n", cuenta.consultarSaldo());
        System.out.println("Maximo de hilos modificando el saldo al mismo tiempo: "
                + OperacionBancaria.getMaximoSimultaneo());
    }
}
