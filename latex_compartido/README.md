# Plantilla LaTeX compartida

Los reportes 11 y 12 reutilizan el **formato institucional** de la actividad 10: portada con logo UDLAP, tipografía, márgenes y estilos de títulos. Los archivos están aquí:

- `estilo.sty`: comandos y formato compartidos.
- `logo.png`: imagen institucional usada por ambas portadas.

Así no se repite una carpeta `template/` por actividad.

## Estructura y compilación

Cada carpeta de actividad 11 y 12 contiene directamente sus `.java`, `*_ultracommented.java`, `.tex`, `.pdf`, `build/` y sus instrucciones `readme.md`.

El inicio de cada reporte es:

```latex
\documentclass[12pt,oneside]{book}
\usepackage{../latex_compartido/estilo}
```

**Compila desde la carpeta de la actividad**, no desde `latex_compartido`. Por ejemplo, en la actividad 11:

```bash
latexmk -pdf -interaction=nonstopmode -halt-on-error -outdir=build Actividad11_Hernandez_Andrea_Espino_Heriberto.tex
cp build/Actividad11_Hernandez_Andrea_Espino_Heriberto.pdf .
```

Necesitas MacTeX o TeX Live con `latexmk` y los paquetes usados en `estilo.sty` (incluidos `libertine` y `newtxmath`). La compilación genera auxiliares en `build/` y el PDF final se copia a la carpeta de la actividad.

Al subir cambios de cualquiera de los dos reportes `.tex` a `main`, **GitHub Actions** recompila ambos PDF y versiona los resultados, incluidos los auxiliares. Las capturas de ejecuciones deben ser reales; el documento muestra un espacio reservado cuando no se han agregado.

Los reportes más antiguos conservan sus plantillas originales para no modificar entregas anteriores.
