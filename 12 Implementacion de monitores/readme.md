# Actividad 12 — Implementación de monitores

Los archivos Java y LaTeX están directamente en esta carpeta. La plantilla y el logo institucional se reutilizan desde `../latex_compartido/`.

## Requisitos

- **Java JDK** para utilizar `javac` y `java`.
- **LaTeX** (MacTeX en macOS o TeX Live en Linux, con `latexmk` y los paquetes del estilo institucional) para generar el PDF.
- Ejecuta los comandos **desde esta carpeta**. Si estás en la raíz del repositorio, entra con:

```bash
cd "12 Implementacion de monitores"
```

## Compilar y ejecutar Java

**Ejercicio 1: productor–consumidor con monitor**

```bash
javac ProductorConsumidorMonitor.java
java ProductorConsumidorMonitor
```

Crea dos productores y dos consumidores que comparten un búfer circular de cinco espacios. Al final debe aparecer `40 elementos consumidos exactamente una vez: true`.

**Ejercicio 2: filósofos con monitor**

```bash
javac FilosofosMonitor.java
java FilosofosMonitor
```

Crea cinco filósofos. Al terminar, cada uno debe indicar que comió **20 veces**. El orden de los mensajes cambia entre ejecuciones.

Para ejecutar cualquiera de las versiones ultracomentadas, compila y ejecuta su nombre completo; por ejemplo:

```bash
javac FilosofosMonitor_ultracommented.java
java FilosofosMonitor_ultracommented
```

También existe `ProductorConsumidorMonitor_ultracommented.java`. Los archivos `.class` se generan localmente y no se suben a Git.

## Compilar el reporte LaTeX

```bash
latexmk -pdf -interaction=nonstopmode -halt-on-error -outdir=build Actividad12_Hernandez_Andrea_Espino_Heriberto.tex
```

El PDF quedará en `build/Actividad12_Hernandez_Andrea_Espino_Heriberto.pdf`. Para copiarlo junto al `.tex`:

```bash
cp build/Actividad12_Hernandez_Andrea_Espino_Heriberto.pdf .
```

En **PowerShell (Windows)**, el equivalente es `Copy-Item build/Actividad12_Hernandez_Andrea_Espino_Heriberto.pdf .`.

Los auxiliares de compilación se guardan en `build/`.

## Capturas, Git y compilación automática

Agrega capturas reales en `figuras/12_productor_consumidor.png` y `figuras/12_filosofos_monitor.png`. Mientras falten, el LaTeX deja espacios reservados.

El flujo de **GitHub Actions** compila el reporte al subir cambios al `.tex` en `main` y hace commit del PDF y de `build/`. Si prefieres guardar tú mismo el resultado, desde esta carpeta ejecuta:

```bash
git add -f Actividad12_Hernandez_Andrea_Espino_Heriberto.pdf build/
git commit -m "build: actualizar reporte actividad 12"
git push
```

Verifica que el programa haya terminado y que las capturas correspondan a ejecuciones reales.
