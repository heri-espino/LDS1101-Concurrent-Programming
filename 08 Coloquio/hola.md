Representacion matematica de texto, tareas de  y funcionamiento basico.

Mucha información se encuentra en _lenguaje natural_: publicaciones en redes sociales, mensajes, transcripciones, etc. y se consideran _datos no estructurados_. Aqui aparece el Procesamiento de Lenguaje Natural (PLN).

tambien ponerlo en un enumitem
Antes de poder aplicar algoritmos sobre el texto, tenemos que transformarlo para poder manipularlo. A este proceso se llama hacer _embeddings_, que representan palabras y sus relaciones con el resto de texto a traves de mappear cada _token_ a un espacio vectorial. Con esto ya podemos tratarlo para hacer tareas de clasifiacion, agrupamiento, brecomendacion (basada en contenido (sim coseno) o filtro colavorativo (factorizacino matrecial), o sistemas hibridos), analisis de sentimiento, o generacion de lenguaje.

Expricar bien esto, pero mantenerlo en un parrafo corto. Es importante porque el es mi profe de programacion concurrente y nos dejo de tarea leer Attention is all you need. Tambien pon como referencia a statquest playlist on nn https://www.youtube.com/watch?v=zxagGtF9MeU&list=PLblh5JKOoLUIxGDQs4LFFD--41Vzf-ME1&index=1 y 3blue1brown https://www.youtube.com/watch?v=aircAruvnKk&list=PLZHQObOWTQDNU6R1_67000Dx_ZCJB-3pi
La popularización de estos algoritmos surgió cuando se empezó a usar tarjetas gráficas para paralizar el trabajo y pudiera calcularse de forma eficiente.
Una coneccion natural entre programación concurrente y el procesamiento de lenguaje natural es que sale con este tipo de algoritmo se hace el uso de Transformers para paralizar el procesamiento de embeddings. En el manuscrito original de Google usaban Keys, Values y otra cosa. Que se obtienen a través de Launcher Three memories y similitud entre x y y (cosalpha = x^T y / normx norm y) entre los embeddings (tenemos que saber cosas de redes normales recurrentes y los problemas del gradiente que se desvanece o explota, seq2seq, word2vec). Además de auto en Coles y de colar como Chat, que es un modelo de solo de solo decoding. Citar al manuscrito original de attention is all you need.

Explicar esto con un ennumitem y poner los ejemplos que se dieron en las diapositivas, me faltan muchas cosas tambien.
Para la lingüística computacional, se pone mayor atención en aspectos foneticos (sonidos fisicos), fonologicos (como funcionan y se organizan);morfológicos (estructura de las palabras), de aqui se puede hacer lematizacion (cantabamos \to cantar) o stemming (obtener una raiz, trabajadores, trabajando, ... \to trabaj), sintácticos (como se organizan, mary saw the man with a telescope), semánticos (diferentes significados de una palabra segun el contexto). Para eso es muy importante que nuestros textos estén bien clasificados. Necesitamos que las etiquetas sean correctas para que el modelo Minee es el error correctamente durante su fase de entrenamiento.

Este problema se vuelve difícil para lenguas, poco habladas, o donde casi no hay documentos. Pues no tenemos datos etiquetados y tampoco tenemos suficientes datos por ejemplo en las lenguas indígenas como el náhuatl maya o zapoteco.

La importancia del tema para México es doble. Por una parte, el español constituye una lengua de enorme importancia demográfica y digital. Por otra, la diversidad de lenguas indígenas plantea problemas computacionales donde todavía existe escasez de corpus y herramientas, haciendo particularmente valiosa la construcción de recursos lingüísticos.





