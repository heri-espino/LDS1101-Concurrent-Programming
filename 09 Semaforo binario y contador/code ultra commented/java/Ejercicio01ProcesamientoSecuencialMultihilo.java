// importa la clase que genera numeros pseudoaleatorios
import java.util.Random;

// declara la clase principal de la actividad
public class Ejercicio01ProcesamientoSecuencialMultihilo {
    // define la cantidad exacta de hilos solicitada para la version multihilo
    private static final int HILOS = 4;
    // define la cantidad de ejecuciones usadas para obtener cada promedio
    private static final int REPETICIONES = 10;
    // guarda los cuatro tamanos de arreglo solicitados en la actividad
    private static final int[] TAMANOS = {40, 100_000, 1_000_000, 10_000_000};

    // crea el generador pseudoaleatorio con una semilla fija para repetir la prueba
    private static final Random ALEATORIO = new Random(2026L);

    // declara el metodo que construye un arreglo antes de medir los tiempos
    private static int[] generarArreglo(int tamano) {
        // reserva un arreglo con la cantidad de elementos recibida
        int[] datos = new int[tamano];

        // recorre todas las posiciones del arreglo
        for (int i = 0; i < datos.length; i++) {
            // asigna un entero entre 1 y 100 inclusive
            datos[i] = 1 + ALEATORIO.nextInt(100);
        }

        // devuelve el arreglo ya construido
        return datos;
    }

    // declara la suma que usa un solo flujo de ejecucion
    private static long sumaSecuencial(int[] datos) {
        // crea el acumulador de la suma
        long suma = 0;

        // recorre cada valor del arreglo en el mismo hilo
        for (int valor : datos) {
            // agrega el valor actual al acumulador
            suma += valor;
        }

        // devuelve la suma de todos los elementos
        return suma;
    }

    // declara la suma que divide el arreglo entre cuatro hilos
    private static long sumaMultihilo(int[] datos) throws InterruptedException {
        // crea un arreglo para guardar los cuatro objetos Thread
        Thread[] hilos = new Thread[HILOS];
        // crea una posicion independiente para el subtotal de cada hilo
        long[] parciales = new long[HILOS];

        // prepara los cuatro hilos
        for (int id = 0; id < HILOS; id++) {
            // copia el identificador para que la lambda pueda conservarlo
            int idFinal = id;
            // calcula el primer indice que procesara este hilo
            int inicio = id * datos.length / HILOS;
            // calcula el limite exclusivo de la seccion de este hilo
            int fin = (id + 1) * datos.length / HILOS;

            // crea el hilo y describe su trabajo mediante una lambda
            hilos[id] = new Thread(() -> {
                // crea el subtotal privado de la tarea
                long subtotal = 0;

                // recorre solamente la seccion asignada a este hilo
                for (int i = inicio; i < fin; i++) {
                    // acumula el elemento actual
                    subtotal += datos[i];
                }

                // guarda el subtotal en una posicion que ningun otro hilo escribe
                parciales[idFinal] = subtotal;
            // asigna un nombre legible al hilo
            }, "Suma-" + id);
        }

        // recorre los cuatro hilos ya creados
        for (Thread hilo : hilos) {
            // inicia la ejecucion concurrente del hilo actual
            hilo.start();
        }

        // vuelve a recorrer los cuatro hilos
        for (Thread hilo : hilos) {
            // espera a que el hilo actual termine antes de continuar
            hilo.join();
        }

        // crea el acumulador de los cuatro subtotales
        long total = 0;

        // recorre los resultados parciales una vez que todos los hilos terminaron
        for (long parcial : parciales) {
            // agrega cada subtotal al resultado final
            total += parcial;
        }

        // devuelve una suma comparable con la version secuencial
        return total;
    }

    // declara la operacion costosa ejecutada secuencialmente
    private static double mayorProcesamientoSecuencial(int[] datos) {
        // crea el acumulador de punto flotante
        double acumulado = 0.0;

        // recorre todos los elementos en un solo hilo
        for (int valor : datos) {
            // calcula sqrt(x) por log(x + 1) y lo acumula
            acumulado += Math.sqrt(valor) * Math.log(valor + 1.0);
        }

        // devuelve el valor acumulado
        return acumulado;
    }

    // declara la misma operacion costosa repartida entre cuatro hilos
    private static double mayorProcesamientoMultihilo(int[] datos)
            throws InterruptedException {
        // crea el arreglo que guardara los objetos Thread
        Thread[] hilos = new Thread[HILOS];
        // crea un subtotal double separado para cada hilo
        double[] parciales = new double[HILOS];

        // prepara las cuatro secciones del arreglo
        for (int id = 0; id < HILOS; id++) {
            // copia el identificador porque la lambda requiere un valor fijo
            int idFinal = id;
            // calcula el inicio de la seccion
            int inicio = id * datos.length / HILOS;
            // calcula el final exclusivo de la seccion
            int fin = (id + 1) * datos.length / HILOS;

            // crea un hilo que procesa solamente su seccion
            hilos[id] = new Thread(() -> {
                // crea el subtotal local del hilo
                double subtotal = 0.0;

                // recorre los indices asignados
                for (int i = inicio; i < fin; i++) {
                    // obtiene el valor del arreglo
                    int valor = datos[i];
                    // ejecuta la operacion matematica y la acumula
                    subtotal += Math.sqrt(valor) * Math.log(valor + 1.0);
                }

                // guarda el subtotal en la posicion exclusiva de este hilo
                parciales[idFinal] = subtotal;
            // asigna un nombre al hilo
            }, "Procesamiento-" + id);
        }

        // inicia los cuatro hilos
        for (Thread hilo : hilos) {
            // start provoca que cada hilo ejecute su lambda
            hilo.start();
        }

        // espera la terminacion de los cuatro hilos
        for (Thread hilo : hilos) {
            // join impide combinar resultados antes de tiempo
            hilo.join();
        }

        // crea el acumulador final
        double total = 0.0;

        // combina los cuatro subtotales
        for (double parcial : parciales) {
            // agrega el resultado de cada hilo
            total += parcial;
        }

        // devuelve el resultado multihilo
        return total;
    }

    // declara una comparacion con tolerancia para resultados double
    private static boolean casiIguales(double a, double b) {
        // calcula una escala basada en la magnitud de los dos resultados
        double escala = Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
        // acepta solamente una diferencia relativa muy pequena
        return Math.abs(a - b) <= 1e-10 * escala;
    }

    // declara el metodo que imprime una fila de la tabla solicitada
    private static void imprimirFila(int tamano, String operacion,
            double tiempoSecuencialMs, double tiempoMultihiloMs) {
        // determina cual promedio fue menor
        String masRapido = tiempoSecuencialMs <= tiempoMultihiloMs
                // selecciona secuencial cuando su tiempo es menor o igual
                ? "Secuencial"
                // selecciona multihilo cuando su tiempo es menor
                : "Multihilo";

        // imprime tamano operacion tiempos y version mas rapida
        System.out.printf("%,12d | %-20s | %15.3f | %15.3f | %-10s%n",
                tamano, operacion, tiempoSecuencialMs, tiempoMultihiloMs, masRapido);
    }

    // declara el punto de entrada del programa
    public static void main(String[] args) throws InterruptedException {
        // muestra el titulo de la prueba
        System.out.println("Comparacion de procesamiento secuencial y multihilo");
        // aclara que la generacion queda fuera del tiempo medido
        System.out.println("Los tiempos no incluyen la generacion del arreglo.");
        // imprime los encabezados de la tabla
        System.out.printf("%12s | %-20s | %15s | %15s | %-10s%n",
                "Tamano", "Operacion", "Secuencial (ms)", "Multihilo (ms)", "Mas rapido");
        // imprime una separacion visual
        System.out.println(
                "-------------+----------------------+-----------------+-----------------+------------");

        // repite el experimento para cada tamano solicitado
        for (int tamano : TAMANOS) {
            // genera el arreglo antes de comenzar cualquier cronometro
            int[] datos = generarArreglo(tamano);

            // inicia el acumulador de nanosegundos de la suma secuencial
            long tiempoSumaSecuencial = 0L;
            // conserva el resultado de la ultima repeticion
            long resultadoSumaSecuencial = 0L;

            // ejecuta la suma secuencial diez veces
            for (int repeticion = 0; repeticion < REPETICIONES; repeticion++) {
                // toma el tiempo justo antes de ejecutar la suma
                long inicio = System.nanoTime();
                // ejecuta la implementacion secuencial
                resultadoSumaSecuencial = sumaSecuencial(datos);
                // agrega la duracion de esta repeticion
                tiempoSumaSecuencial += System.nanoTime() - inicio;
            }

            // inicia el acumulador de nanosegundos de la suma multihilo
            long tiempoSumaMultihilo = 0L;
            // conserva el resultado multihilo
            long resultadoSumaMultihilo = 0L;

            // ejecuta la suma multihilo diez veces
            for (int repeticion = 0; repeticion < REPETICIONES; repeticion++) {
                // toma el tiempo antes de crear iniciar y esperar los hilos
                long inicio = System.nanoTime();
                // ejecuta la implementacion de cuatro hilos
                resultadoSumaMultihilo = sumaMultihilo(datos);
                // acumula la duracion completa
                tiempoSumaMultihilo += System.nanoTime() - inicio;
            }

            // comprueba que ambas sumas sean exactamente iguales
            if (resultadoSumaSecuencial != resultadoSumaMultihilo) {
                // detiene la prueba si existe una diferencia
                throw new IllegalStateException(
                        "La suma secuencial y multihilo no coinciden");
            }

            // convierte el promedio secuencial de nanosegundos a milisegundos
            double promedioSumaSecuencial = tiempoSumaSecuencial
                    / (double) REPETICIONES / 1_000_000.0;
            // convierte el promedio multihilo de nanosegundos a milisegundos
            double promedioSumaMultihilo = tiempoSumaMultihilo
                    / (double) REPETICIONES / 1_000_000.0;

            // imprime la fila correspondiente a la suma
            imprimirFila(tamano, "Suma",
                    promedioSumaSecuencial, promedioSumaMultihilo);

            // inicia el acumulador de tiempo de la operacion costosa secuencial
            long tiempoMayorSecuencial = 0L;
            // conserva su resultado numerico
            double resultadoMayorSecuencial = 0.0;

            // ejecuta la version secuencial diez veces
            for (int repeticion = 0; repeticion < REPETICIONES; repeticion++) {
                // inicia el cronometro
                long inicio = System.nanoTime();
                // procesa todo el arreglo en un flujo
                resultadoMayorSecuencial = mayorProcesamientoSecuencial(datos);
                // agrega la duracion
                tiempoMayorSecuencial += System.nanoTime() - inicio;
            }

            // inicia el acumulador de tiempo de la version multihilo
            long tiempoMayorMultihilo = 0L;
            // conserva el resultado multihilo
            double resultadoMayorMultihilo = 0.0;

            // ejecuta la version multihilo diez veces
            for (int repeticion = 0; repeticion < REPETICIONES; repeticion++) {
                // inicia el cronometro
                long inicio = System.nanoTime();
                // procesa el arreglo con cuatro hilos
                resultadoMayorMultihilo = mayorProcesamientoMultihilo(datos);
                // agrega la duracion completa
                tiempoMayorMultihilo += System.nanoTime() - inicio;
            }

            // comprueba que los resultados double sean numericamente equivalentes
            if (!casiIguales(resultadoMayorSecuencial, resultadoMayorMultihilo)) {
                // detiene la prueba si la diferencia supera la tolerancia
                throw new IllegalStateException(
                        "El procesamiento secuencial y multihilo no coinciden");
            }

            // calcula el promedio secuencial en milisegundos
            double promedioMayorSecuencial = tiempoMayorSecuencial
                    / (double) REPETICIONES / 1_000_000.0;
            // calcula el promedio multihilo en milisegundos
            double promedioMayorMultihilo = tiempoMayorMultihilo
                    / (double) REPETICIONES / 1_000_000.0;

            // imprime la fila de mayor procesamiento
            imprimirFila(tamano, "Mayor procesamiento",
                    promedioMayorSecuencial, promedioMayorMultihilo);
        }

        // confirma que todas las comparaciones de resultados fueron correctas
        System.out.println(
                "Verificacion completada: ambas versiones produjeron resultados equivalentes.");
    }
}
