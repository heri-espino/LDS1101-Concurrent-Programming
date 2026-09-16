import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Ejercicio02SincronizacionProcesos {
    public static void main(String[] args) throws IOException, InterruptedException {
        String ejecutableJava = Paths.get(
                System.getProperty("java.home"), "bin", "java").toString();
        Path archivoHijo = Paths.get(
                System.getProperty("user.dir"), "Ejercicio02ProcesoHijo.java");

        ProcessBuilder constructorProceso = new ProcessBuilder(
                ejecutableJava, archivoHijo.toAbsolutePath().toString());
        constructorProceso.inheritIO();

        System.out.println("proceso principal: crea ProcessBuilder");
        Process procesoHijo = constructorProceso.start();
        System.out.println("proceso principal: proceso hijo iniciado");
        System.out.println("proceso principal: ejecuta waitFor() y espera");

        int codigoSalida = procesoHijo.waitFor();

        System.out.println("proceso principal: proceso hijo termino con codigo "
                + codigoSalida);
        System.out.println("proceso principal: continua con la ejecucion");
    }
}
