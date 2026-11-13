import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class ServidorUDP {
    public static void main(String[] args) throws Exception {
        try (DatagramSocket servidor = new DatagramSocket(5000)) {
            System.out.println("Servidor UDP en puerto 5000");

            while (true) {
                byte[] espacio = new byte[1024];
                DatagramPacket recibido = new DatagramPacket(espacio, espacio.length);
                servidor.receive(recibido);

                String mensaje = new String(recibido.getData(), 0,
                        recibido.getLength(), StandardCharsets.UTF_8);
                System.out.println("De " + recibido.getAddress().getHostAddress()
                        + ":" + recibido.getPort() + " -> " + mensaje);

                String respuesta = "Mensaje recibido: " + mensaje;
                byte[] datos = respuesta.getBytes(StandardCharsets.UTF_8);
                DatagramPacket enviado = new DatagramPacket(datos, datos.length,
                        recibido.getAddress(), recibido.getPort());
                servidor.send(enviado);

                if (mensaje.equalsIgnoreCase("SALIR")) break;
            }
            System.out.println("Servidor UDP cerrado");
        }
    }
}
