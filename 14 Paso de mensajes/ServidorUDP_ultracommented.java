// representa un mensaje udp con sus bytes y su direccion
import java.net.DatagramPacket;
// permite enviar o recibir mensajes udp
import java.net.DatagramSocket;
// se usa para codificar los mensajes como utf 8
import java.nio.charset.StandardCharsets;

// este archivo define uno de los programas independientes de la actividad
public class ServidorUDP_ultracommented {
    // la ejecucion comienza en este metodo main
    public static void main(String[] args) throws Exception {
        // abrimos el puerto udp y lo cerramos automaticamente al terminar
        try (DatagramSocket servidor = new DatagramSocket(5000)) {
            System.out.println("Servidor UDP en puerto 5000");

            // repetimos para atender todos los mensajes hasta salir
            while (true) {
                // reservamos espacio para los bytes del mensaje entrante
                byte[] espacio = new byte[1024];
                // preparamos el paquete en el que llegara la informacion
                DatagramPacket recibido = new DatagramPacket(espacio, espacio.length);
                // esta llamada espera hasta recibir un datagrama
                servidor.receive(recibido);

                // convertimos los bytes recibidos en texto sin leer espacio extra
                String mensaje = new String(recibido.getData(), 0,
                        recibido.getLength(), StandardCharsets.UTF_8);
                // mostramos la direccion ip que envio el mensaje
                System.out.println("De " + recibido.getAddress().getHostAddress()
                        + ":" + recibido.getPort() + " -> " + mensaje);

                String respuesta = "Mensaje recibido: " + mensaje;
                // convertimos el texto a bytes para transmitirlo
                byte[] datos = respuesta.getBytes(StandardCharsets.UTF_8);
                DatagramPacket enviado = new DatagramPacket(datos, datos.length,
                        recibido.getAddress(), recibido.getPort());
                // enviamos la confirmacion a la ip y puerto del cliente
                servidor.send(enviado);

                // salir termina la conversacion despues de recibir la respuesta
                if (mensaje.equalsIgnoreCase("SALIR")) break;
            }
            System.out.println("Servidor UDP cerrado");
        }
    }
}
