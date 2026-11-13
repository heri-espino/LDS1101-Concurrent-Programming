"""Rebuild beginner-friendly commented variants without changing the logic."""
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
NAMES = ["ServidorUDP", "ClienteUDP", "ServidorTCP", "ClienteTCP"]

EXPLANATIONS = {
    "import java.net.DatagramPacket;": "representa un mensaje udp con sus bytes y su direccion",
    "import java.net.DatagramSocket;": "permite enviar o recibir mensajes udp",
    "import java.net.InetAddress;": "representa la direccion ip del servidor",
    "import java.util.Scanner;": "sirve para leer texto escrito en el teclado",
    "import java.nio.charset.StandardCharsets;": "se usa para codificar los mensajes como utf 8",
    "import java.io.BufferedReader;": "lee lineas de texto del teclado o de la conexion",
    "import java.io.InputStreamReader;": "convierte un flujo de bytes en texto",
    "import java.io.PrintWriter;": "envia lineas de texto a traves de tcp",
    "import java.net.ServerSocket;": "escucha las conexiones tcp en un puerto",
    "import java.net.Socket;": "representa una conexion tcp entre cliente y servidor",
    "import java.util.HashMap;": "guarda los servicios con su nombre y puerto",
    "import java.util.Map;": "declara la estructura de nombre a puerto",
    "public class ": "este archivo define uno de los programas independientes de la actividad",
    "public static void main": "la ejecucion comienza en este metodo main",
    "try (DatagramSocket servidor": "abrimos el puerto udp y lo cerramos automaticamente al terminar",
    "new DatagramSocket()": "creamos el socket del cliente con un puerto local automatico",
    "new DatagramSocket(5000)": "el servidor udp escucha en el puerto 5000",
    "while (true)": "repetimos para atender todos los mensajes hasta salir",
    "new byte[1024]": "reservamos espacio para los bytes del mensaje entrante",
    "new DatagramPacket(espacio": "preparamos el paquete en el que llegara la informacion",
    "servidor.receive(recibido)": "esta llamada espera hasta recibir un datagrama",
    "cliente.receive(recibido)": "el cliente espera la confirmacion del servidor",
    "new String(recibido.getData()": "convertimos los bytes recibidos en texto sin leer espacio extra",
    "getHostAddress()": "mostramos la direccion ip que envio el mensaje",
    "getBytes(StandardCharsets.UTF_8)": "convertimos el texto a bytes para transmitirlo",
    "servidor.send(enviado)": "enviamos la confirmacion a la ip y puerto del cliente",
    "cliente.send(enviado)": "enviamos el mensaje al puerto 5000 del servidor",
    "equalsIgnoreCase(\"SALIR\")": "salir termina la conversacion despues de recibir la respuesta",
    "InetAddress.getByName(\"localhost\")": "localhost apunta a la misma computadora",
    "cliente.setSoTimeout(3000)": "si no llega respuesta en tres segundos evitamos esperar indefinidamente",
    "Scanner teclado": "leemos los mensajes que el usuario escribe",
    "teclado.hasNextLine()": "si el teclado se cerro terminamos el programa",
    "teclado.nextLine()": "obtenemos el texto completo escrito por el usuario",
    "Map<String, Integer> servicios": "el mapa relaciona un nombre de servicio con su puerto",
    "new ServerSocket(6000)": "el servidor tcp escucha conexiones en el puerto 6000",
    "boolean activo": "esta variable determina cuando el servidor debe terminar",
    "while (activo)": "seguimos atendiendo conexiones mientras no pidan salir",
    "servidor.accept()": "esperamos hasta que un cliente tcp se conecte",
    "cliente.getInputStream()": "obtenemos los datos que llegan desde el otro proceso",
    "cliente.getOutputStream()": "obtenemos el canal para enviar datos al otro proceso",
    "String solicitud;": "guardamos cada orden enviada por el usuario",
    "entrada.readLine()": "esperamos y leemos una linea completa por tcp",
    "split(\"\\\\s+\")": "separamos la orden en palabras usando espacios",
    "partes[0].equalsIgnoreCase(\"SALIR\")": "esta orden termina el servidor despues de responder",
    "partes[0].equalsIgnoreCase(\"REGISTRAR\")": "registramos un servicio y el puerto indicado",
    "Integer.parseInt(partes[2])": "convertimos el puerto de texto a un numero",
    "puerto < 1 || puerto > 65535": "validamos el rango de puertos tcp valido",
    "servicios.put": "guardamos o actualizamos el nombre y su puerto",
    "partes[0].equalsIgnoreCase(\"BUSCAR\")": "buscamos el servicio por su nombre en el mapa",
    "servicios.get": "obtenemos el puerto registrado o null si no existe",
    "salida.println(respuesta)": "respondemos al cliente con una linea completa",
    "salida.println(solicitud)": "enviamos la orden escrita al servidor tcp",
    "try (Socket cliente": "abrimos la conexion tcp al servidor en localhost",
    "if (respuesta == null)": "si se cerro la conexion dejamos de leer",
}

for name in NAMES:
    src = (ROOT / f"{name}.java").read_text(encoding="utf-8")
    out = []
    for line in src.splitlines():
        code = line.strip()
        for needle, explanation in EXPLANATIONS.items():
            if needle in code:
                indentation = line[:len(line) - len(line.lstrip())]
                out.append(indentation + "// " + explanation)
                break
        out.append(line.replace(f"public class {name}", f"public class {name}_ultracommented"))
    (ROOT / f"{name}_ultracommented.java").write_text("\n".join(out) + "\n", encoding="utf-8")
