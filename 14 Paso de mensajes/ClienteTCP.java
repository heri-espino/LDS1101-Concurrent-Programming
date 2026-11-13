import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClienteTCP {
    public static void main(String[] args) throws Exception {
        try (Socket cliente = new Socket("localhost", 6000);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(
                     System.in, StandardCharsets.UTF_8));
             BufferedReader entrada = new BufferedReader(new InputStreamReader(
                     cliente.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true,
                     StandardCharsets.UTF_8)) {

            System.out.println("Escribe REGISTRAR nombre puerto, BUSCAR nombre o SALIR");
            String solicitud;
            while ((solicitud = teclado.readLine()) != null) {
                salida.println(solicitud);
                String respuesta = entrada.readLine();
                if (respuesta == null) break;
                System.out.println(respuesta);
                if (solicitud.equalsIgnoreCase("SALIR")) break;
            }
        }
    }
}
