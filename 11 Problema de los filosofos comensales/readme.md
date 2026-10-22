# Actividad 11 — Problema de los filósofos comensales

Los archivos Java y LaTeX están directamente en esta carpeta. La plantilla y el logo institucional son compartidos desde `../latex_compartido/`.

## Requisitos

- **Java JDK** para usar `javac` y `java`.
- **LaTeX** (MacTeX en macOS o TeX Live en Linux, con `latexmk` y los paquetes del estilo institucional) para generar el PDF.
- Ejecuta los comandos **desde esta carpeta**. Si estás en la raíz del repositorio, entra primero con:

```bash
cd "11 Problema de los filosofos comensales"
```

## Compilar y ejecutar Java

```bash
javac FilosofosComensales.java
java FilosofosComensales
java FilosofosComensales mejorada
```

- Sin argumentos: versión original, que muestra el **riesgo de interbloqueo**. Puede dejar a los cinco filósofos esperando. El programa informa cuántos hilos no terminaron tras 2 segundos; este límite de tiempo no resuelve el interbloqueo.
- Con `mejorada`: el filósofo 0 cambia el orden en que toma los tenedores, evitando la espera circular.

Para probar la versión **ultracomentada** por separado:

```bash
javac FilosofosComensales_ultracommented.java
java FilosofosComensales_ultracommented mejorada
```

Los archivos `.class` se generan al compilar Java y no se suben a Git.

## Compilar el reporte LaTeX

```bash
latexmk -pdf -interaction=nonstopmode -halt-on-error -outdir=build Actividad11_Hernandez_Andrea_Espino_Heriberto.tex
```

El PDF se genera en `build/Actividad11_Hernandez_Andrea_Espino_Heriberto.pdf`. Para tenerlo también en la raíz de la actividad:

```bash
cp build/Actividad11_Hernandez_Andrea_Espino_Heriberto.pdf .
```

En **PowerShell (Windows)**, usa `Copy-Item build/Actividad11_Hernandez_Andrea_Espino_Heriberto.pdf .` en lugar de `cp`.

El resto de los auxiliares de compilación (`.aux`, `.log`, `.toc`, etc.) quedan en `build/`.

## Capturas, Git y compilación automática

Guarda capturas reales en `figuras/11_original.png` y `figuras/11_mejorada.png` cuando las tengas. El LaTeX deja un espacio reservado si aún no existen.

El repositorio incluye un flujo de **GitHub Actions** que, al subir cambios al `.tex` de esta actividad en `main`, recompila el reporte y hace commit del PDF y de los archivos de `build/`. No necesitas subirlos manualmente si el flujo terminó correctamente. Para hacerlo manualmente desde esta carpeta:

```bash
git add -f Actividad11_Hernandez_Andrea_Espino_Heriberto.pdf build/
git commit -m "build: actualizar reporte actividad 11"
git push
```

Comprueba primero el resultado de la compilación: las capturas pendientes no deben presentarse como evidencia real.
