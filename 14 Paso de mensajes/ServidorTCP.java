import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServidorTCP {
    public static void main(String[] args) throws Exception {
        Map<String, Integer> servicios = new HashMap<>();

        try (ServerSocket servidor = new ServerSocket(6000)) {
            System.out.println("Servidor TCP en puerto 6000");
            boolean activo = true;

            while (activo) {
                try (Socket cliente = servidor.accept();
                     BufferedReader entrada = new BufferedReader(new InputStreamReader(
                             cliente.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true,
                             StandardCharsets.UTF_8)) {
                    System.out.println("Cliente conectado: " + cliente.getRemoteSocketAddress());

                    String solicitud;
                    while ((solicitud = entrada.readLine()) != null) {
                        String[] partes = solicitud.trim().split("\\s+");
                        String respuesta;

                        if (partes[0].equalsIgnoreCase("SALIR") && partes.length == 1) {
                            respuesta = "Conexion cerrada";
                            activo = false;
                        } else if (partes[0].equalsIgnoreCase("REGISTRAR") && partes.length == 3) {
                            try {
                                int puerto = Integer.parseInt(partes[2]);
                                if (puerto < 1 || puerto > 65535) {
                                    respuesta = "Puerto invalido";
                                } else {
                                    servicios.put(partes[1], puerto);
                                    respuesta = "Servicio registrado";
                                }
                            } catch (NumberFormatException e) {
                                respuesta = "Puerto invalido";
                            }
                        } else if (partes[0].equalsIgnoreCase("BUSCAR") && partes.length == 2) {
                            Integer puerto = servicios.get(partes[1]);
                            respuesta = puerto == null ? "Servicio no encontrado"
                                    : "Servicio encontrado en el puerto " + puerto;
                        } else {
                            respuesta = "Solicitud invalida";
                        }

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
