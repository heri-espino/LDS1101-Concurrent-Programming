// importa un contador atomico usado solamente para comprobar la exclusion mutua
import java.util.concurrent.atomic.AtomicInteger;

// declara el semaforo binario siguiendo la estructura del pseudocodigo de clase
class BinarySemaphore {
    // guarda el unico permiso del semaforo
    // true significa disponible y false significa ocupado
    boolean value;

    // recibe el valor inicial del semaforo
    public BinarySemaphore(boolean initValue) {
        // copia el valor inicial al estado compartido
        value = initValue;
    }

    // solicita el unico permiso del semaforo
    // synchronized hace indivisible la revision y modificacion de value
    public synchronized void P() {
        // repite mientras el permiso este ocupado
        while (value == false) {
            // bloquea el hilo y libera temporalmente el monitor
            Util.mywait(this);
        }

        // ocupa el permiso antes de abandonar P
        value = false;
    }

    // libera el unico permiso del semaforo
    // synchronized protege el cambio y la notificacion
    public synchronized void V() {
        // marca el permiso como disponible
        value = true;
        // despierta a uno de los hilos que esperan
        notify();
    }
}

// declara el recurso compartido protegido por el semaforo binario
class CuentaBancaria {
    // guarda el saldo actual de la cuenta
    private double saldo;

    // crea la cuenta con el saldo recibido
    public CuentaBancaria(double saldoInicial) {
        // copia el saldo inicial
        saldo = saldoInicial;
    }

    // devuelve el saldo actual
    // no usa synchronized porque el semaforo protege el acceso
    public double consultarSaldo() {
        // entrega el valor compartido
        return saldo;
    }

    // aumenta el saldo
    public void depositar(double cantidad) {
        // suma la cantidad depositada
        saldo += cantidad;
    }

    // disminuye el saldo
    public void retirar(double cantidad) {
        // comprueba que existan fondos suficientes
        if (cantidad > saldo) {
            // detiene la operacion si el retiro no puede realizarse
            throw new IllegalStateException("Saldo insuficiente");
        }

        // resta la cantidad retirada
        saldo -= cantidad;
    }
}

// declara la tarea concurrente que ejecutara cada hilo de la cuenta
class OperacionBancaria implements Runnable {
    // define los tipos de operacion usados en la prueba
    public enum Tipo {
        // representa un deposito
        DEPOSITO,
        // representa un retiro
        RETIRO
    }

    // cuenta cuantos hilos estan dentro de la seccion critica
    // solo verifica el comportamiento y no concede permisos
    private static final AtomicInteger HILOS_EN_SECCION_CRITICA =
            new AtomicInteger(0);
    // conserva el maximo de hilos simultaneos observado
    private static final AtomicInteger MAXIMO_SIMULTANEO =
            new AtomicInteger(0);

    // guarda el semaforo compartido
    private final BinarySemaphore mutex;
    // guarda la cuenta compartida
    private final CuentaBancaria cuenta;
    // guarda el tipo de operacion
    private final Tipo tipo;
    // guarda la cantidad de la operacion
    private final double cantidad;

    // recibe los datos necesarios para la tarea
    public OperacionBancaria(BinarySemaphore mutex, CuentaBancaria cuenta,
            Tipo tipo, double cantidad) {
        // conserva el semaforo
        this.mutex = mutex;
        // conserva la cuenta
        this.cuenta = cuenta;
        // conserva el tipo
        this.tipo = tipo;
        // conserva la cantidad
        this.cantidad = cantidad;
    }

    // implementa el trabajo que ejecutara cada Thread
    @Override
    public void run() {
        // solicita el permiso antes de entrar a la seccion critica
        // P corresponde conceptualmente a acquire
        mutex.P();

        // garantiza que V se ejecute aun si ocurre un error
        try {
            // registra que este hilo entro a la seccion critica
            int simultaneos = HILOS_EN_SECCION_CRITICA.incrementAndGet();
            // actualiza el maximo observado
            MAXIMO_SIMULTANEO.accumulateAndGet(simultaneos, Math::max);

            // comprueba la propiedad de exclusion mutua
            if (simultaneos > 1) {
                // reporta una falla si dos hilos entraron juntos
                throw new IllegalStateException(
                        "Mas de un hilo entro a la seccion critica");
            }

            // consulta el saldo una vez obtenido el permiso
            double saldoAntes = cuenta.consultarSaldo();
            // obtiene el nombre del hilo actual
            String nombre = Thread.currentThread().getName();

            // muestra la entrada a la seccion critica
            System.out.printf("%s entra | saldo antes = $%.2f%n",
                    nombre, saldoAntes);

            // mantiene ocupado el permiso para hacer visible la espera
            Util.mysleep(120);

            // decide si la tarea deposita o retira
            if (tipo == Tipo.DEPOSITO) {
                // realiza el deposito
                cuenta.depositar(cantidad);
                // informa la operacion
                System.out.printf("%s deposita $%.2f%n", nombre, cantidad);
            } else {
                // realiza el retiro
                cuenta.retirar(cantidad);
                // informa la operacion
                System.out.printf("%s retira $%.2f%n", nombre, cantidad);
            }

            // muestra el saldo despues de la modificacion
            System.out.printf("%s sale  | saldo despues = $%.2f%n",
                    nombre, cuenta.consultarSaldo());
        // ejecuta siempre la salida de la seccion critica
        } finally {
            // registra que el hilo ya salio
            HILOS_EN_SECCION_CRITICA.decrementAndGet();
            // libera el permiso para otro hilo
            // V corresponde conceptualmente a release
            mutex.V();
        }
    }

    // devuelve el maximo observado
    public static int getMaximoSimultaneo() {
        // entrega el valor de comprobacion
        return MAXIMO_SIMULTANEO.get();
    }
}

// declara el programa principal de la parte del semaforo binario
public class Ejercicio01SemaforoBinario {
    // declara el punto de entrada
    public static void main(String[] args) throws InterruptedException {
        // crea la cuenta compartida con saldo inicial de 1000
        CuentaBancaria cuenta = new CuentaBancaria(1000.0);
        // crea el semaforo binario con el permiso disponible
        BinarySemaphore mutex = new BinarySemaphore(true);

        // crea los cinco hilos solicitados
        Thread[] hilos = {
                // hilo A deposita 200
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.DEPOSITO, 200.0), "Hilo A"),
                // hilo B retira 150
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.RETIRO, 150.0), "Hilo B"),
                // hilo C deposita 300
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.DEPOSITO, 300.0), "Hilo C"),
                // hilo D retira 100
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.RETIRO, 100.0), "Hilo D"),
                // hilo E deposita 50
                new Thread(new OperacionBancaria(
                        mutex, cuenta, OperacionBancaria.Tipo.DEPOSITO, 50.0), "Hilo E")
        };

        // muestra el nombre de la prueba
        System.out.println("=== Semaforo binario: cuenta bancaria ===");
        // muestra el saldo inicial
        System.out.printf("Saldo inicial: $%.2f%n", cuenta.consultarSaldo());

        // inicia los cinco hilos
        for (Thread hilo : hilos) {
            // permite que los hilos compitan por el unico permiso
            hilo.start();
        }

        // espera la terminacion de todos
        for (Thread hilo : hilos) {
            // evita mostrar el resultado final antes de tiempo
            hilo.join();
        }

        // muestra el resultado esperado
        System.out.printf("Saldo final esperado: $1300.00%n");
        // muestra el resultado realmente obtenido
        System.out.printf("Saldo final obtenido: $%.2f%n",
                cuenta.consultarSaldo());
        // comprueba que nunca hubiera mas de un hilo modificando la cuenta
        System.out.println("Maximo de hilos modificando el saldo al mismo tiempo: "
                + OperacionBancaria.getMaximoSimultaneo());
    }
}
