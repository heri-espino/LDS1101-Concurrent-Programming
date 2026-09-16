public class Ejercicio01SincronizacionHilos {
    private static String datosObtenidos;
    private static String datosProcesados;

    // simula el tiempo de una tarea
    private static void pausar(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
        }
    }

    // representa la tarea de obtener datos
    private static class TareaObtenerDatos implements Runnable {
        @Override
        public void run() {
            System.out.println("Hilo 1 inicia: obtener datos");
            pausar(500);
            datosObtenidos = "datos obtenidos";
            System.out.println("Hilo 1 termina: obtener datos");
        }
    }

    // representa la tarea de procesar datos
    private static class TareaProcesarDatos implements Runnable {
        @Override
        public void run() {
            System.out.println("Hilo 2 inicia: procesar datos");
            pausar(500);
            datosProcesados = datosObtenidos + " y procesados";
            System.out.println("Hilo 2 termina: procesar datos");
        }
    }

    // representa la tarea de generar el reporte
    private static class TareaGenerarReporte implements Runnable {
        @Override
        public void run() {
            System.out.println("Hilo 3 inicia: generar reporte");
            pausar(500);
            System.out.println("reporte: " + datosProcesados);
            System.out.println("Hilo 3 termina: generar reporte");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread hilo1 = new Thread(new TareaObtenerDatos(), "Hilo 1");
        Thread hilo2 = new Thread(new TareaProcesarDatos(), "Hilo 2");
        Thread hilo3 = new Thread(new TareaGenerarReporte(), "Hilo 3");

        System.out.println("proceso principal: inicia Hilo 1");
        hilo1.start();
        hilo1.join();

        System.out.println("proceso principal: Hilo 1 termino, inicia Hilo 2");
        hilo2.start();
        hilo2.join();

        System.out.println("proceso principal: Hilo 2 termino, inicia Hilo 3");
        hilo3.start();
        hilo3.join();

        System.out.println("proceso principal: las tres tareas terminaron");
    }
}
