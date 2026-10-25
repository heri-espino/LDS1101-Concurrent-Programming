# Actividad 13 — Monitores en Java

La carpeta conserva el PDF original de instrucciones y contiene directamente los cinco programas Java, sus cinco versiones `_ultracommented.java`, el reporte `.tex`, el PDF generado y `build/` con los auxiliares de LaTeX. El formato UDLAP y el logo se reutilizan desde `../latex_compartido/`.

## Requisitos

Instala un **JDK 17 o superior** (`java` y `javac`) y una distribución LaTeX con `latexmk`, como MacTeX (macOS) o TeX Live (Linux). Abre la terminal en la raíz del repositorio y entra a esta carpeta:

```bash
cd "13 Monitores parte II"
```

## Compilar y ejecutar Java

Para compilar **todos los archivos Java** (normales y ultracomentados):

```bash
javac *.java
```

Para ejecutar los cinco apartados:

```bash
java ColaEnlazada
java VentaBoletos
java Interbloqueo
java InterbloqueoSolucion
java MetodosThread
```

En `ColaEnlazada`, dos productores depositan 15 elementos cada uno y dos consumidores retiran 30 en total. La salida termina con `Sin perdidas ni duplicados: true`.

En `VentaBoletos`, los dos eventos comienzan con 6 boletos, se reciben 10 intentos de compra por evento y el resultado esperado es `Ventas totales: 12`.

En `Interbloqueo`, ambos hilos obtienen un monitor distinto y quedan esperando el del otro. El programa termina su demostración tras un tiempo limitado gracias a los hilos `daemon`: **no significa que se haya resuelto el interbloqueo**.

En `InterbloqueoSolucion`, ambos usan el mismo orden de monitores. Debe imprimirse `Ambos hilos terminaron: true` y `Valores finales: p=10, q=20`.

En `MetodosThread`, observa `interrupt()`, `yield()` y `holdsLock()`. **yield no garantiza un cambio de contexto**.

Para ejecutar una versión ultracomentada:

```bash
java ColaEnlazada_ultracommented
```

El sufijo `_ultracommented` se aplica a los otros cuatro nombres. Los archivos `.class` son temporales y están ignorados por Git.

## Compilar el reporte PDF

Desde esta misma carpeta:

```bash
latexmk -pdf -interaction=nonstopmode -halt-on-error -outdir=build Actividad13_Hernandez_Andrea_Espino_Heriberto.tex
cp build/Actividad13_Hernandez_Andrea_Espino_Heriberto.pdf .
```

En Windows PowerShell sustituye la segunda instrucción por:

```powershell
Copy-Item build/Actividad13_Hernandez_Andrea_Espino_Heriberto.pdf .
```

El PDF final queda junto al `.tex`, mientras que los archivos `.aux`, `.log`, `.toc`, etc. se guardan en `build/`.

## Capturas, pruebas y Git

Ejecuta cada programa al menos cinco veces para observar si sus resultados cambian. GitHub Actions compila los Java, verifica las cinco ejecuciones de cada apartado, compila el informe y sube el PDF y los auxiliares en un commit automático cuando se actualiza el reporte.

El reporte tiene espacios para añadir **capturas reales** desde tu computadora con los nombres `captura_cola.png`, `captura_boletos.png`, `captura_interbloqueo.png`, `captura_solucion.png` y `captura_thread.png`. No están generadas ni se consideran evidencias hasta que las agregues.

Para guardar manualmente el PDF y auxiliares:

```bash
git add -f Actividad13_Hernandez_Andrea_Espino_Heriberto.pdf build/
git commit -m "build: actualizar reporte actividad 13"
git push
```
