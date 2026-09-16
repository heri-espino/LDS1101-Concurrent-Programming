// declara la clase principal del ejercicio
public class Ejercicio01SincronizacionHilos {
    // declara el dato producido por el primer hilo
    private static String datosObtenidos;
    // declara el dato producido por el segundo hilo
    private static String datosProcesados;

    // declara el metodo que simula el tiempo de una tarea
    private static void pausar(long milisegundos) {
        // inicia el bloque que puede recibir una interrupcion
        try {
            // detiene temporalmente el hilo actual
            Thread.sleep(milisegundos);
        // captura la interrupcion del hilo
        } catch (InterruptedException excepcion) {
            // conserva la marca de interrupcion del hilo
            Thread.currentThread().interrupt();
        // cierra el manejo de la interrupcion
        }
    // cierra el metodo de pausa
    }

    // declara la tarea que obtiene los datos
    private static class TareaObtenerDatos implements Runnable {
        // redefine el metodo de ejecucion de Runnable
        @Override
        // ejecuta la tarea del primer hilo
        public void run() {
            // informa el inicio de la obtencion de datos
            System.out.println("Hilo 1 inicia: obtener datos");
            // simula el trabajo de obtener los datos
            pausar(500);
            // guarda los datos obtenidos para el siguiente hilo
            datosObtenidos = "datos obtenidos";
            // informa el final de la obtencion de datos
            System.out.println("Hilo 1 termina: obtener datos");
        // cierra el metodo run
        }
    // cierra la clase de la primera tarea
    }

    // declara la tarea que procesa los datos
    private static class TareaProcesarDatos implements Runnable {
        // redefine el metodo de ejecucion de Runnable
        @Override
        // ejecuta la tarea del segundo hilo
        public void run() {
            // informa el inicio del procesamiento
            System.out.println("Hilo 2 inicia: procesar datos");
            // simula el trabajo de procesar los datos
            pausar(500);
            // guarda los datos procesados para el reporte
            datosProcesados = datosObtenidos + " y procesados";
            // informa el final del procesamiento
            System.out.println("Hilo 2 termina: procesar datos");
        // cierra el metodo run
        }
    // cierra la clase de la segunda tarea
    }

    // declara la tarea que genera el reporte
    private static class TareaGenerarReporte implements Runnable {
        // redefine el metodo de ejecucion de Runnable
        @Override
        // ejecuta la tarea del tercer hilo
        public void run() {
            // informa el inicio de la generacion del reporte
            System.out.println("Hilo 3 inicia: generar reporte");
            // simula el trabajo de generar el reporte
            pausar(500);
            // muestra el contenido del reporte
            System.out.println("reporte: " + datosProcesados);
            // informa el final de la generacion del reporte
            System.out.println("Hilo 3 termina: generar reporte");
        // cierra el metodo run
        }
    // cierra la clase de la tercera tarea
    }

    // declara el punto de entrada del programa
    public static void main(String[] args) throws InterruptedException {
        // crea el hilo que ejecuta la obtencion de datos
        Thread hilo1 = new Thread(new TareaObtenerDatos(), "Hilo 1");
        // crea el hilo que ejecuta el procesamiento de datos
        Thread hilo2 = new Thread(new TareaProcesarDatos(), "Hilo 2");
        // crea el hilo que ejecuta la generacion del reporte
        Thread hilo3 = new Thread(new TareaGenerarReporte(), "Hilo 3");

        // informa el inicio de la primera tarea
        System.out.println("proceso principal: inicia Hilo 1");
        // inicia el primer hilo
        hilo1.start();
        // espera a que termine el primer hilo
        hilo1.join();

        // informa que la primera tarea termino y comienza la segunda
        System.out.println("proceso principal: Hilo 1 termino, inicia Hilo 2");
        // inicia el segundo hilo
        hilo2.start();
        // espera a que termine el segundo hilo
        hilo2.join();

        // informa que la segunda tarea termino y comienza la tercera
        System.out.println("proceso principal: Hilo 2 termino, inicia Hilo 3");
        // inicia el tercer hilo
        hilo3.start();
        // espera a que termine el tercer hilo
        hilo3.join();

        // informa que todas las tareas terminaron
        System.out.println("proceso principal: las tres tareas terminaron");
    // cierra el metodo main
    }
// cierra la clase principal
}
