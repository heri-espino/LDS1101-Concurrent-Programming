import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClienteUDP {
    public static void main(String[] args) throws Exception {
        InetAddress destino = InetAddress.getByName("localhost");

        try (DatagramSocket cliente = new DatagramSocket();
             Scanner teclado = new Scanner(System.in)) {
            cliente.setSoTimeout(3000);

            while (true) {
                System.out.print("Mensaje (SALIR para terminar): ");
                if (!teclado.hasNextLine()) break;
                String mensaje = teclado.nextLine();

                byte[] datos = mensaje.getBytes(StandardCharsets.UTF_8);
                DatagramPacket enviado = new DatagramPacket(datos, datos.length,
                        destino, 5000);
                cliente.send(enviado);

                byte[] espacio = new byte[1024];
                DatagramPacket recibido = new DatagramPacket(espacio, espacio.length);
                cliente.receive(recibido);
                System.out.println(new String(recibido.getData(), 0,
                        recibido.getLength(), StandardCharsets.UTF_8));

                if (mensaje.equalsIgnoreCase("SALIR")) break;
            }
        }
    }
}
