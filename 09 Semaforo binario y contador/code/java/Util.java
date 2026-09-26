public final class Util {
    private Util() {
    }

    public static void mywait(Object monitor) {
        boolean interrumpido = false;

        while (true) {
            try {
                monitor.wait();
                break;
            } catch (InterruptedException excepcion) {
                interrumpido = true;
            }
        }

        if (interrumpido) {
            Thread.currentThread().interrupt();
        }
    }

    public static void mysleep(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
        }
    }
}
