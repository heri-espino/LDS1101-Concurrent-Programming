// representa un mensaje udp con sus bytes y su direccion
import java.net.DatagramPacket;
// permite enviar o recibir mensajes udp
import java.net.DatagramSocket;
// representa la direccion ip del servidor
import java.net.InetAddress;
// se usa para codificar los mensajes como utf 8
import java.nio.charset.StandardCharsets;
// sirve para leer texto escrito en el teclado
import java.util.Scanner;

// este archivo define uno de los programas independientes de la actividad
public class ClienteUDP_ultracommented {
    // la ejecucion comienza en este metodo main
    public static void main(String[] args) throws Exception {
        // localhost apunta a la misma computadora
        InetAddress destino = InetAddress.getByName("localhost");

        // creamos el socket del cliente con un puerto local automatico
        try (DatagramSocket cliente = new DatagramSocket();
             // leemos los mensajes que el usuario escribe
             Scanner teclado = new Scanner(System.in)) {
            // si no llega respuesta en tres segundos evitamos esperar indefinidamente
            cliente.setSoTimeout(3000);

            // repetimos para atender todos los mensajes hasta salir
            while (true) {
                System.out.print("Mensaje (SALIR para terminar): ");
                // si el teclado se cerro terminamos el programa
                if (!teclado.hasNextLine()) break;
                // obtenemos el texto completo escrito por el usuario
                String mensaje = teclado.nextLine();

                // convertimos el texto a bytes para transmitirlo
                byte[] datos = mensaje.getBytes(StandardCharsets.UTF_8);
                DatagramPacket enviado = new DatagramPacket(datos, datos.length,
                        destino, 5000);
                // enviamos el mensaje al puerto 5000 del servidor
                cliente.send(enviado);

                // reservamos espacio para los bytes del mensaje entrante
                byte[] espacio = new byte[1024];
                // preparamos el paquete en el que llegara la informacion
                DatagramPacket recibido = new DatagramPacket(espacio, espacio.length);
                // el cliente espera la confirmacion del servidor
                cliente.receive(recibido);
                // convertimos los bytes recibidos en texto sin leer espacio extra
                System.out.println(new String(recibido.getData(), 0,
                        recibido.getLength(), StandardCharsets.UTF_8));

                // salir termina la conversacion despues de recibir la respuesta
                if (mensaje.equalsIgnoreCase("SALIR")) break;
            }
        }
    }
}
