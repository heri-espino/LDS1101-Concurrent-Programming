// lee lineas de texto del teclado o de la conexion
import java.io.BufferedReader;
// convierte un flujo de bytes en texto
import java.io.InputStreamReader;
// envia lineas de texto a traves de tcp
import java.io.PrintWriter;
// escucha las conexiones tcp en un puerto
import java.net.ServerSocket;
// representa una conexion tcp entre cliente y servidor
import java.net.Socket;
// se usa para codificar los mensajes como utf 8
import java.nio.charset.StandardCharsets;
// guarda los servicios con su nombre y puerto
import java.util.HashMap;
// declara la estructura de nombre a puerto
import java.util.Map;

// este archivo define uno de los programas independientes de la actividad
public class ServidorTCP_ultracommented {
    // la ejecucion comienza en este metodo main
    public static void main(String[] args) throws Exception {
        // el mapa relaciona un nombre de servicio con su puerto
        Map<String, Integer> servicios = new HashMap<>();

        // el servidor tcp escucha conexiones en el puerto 6000
        try (ServerSocket servidor = new ServerSocket(6000)) {
            System.out.println("Servidor TCP en puerto 6000");
            // esta variable determina cuando el servidor debe terminar
            boolean activo = true;

            // seguimos atendiendo conexiones mientras no pidan salir
            while (activo) {
                // esperamos hasta que un cliente tcp se conecte
                try (Socket cliente = servidor.accept();
                     BufferedReader entrada = new BufferedReader(new InputStreamReader(
                             // obtenemos los datos que llegan desde el otro proceso
                             cliente.getInputStream(), StandardCharsets.UTF_8));
                     // obtenemos el canal para enviar datos al otro proceso
                     PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true,
                             StandardCharsets.UTF_8)) {
                    System.out.println("Cliente conectado: " + cliente.getRemoteSocketAddress());

                    // guardamos cada orden enviada por el usuario
                    String solicitud;
                    // esperamos y leemos una linea completa por tcp
                    while ((solicitud = entrada.readLine()) != null) {
                        // separamos la orden en palabras usando espacios
                        String[] partes = solicitud.trim().split("\\s+");
                        String respuesta;

                        // salir termina la conversacion despues de recibir la respuesta
                        if (partes[0].equalsIgnoreCase("SALIR") && partes.length == 1) {
                            respuesta = "Conexion cerrada";
                            activo = false;
                        // registramos un servicio y el puerto indicado
                        } else if (partes[0].equalsIgnoreCase("REGISTRAR") && partes.length == 3) {
                            try {
                                // convertimos el puerto de texto a un numero
                                int puerto = Integer.parseInt(partes[2]);
                                // validamos el rango de puertos tcp valido
                                if (puerto < 1 || puerto > 65535) {
                                    respuesta = "Puerto invalido";
                                } else {
                                    // guardamos o actualizamos el nombre y su puerto
                                    servicios.put(partes[1], puerto);
                                    respuesta = "Servicio registrado";
                                }
                            } catch (NumberFormatException e) {
                                respuesta = "Puerto invalido";
                            }
                        // buscamos el servicio por su nombre en el mapa
                        } else if (partes[0].equalsIgnoreCase("BUSCAR") && partes.length == 2) {
                            // obtenemos el puerto registrado o null si no existe
                            Integer puerto = servicios.get(partes[1]);
                            respuesta = puerto == null ? "Servicio no encontrado"
                                    : "Servicio encontrado en el puerto " + puerto;
                        } else {
                            respuesta = "Solicitud invalida";
                        }

                        // respondemos al cliente con una linea completa
                        salida.println(respuesta);
                        System.out.println(solicitud + " -> " + respuesta);
                        if (!activo) break;
                    }
                }
            }
            System.out.println("Servidor TCP cerrado");
        }
    }
}
