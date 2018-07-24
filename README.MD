# Modelo C4
[TODO]


# Structurizr y generación de modelos
[TODO]


# Ejemplos

Se recomienda importar el proyecto en un IDE como Eclipse o STS para facilitar la ejecución de los ejemplos. 

Cada ejemplo consiste en la ejecución de un método `main` de las clase `App` correspondiente al ejemplo. En cada método se define código que generará modelos a partir de definiciones escritas mediante el API de Structurizer.

Como precondición de los ejemplo, se requiere clonar el repositorio de Spring Clinic, el cual es una aplicación web basada en SpringBoot y está disponible en la siguiente ubicación.

	https://github.com/spring-projects/spring-petclinic.git

Posteriormente se deberá construir el projecto utilizando maven para que esté disponible en el repositorio local.

```
  mvn clean install 
```

## Ejemplo básico

Este ejemplo muestra como generar un espacio de trabajo de Structurizr utilizando el API para Java a la vez que se agregan algunos elementos simples de forma manual y se encuentra ubicado en el paquete `com.structurizr.example.structurizrdot`. 

El espacio de trabajo puede ser exportado de diferentes formas para su visualización. Por facilidad en este ejemplo se generará código en formato DOT, el cual puede ser interpretado por la herramienta DOT, que viene junto con la , para generar diagramas en diferentes formatos (PDF, png, jpg, etc).

Otra posibilidad es utilizar el sitio web GraphvizFiddle para visualizar los diagramas.

    https://stamm-wilbrandt.de/GraphvizFiddle/ 

## Anotaciones de Structurizr

Este segundo ejemplo, ubicado en el paquete `com.structurizr.example.annotations`, generará código en formato DOT a partir de clases anotadas dentro del mismo paquete. Estas anotaciones son definidas por el API de Structurizr y permiten generar componentes a partir del código fuente.

## Anotaciones de Spring - Spring Clinic

En el último ejemplo, accesible en el paquete `` se definen una serie de elementos para construir vistas de diferentes tipos (sistema, componentes, despliegue). Algunos componentes son generados dinámicamente a partir de las clases marcadas con anotaciones de Spring Framework como son @Component, @Service, @Controller o @Repository. 

En este caso el código generado está en un formato que puede visualizarse mediente la herramienta PlanUML o bien mediante el sitio web

    http://www.plantuml.com/plantuml/
