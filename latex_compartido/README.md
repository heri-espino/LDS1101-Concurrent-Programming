# Plantilla LaTeX compartida

El formato común vive una sola vez en `latex_compartido/estilo.sty`.

En cada actividad nueva se guardan únicamente su archivo .tex y sus evidencias en figuras/.

Desde la carpeta latex:

```bash
mkdir -p build
xelatex -interaction=nonstopmode -halt-on-error -output-directory=build ActividadNN.tex
xelatex -interaction=nonstopmode -halt-on-error -output-directory=build ActividadNN.tex
```

El archivo debe comenzar con:

```latex
\documentclass[12pt]{report}
\usepackage{../../latex_compartido/estilo}
```

Las capturas se cargan desde figuras/. Si faltan, el reporte reserva el espacio sin inventar una captura. Los reportes antiguos mantienen sus plantillas para preservar compatibilidad.
