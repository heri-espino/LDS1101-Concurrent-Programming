import java.util.Random;

public class Ejercicio01ProcesamientoSecuencialMultihilo {
    // configura la cantidad de hilos y repeticiones de la actividad
    private static final int HILOS = 4;
    private static final int REPETICIONES = 10;
    private static final int[] TAMANOS = {40, 100_000, 1_000_000, 10_000_000};

    // usa una semilla fija para que los arreglos sean reproducibles
    private static final Random ALEATORIO = new Random(2026L);

    // genera valores enteros entre 1 y 100 fuera de la medicion
    private static int[] generarArreglo(int tamano) {
        int[] datos = new int[tamano];

        for (int i = 0; i < datos.length; i++) {
            datos[i] = 1 + ALEATORIO.nextInt(100);
        }

        return datos;
    }

    // acumula todo el arreglo en un solo flujo de ejecucion
    private static long sumaSecuencial(int[] datos) {
        long suma = 0;

        for (int valor : datos) {
            suma += valor;
        }

        return suma;
    }

    // divide el arreglo en cuatro secciones y suma cada una en un hilo
    private static long sumaMultihilo(int[] datos) throws InterruptedException {
        Thread[] hilos = new Thread[HILOS];
        long[] parciales = new long[HILOS];

        for (int id = 0; id < HILOS; id++) {
            int idFinal = id;
            int inicio = id * datos.length / HILOS;
            int fin = (id + 1) * datos.length / HILOS;

            hilos[id] = new Thread(() -> {
                long subtotal = 0;

                for (int i = inicio; i < fin; i++) {
                    subtotal += datos[i];
                }

                parciales[idFinal] = subtotal;
            }, "Suma-" + id);
        }

        // start inicia los cuatro hilos
        for (Thread hilo : hilos) {
            hilo.start();
        }

        // join espera a que todos terminen antes de combinar resultados
        for (Thread hilo : hilos) {
            hilo.join();
        }

        long total = 0;

        for (long parcial : parciales) {
            total += parcial;
        }

        return total;
    }

    // calcula sqrt(x) por log(x + 1) sobre todo el arreglo
    private static double mayorProcesamientoSecuencial(int[] datos) {
        double acumulado = 0.0;

        for (int valor : datos) {
            acumulado += Math.sqrt(valor) * Math.log(valor + 1.0);
        }

        return acumulado;
    }

    // reparte el procesamiento costoso entre cuatro hilos
    private static double mayorProcesamientoMultihilo(int[] datos)
            throws InterruptedException {
        Thread[] hilos = new Thread[HILOS];
        double[] parciales = new double[HILOS];

        for (int id = 0; id < HILOS; id++) {
            int idFinal = id;
            int inicio = id * datos.length / HILOS;
            int fin = (id + 1) * datos.length / HILOS;

            hilos[id] = new Thread(() -> {
                double subtotal = 0.0;

                for (int i = inicio; i < fin; i++) {
                    int valor = datos[i];
                    subtotal += Math.sqrt(valor) * Math.log(valor + 1.0);
                }

                parciales[idFinal] = subtotal;
            }, "Procesamiento-" + id);
        }

        for (Thread hilo : hilos) {
            hilo.start();
        }

        for (Thread hilo : hilos) {
            hilo.join();
        }

        double total = 0.0;

        for (double parcial : parciales) {
            total += parcial;
        }

        return total;
    }

    // compara resultados double permitiendo una diferencia numerica muy pequena
    private static boolean casiIguales(double a, double b) {
        double escala = Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
        return Math.abs(a - b) <= 1e-10 * escala;
    }

    // imprime una fila con los promedios de las diez ejecuciones
    private static void imprimirFila(int tamano, String operacion,
            double tiempoSecuencialMs, double tiempoMultihiloMs) {
        String masRapido = tiempoSecuencialMs <= tiempoMultihiloMs
                ? "Secuencial"
                : "Multihilo";

        System.out.printf("%,12d | %-20s | %15.3f | %15.3f | %-10s%n",
                tamano, operacion, tiempoSecuencialMs, tiempoMultihiloMs, masRapido);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Comparacion de procesamiento secuencial y multihilo");
        System.out.println("Los tiempos no incluyen la generacion del arreglo.");
        System.out.printf("%12s | %-20s | %15s | %15s | %-10s%n",
                "Tamano", "Operacion", "Secuencial (ms)", "Multihilo (ms)", "Mas rapido");
        System.out.println(
                "-------------+----------------------+-----------------+-----------------+------------");

        for (int tamano : TAMANOS) {
            // la generacion queda fuera de cualquier medicion de tiempo
            int[] datos = generarArreglo(tamano);

            long tiempoSumaSecuencial = 0L;
            long resultadoSumaSecuencial = 0L;

            for (int repeticion = 0; repeticion < REPETICIONES; repeticion++) {
                long inicio = System.nanoTime();
                resultadoSumaSecuencial = sumaSecuencial(datos);
                tiempoSumaSecuencial += System.nanoTime() - inicio;
            }

            long tiempoSumaMultihilo = 0L;
            long resultadoSumaMultihilo = 0L;

            for (int repeticion = 0; repeticion < REPETICIONES; repeticion++) {
                long inicio = System.nanoTime();
                resultadoSumaMultihilo = sumaMultihilo(datos);
                tiempoSumaMultihilo += System.nanoTime() - inicio;
            }

            if (resultadoSumaSecuencial != resultadoSumaMultihilo) {
                throw new IllegalStateException(
                        "La suma secuencial y multihilo no coinciden");
            }

            double promedioSumaSecuencial = tiempoSumaSecuencial
                    / (double) REPETICIONES / 1_000_000.0;
            double promedioSumaMultihilo = tiempoSumaMultihilo
                    / (double) REPETICIONES / 1_000_000.0;

            imprimirFila(tamano, "Suma",
                    promedioSumaSecuencial, promedioSumaMultihilo);

            long tiempoMayorSecuencial = 0L;
            double resultadoMayorSecuencial = 0.0;

            for (int repeticion = 0; repeticion < REPETICIONES; repeticion++) {
                long inicio = System.nanoTime();
                resultadoMayorSecuencial = mayorProcesamientoSecuencial(datos);
                tiempoMayorSecuencial += System.nanoTime() - inicio;
            }

            long tiempoMayorMultihilo = 0L;
            double resultadoMayorMultihilo = 0.0;

            for (int repeticion = 0; repeticion < REPETICIONES; repeticion++) {
                long inicio = System.nanoTime();
                resultadoMayorMultihilo = mayorProcesamientoMultihilo(datos);
                tiempoMayorMultihilo += System.nanoTime() - inicio;
            }

            if (!casiIguales(resultadoMayorSecuencial, resultadoMayorMultihilo)) {
                throw new IllegalStateException(
                        "El procesamiento secuencial y multihilo no coinciden");
            }

            double promedioMayorSecuencial = tiempoMayorSecuencial
                    / (double) REPETICIONES / 1_000_000.0;
            double promedioMayorMultihilo = tiempoMayorMultihilo
                    / (double) REPETICIONES / 1_000_000.0;

            imprimirFila(tamano, "Mayor procesamiento",
                    promedioMayorSecuencial, promedioMayorMultihilo);
        }

        System.out.println(
                "Verificacion completada: ambas versiones produjeron resultados equivalentes.");
    }
}
