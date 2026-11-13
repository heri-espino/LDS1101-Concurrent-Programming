// lee lineas de texto del teclado o de la conexion
import java.io.BufferedReader;
// convierte un flujo de bytes en texto
import java.io.InputStreamReader;
// envia lineas de texto a traves de tcp
import java.io.PrintWriter;
// representa una conexion tcp entre cliente y servidor
import java.net.Socket;
// se usa para codificar los mensajes como utf 8
import java.nio.charset.StandardCharsets;

// este archivo define uno de los programas independientes de la actividad
public class ClienteTCP_ultracommented {
    // la ejecucion comienza en este metodo main
    public static void main(String[] args) throws Exception {
        // abrimos la conexion tcp al servidor en localhost
        try (Socket cliente = new Socket("localhost", 6000);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(
                     System.in, StandardCharsets.UTF_8));
             BufferedReader entrada = new BufferedReader(new InputStreamReader(
                     // obtenemos los datos que llegan desde el otro proceso
                     cliente.getInputStream(), StandardCharsets.UTF_8));
             // obtenemos el canal para enviar datos al otro proceso
             PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true,
                     StandardCharsets.UTF_8)) {

            System.out.println("Escribe REGISTRAR nombre puerto, BUSCAR nombre o SALIR");
            // guardamos cada orden enviada por el usuario
            String solicitud;
            while ((solicitud = teclado.readLine()) != null) {
                // enviamos la orden escrita al servidor tcp
                salida.println(solicitud);
                // esperamos y leemos una linea completa por tcp
                String respuesta = entrada.readLine();
                // si se cerro la conexion dejamos de leer
                if (respuesta == null) break;
                System.out.println(respuesta);
                // salir termina la conversacion despues de recibir la respuesta
                if (solicitud.equalsIgnoreCase("SALIR")) break;
            }
        }
    }
}
