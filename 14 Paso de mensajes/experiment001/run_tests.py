"""Run three real client/server sessions for each protocol (local loopback)."""
from pathlib import Path
import csv
import subprocess
import time

ROOT = Path(__file__).resolve().parent.parent
OUT = Path(__file__).resolve().parent / "results"
OUT.mkdir(exist_ok=True)

UDP = [
    (["Hola servidor", "SALIR"], "Hola servidor", "Mensaje recibido: Hola servidor"),
    (["Prueba de mensajes", "SALIR"], "Prueba de mensajes", "Mensaje recibido: Prueba de mensajes"),
    (["UDP funciona", "SALIR"], "UDP funciona", "Mensaje recibido: UDP funciona"),
]
TCP = [
    (["REGISTRAR calculadora 7000", "BUSCAR calculadora", "BUSCAR inventario", "SALIR"],
     "BUSCAR calculadora", "Servicio encontrado en el puerto 7000"),
    (["REGISTRAR inventario 7001", "BUSCAR inventario", "SALIR"],
     "BUSCAR inventario", "Servicio encontrado en el puerto 7001"),
    (["BUSCAR desconocido", "REGISTRAR agenda 7010", "BUSCAR agenda", "SALIR"],
     "BUSCAR desconocido", "Servicio no encontrado"),
]

subprocess.run(["javac", *[p.name for p in ROOT.glob("*.java")]],
               cwd=ROOT, check=True, capture_output=True, text=True)
rows = []

for protocol, cases in [("UDP", UDP), ("TCP", TCP)]:
    for number, (commands, chosen, expected) in enumerate(cases, start=1):
        prefix = f"{protocol.lower()}_{number:02}"
        with (OUT / f"{prefix}_servidor.txt").open("w", encoding="utf-8") as server_log:
            server = subprocess.Popen(["java", f"Servidor{protocol}"], cwd=ROOT,
                                      stdout=server_log, stderr=subprocess.STDOUT, text=True)
            try:
                time.sleep(0.35)
                if server.poll() is not None:
                    raise AssertionError(f"Server exited unexpectedly in {prefix}")
                result = subprocess.run(["java", f"Cliente{protocol}"], cwd=ROOT,
                                        input="\n".join(commands) + "\n", text=True,
                                        capture_output=True, timeout=8)
                (OUT / f"{prefix}_cliente.txt").write_text(
                    result.stdout + result.stderr, encoding="utf-8")
                assert result.returncode == 0, (prefix, result.stderr)
                assert expected in result.stdout, (prefix, result.stdout)
                assert ("Mensaje recibido: SALIR" if protocol == "UDP" else "Conexion cerrada") in result.stdout
                server.wait(timeout=5)
                assert server.returncode == 0, (prefix, "server did not exit normally")
                rows.append({"Prueba": number if protocol == "UDP" else number + 3,
                             "Protocolo": protocol, "Mensaje enviado": chosen,
                             "Respuesta": expected, "Resultado": "Correcto"})
                print(f"{prefix}: OK")
            finally:
                if server.poll() is None:
                    server.terminate()
                    try:
                        server.wait(timeout=2)
                    except subprocess.TimeoutExpired:
                        server.kill()
                        server.wait()

with (OUT / "tabla_pruebas.csv").open("w", encoding="utf-8", newline="") as f:
    writer = csv.DictWriter(f, fieldnames=rows[0].keys())
    writer.writeheader()
    writer.writerows(rows)

print("Seis sesiones completas: 3 UDP + 3 TCP; todas correctas")
