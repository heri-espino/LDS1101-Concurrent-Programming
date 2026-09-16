// importa la clase para atender errores de entrada y salida
import java.io.IOException;
// importa el tipo que representa una ruta del sistema
import java.nio.file.Path;
// importa la clase para construir rutas del sistema
import java.nio.file.Paths;

// declara la clase principal del ejercicio
public class Ejercicio02SincronizacionProcesos {
    // declara el punto de entrada del programa
    public static void main(String[] args) throws IOException, InterruptedException {
        // obtiene la ruta del ejecutable de Java de la JVM actual
        String ejecutableJava = Paths.get(
                // obtiene la carpeta de instalacion de la JVM
                System.getProperty("java.home"),
                // agrega la carpeta de ejecutables
                "bin",
                // selecciona el ejecutable de Java
                "java").toString();
        // construye la ruta del archivo del proceso hijo
        Path archivoHijo = Paths.get(
                // obtiene la carpeta desde la que se ejecuta el programa
                System.getProperty("user.dir"),
                // selecciona el archivo fuente del proceso hijo
                "Ejercicio02ProcesoHijo.java");

        // crea el constructor del proceso externo
        ProcessBuilder constructorProceso = new ProcessBuilder(
                // indica el ejecutable que iniciara el proceso hijo
                ejecutableJava,
                // indica el archivo Java que ejecutara el proceso hijo
                archivoHijo.toAbsolutePath().toString());
        // conecta la entrada y la salida del proceso hijo con el terminal
        constructorProceso.inheritIO();

        // informa que se creo el constructor del proceso
        System.out.println("proceso principal: crea ProcessBuilder");
        // inicia el proceso hijo
        Process procesoHijo = constructorProceso.start();
        // informa que el proceso hijo ya fue iniciado
        System.out.println("proceso principal: proceso hijo iniciado");
        // informa que el proceso principal esperara su finalizacion
        System.out.println("proceso principal: ejecuta waitFor() y espera");

        // espera a que el proceso hijo termine y conserva su codigo de salida
        int codigoSalida = procesoHijo.waitFor();

        // informa que el proceso hijo termino
        System.out.println("proceso principal: proceso hijo termino con codigo "
                // agrega el codigo de salida del proceso hijo
                + codigoSalida);
        // informa que el proceso principal continua
        System.out.println("proceso principal: continua con la ejecucion");
    // cierra el metodo main
    }
// cierra la clase principal
}
