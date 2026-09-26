// declara una clase auxiliar con operaciones de espera usadas por los semaforos
public final class Util {
    // evita crear objetos porque la clase solo contiene metodos estaticos
    private Util() {
    }

    // encapsula wait para conservar Util.mywait(this) como en el pseudocodigo
    public static void mywait(Object monitor) {
        // recuerda si el hilo fue interrumpido mientras esperaba
        boolean interrumpido = false;

        // repite la espera solamente si una interrupcion externa la corta
        while (true) {
            // inicia el bloque que puede lanzar InterruptedException
            try {
                // libera temporalmente el monitor y bloquea el hilo
                monitor.wait();
                // termina el ciclo cuando wait regresa normalmente
                break;
            // captura una interrupcion ocurrida durante la espera
            } catch (InterruptedException excepcion) {
                // conserva la informacion para restaurarla despues
                interrumpido = true;
            }
        }

        // comprueba si hubo una interrupcion
        if (interrumpido) {
            // restaura la marca de interrupcion del hilo actual
            Thread.currentThread().interrupt();
        }
    }

    // encapsula una pausa usada solo para hacer visible la concurrencia
    public static void mysleep(long milisegundos) {
        // inicia el bloque que puede recibir una interrupcion
        try {
            // pausa el hilo actual durante el tiempo indicado
            Thread.sleep(milisegundos);
        // captura una interrupcion durante la pausa
        } catch (InterruptedException excepcion) {
            // conserva la marca de interrupcion
            Thread.currentThread().interrupt();
        }
    }
}
