# Reporte Proyecto Juego: Calypso, Skill Issue Edition

**Asignatura:** ICI2241 / INF2236, Programación Avanzada
**Profesor:** Claudio Cubillos
**Proyecto:** Juego (GM), semestre II-2026, ID de tema 9

**Integrantes:**
- Renato Agustín Bermúdez Salfate
- Iván Octavio García Briones
- Nicolás Daniel Torres González

**Repositorio:** https://github.com/Katongo777/CalypsoGM

---

## Introducción

Partimos del juego base **SpaceNav2024** entregado en el curso, un juego tipo *Asteroids* hecho en Java con LibGDX, y lo transformamos en **Calypso: Skill Issue Edition**, un juego de naves por oleadas con humor de comunidad gamer. El trabajo tuvo tres ejes:

1. **Rediseñar el código** del juego base: separar responsabilidades, encapsular, construir jerarquías de clases e interfaces y respetar los principios de diseño orientado a objetos.
2. **Aplicar los cuatro patrones** vistos en clases: Singleton, Strategy, Template Method y Builder.
3. **Hacer un juego completo y equilibrado**: personajes, armas, enemigos con comportamiento propio, jefes con fases, power-ups y una campaña de 20 oleadas con tres niveles de dificultad.

El proyecto compila y se ejecuta con **JDK 11**. El reporte sigue el orden de los requerimientos GM-1 a GM-10. No incluye código fuente: el diseño se explica con texto, tablas y diagramas UML generados directamente desde el código, para que coincidan con la versión final.

| Requerimiento | Sección |
|---|---|
| GM-1 Explicación del juego desde el jugador | 1 |
| GM-2 Análisis desde la ingeniería de software | 2 |
| GM-3 Diagrama de clases del dominio | 3 |
| GM-4 Clases abstractas e interfaces | 4 |
| GM-5 Encapsulamiento y principios de diseño | 5 |
| GM-6 Patrón Singleton | 6 |
| GM-7 Patrón Strategy | 7 |
| GM-8 Patrón Template Method | 8 |
| GM-9 Patrón Builder | 9 |
| GM-10 GitHub, commits e instrucciones de ejecución | 10 |

---

## 1. Explicación del juego desde la perspectiva del jugador (GM-1)

### Narrativa
El servidor **Calypso** está infestado. Hay **bots** que solo saben avanzar, **lags** con el ping en 999 que se teletransportan, **cheaters** con lentes oscuros, **spammers** que inundan el chat de balas y **snipers** que te apuntan desde lejos. Al final de cada bloque de oleadas te espera un jefe: **El Toxic**, **El Hacker**, **El Sweaty** y, al final de todo, **El Admin**, que tiene el poder de banear. Tú eres un jugador que acaba de entrar a la partida y tiene que sobrevivir y bajar al Admin.

El tono es de humor de comunidad gamer: el locutor grita *FIRST BLOOD* o *KILLING SPREE*, y si te hacen daño te dice *"Skill issue"* o *"Eso fue lag, lo juro"*.

### Objetivo
Pasar las **20 oleadas** de la campaña eliminando a todos los enemigos de cada una, derrotar al jefe final (El Admin) y juntar puntos para **superar tu récord**, que queda guardado. Si pierdes todas tus vidas, aparece el Game Over.

### Cómo se juega
1. El **menú** es de botones y se maneja con mouse o con flechas y ENTER: *Jugar, Cómo sobrevivir, Opciones y Salir*. La primera vez que juegas se abre la **guía "Cómo sobrevivir"**.
2. Eliges a tu **personaje** con flechas o con el mouse. Cada uno tiene su arma, sus vidas y su **habilidad especial**.
3. Cada oleada empieza con un cartel (*"OLEADA 2/20"*, su nombre y un aviso si hay un enemigo nuevo o un jefe). Se puede saltar con ENTER. Tu nave aparece desde abajo y los enemigos entran en grupos desde arriba.
4. Mueves la nave con las **flechas** y disparas manteniendo **ESPACIO**. Los enemigos también disparan: esquiva sus balas y las líneas rojas que avisan los rayos.
5. Eliminar enemigos llena la **barra de habilidad**; cuando está llena, aprieta **X**.
6. Si te golpean pierdes una vida: el corazón se rompe, la pantalla tiembla y quedas invulnerable 3 segundos, pero haces la mitad de daño. Antes de cada jefe recuperas hasta 3 vidas, y derrotarlo te da 2 más.
7. Con **ESC** o **P** abres el menú de pausa (Continuar, Opciones, Salir al menú).
8. Si derrotas al jefe de la oleada 20 aparece la pantalla de **VICTORIA**. Si te quedas sin vidas, en el **Game Over** presionas **F** para "presentar tus respetos" y volver a jugar.

#### Controles

| Tecla | Acción |
|---|---|
| Flechas | Mover la nave |
| ESPACIO (mantener) | Disparar |
| X | Habilidad especial (con la barra llena) |
| P o ESC | Pausa |
| ENTER | Aceptar en los menús; saltar el cartel de oleada |
| Mouse | Elegir y accionar botones y personajes |
| H | Abrir la guía (desde el menú) |
| ← → | Elegir personaje; cambiar valores en Opciones |
| F | Volver a jugar en el Game Over |
| ↑ ↑ ↓ ↓ ← → ← → B A (en el menú) | **Código Konami**: modo dios |

### Elementos del juego

#### Personajes
| Personaje | Arma inicial | Vidas | Velocidad | Habilidad (X) | Frase |
|---|---|---|---|---|---|
| **Tryhard** | Pistola | 4 | 7 | Overdrive | "Suda hasta en el tutorial." |
| **Camper** | Railgun | 5 | 6 | Tiro certero | "Una bala, una baja." |
| **Main Support** | Escopeta | 5 | 6 | Burbuja | "Cura con amor... y con perdigones." |

#### Armas
| Arma | Cómo dispara |
|---|---|
| **Pistola** | Una bala recta, cadencia normal |
| **Escopeta** | Tres perdigones en abanico, cadencia lenta |
| **Railgun** | Un rayo muy rápido que atraviesa a todos los enemigos (2 de daño) |
| **BFG 9000** | Una esfera enorme y lenta que atraviesa todo (5 de daño) |

#### Habilidades especiales (tecla X)
| Personaje | Habilidad | Efecto |
|---|---|---|
| **Tryhard** | Overdrive | 6 segundos disparando al doble de velocidad |
| **Camper** | Tiro certero | Un rayo gigante que atraviesa todo |
| **Main Support** | Burbuja | Cura 1 vida, da escudo 5 s y borra las balas enemigas |

#### Enemigos
| Enemigo | Descripción | Vida | Puntos |
|---|---|---|---|
| **Bot** | Robot que avanza recto, rebota y dispara de vez en cuando | 1 | 10 |
| **Lag** | Burbuja roja con el ping en 999. Se teletransporta, pero tiembla y parpadea justo antes de saltar | 1 | 15 |
| **Cheater** | Grande y resistente, con lentes oscuros; dispara 3 balas apuntadas y al morir se parte en 2 Lags | 3 | 50 |
| **Spammer** (desde la oleada 6) | Globo de chat que flota de lado a lado y dispara ráfagas de 3 balas | 1 | 20 |
| **Sniper** (desde la oleada 11) | Se detiene, apunta con una línea roja y dispara una bala rápida | 2 | 30 |
| **Jefes** (oleadas 5, 10, 15 y 20) | El Toxic, El Hacker, El Sweaty y El Admin. Barra de vida arriba, ataques propios y un escudo en cada cambio de fase | 28 / 36 / 48 / 55 | 500 / 1000 / 1500 / 3000 |

Los enemigos normales se mueven en la parte de arriba de la pantalla; abajo queda espacio para maniobrar. Los jefes no desaparecen al chocar con tu nave y siempre dejan un power-up al morir.

#### La campaña
| Oleadas | Qué pasa |
|---|---|
| 1 a 3 | Bots, luego Lags y luego Cheaters |
| 4 | **Hardcore**: más enemigos, más rápidos y más agresivos, pero caen más power-ups |
| 5 | **Jefe: El Toxic** |
| 6 a 9 | Llegan los Spammers (la 8 es hardcore) |
| 10 | **Jefe: El Hacker** |
| 11 a 14 | Llegan los Snipers (la 12 es hardcore) |
| 15 | **Jefe: El Sweaty** |
| 16 a 19 | Todo junto (la 17 y la 19 son hardcore) |
| 20 | **Jefe final: El Admin** |

La dificultad crece de forma pareja: en cada oleada los enemigos disparan un poco más seguido y un poco más rápido, así las primeras enseñan y las últimas exigen, y el reto no se concentra solo en los jefes.

#### Dificultad (se elige en Opciones)
| Dificultad | Enemigos | Disparos enemigos | Balas | Aviso de los rayos | Vidas extra |
|---|---|---|---|---|---|
| **Fácil** | Más lentos | La mitad de seguido | Más lentas | Más tiempo para esquivar | +3 |
| **Normal** | Algo más lentos | Algo menos seguido | Algo más lentas | Algo más de tiempo | +1 |
| **Difícil** | Más rápidos | Más seguido | Más rápidas | Menos tiempo | 0 |

#### Power-ups (los sueltan los enemigos al morir)
| Power-up | Efecto |
|---|---|
| **Pizza** | +1 vida |
| **Silla gamer** | Escudo durante 10 segundos |
| **Teclado RGB** | Disparo al doble de velocidad durante 10 segundos |
| **Caja de botín** | Un arma al azar durante 15 segundos (Escopeta, Railgun o, con suerte, el BFG 9000) |

Si te queda una sola vida, los power-ups caen más seguido y son pizzas.

#### Otros detalles
- **Locutor:** anuncia *FIRST BLOOD*, *COMBO x3*, *KILLING SPREE*, *RAMPAGE* y *GODLIKE* según tu racha de eliminaciones.
- **Fuente pixel-art propia** para todos los textos.
- **Récord y opciones:** el récord y las opciones (volumen y dificultad) se guardan en archivos.
- **Música:** una canción para el menú, otra para el juego y otra más tensa para los jefes.
- **Efectos de impacto:** temblor de pantalla, partículas, destello rojo al ser golpeado y corazón que se rompe.
- **Modo dios (easter egg):** el código Konami en el menú da 30 vidas y el BFG 9000.

### Pantallazos

#### Menú principal
![Menú](capturas/menu.png)

![Menú con modo dios](capturas/menu_modo_dios.png)

#### Guía "Cómo sobrevivir" (2 páginas)
![Guía página 1](capturas/guia_1.png)

![Guía página 2](capturas/guia_2.png)

#### Selección de personaje
![Selección de personaje](capturas/seleccion.png)

#### Cartel de inicio de oleada
![Cartel de oleada](capturas/intro_oleada.png)

#### Jugando (barra de habilidad abajo a la izquierda, enemigos que disparan)
![Oleada 1](capturas/oleada1.png)

![Balas enemigas](capturas/oleada2_balas.png)

![Power-ups](capturas/powerups.png)

![Combo](capturas/combo.png)

#### Armas y habilidad
![Railgun](capturas/railgun_camper.png)

![Habilidad del Camper](capturas/habilidad_camper.png)

![BFG](capturas/bfg.png)

#### Oleada hardcore
![Hardcore](capturas/hardcore.png)

#### Jefes
![El Toxic](capturas/jefe_toxic.png)

![El Toxic con escudo](capturas/jefe_toxic_escudo.png)

![El Hacker](capturas/jefe_hacker.png)

![El Hacker: rayos y escudo](capturas/jefe_hacker_rayos.png)

![El Sweaty](capturas/jefe_sweaty.png)

![El Admin](capturas/jefe_admin.png)

![El Admin: rayos de ban](capturas/jefe_admin_rayos.png)

#### Pausa y opciones
![Pausa](capturas/pausa.png)

![Opciones](capturas/opciones.png)

#### Game Over y Victoria
![Game Over](capturas/game_over.png)

![Victoria](capturas/victoria.png)

---

## 2. Análisis desde la ingeniería de software (GM-2)

### 2.1 Arquitectura del juego base

SpaceNav2024 es una aplicación LibGDX organizada en dos módulos de Gradle: `core`, con la lógica del juego, y `lwjgl3`, con el lanzador de escritorio. El módulo `core` tenía **7 clases en un único paquete**, con 668 líneas en total:

| Clase | Responsabilidad | Tamaño |
|---|---|---|
| `SpaceNavigation` | Hereda de `Game`. Crea el `SpriteBatch` y la fuente, y guarda el puntaje máximo en memoria | 52 líneas |
| `PantallaMenu`, `PantallaGameOver` | Implementan `Screen` directamente | 79 líneas cada una |
| `PantallaJuego` | Implementa `Screen`. Carga recursos, crea la nave y los asteroides, mueve todo, revisa colisiones, dibuja el HUD y decide la ronda siguiente | 202 líneas |
| `Nave4` | Movimiento con teclado, disparo, vidas y estado herido | 127 líneas |
| `Ball2` | Asteroide que rebota en los bordes y contra otros asteroides | 80 líneas |
| `Bullet` | Bala del jugador | 49 líneas |

El ciclo de juego es el de LibGDX: `Game` delega en la `Screen` activa, que en cada fotograma ejecuta su `render(delta)`. En el juego base, `PantallaJuego.render` concentraba en un solo método la entrada, la física, las colisiones, el dibujo y el cambio de ronda.

![Diagrama de clases del juego base](uml/00_juego_base.png)

### 2.2 Problemas de diseño detectados

Revisamos el código según los criterios del curso (cohesión, acoplamiento, encapsulamiento, extensibilidad y manejo de recursos):

| Criterio | Problema | Dónde | Consecuencia técnica |
|---|---|---|---|
| Manejo de recursos | Cada asteroide y cada ronda cargaban una `Texture` y un `Sound` nuevos con `new Texture(...)` y `Gdx.audio.newSound(...)`, y `dispose()` no los liberaba | `PantallaJuego` | Fuga de memoria proporcional a la cantidad de rondas jugadas |
| Acoplamiento | El método `draw` de la nave recibía la `PantallaJuego` y le agregaba las balas | `Nave4` | El modelo dependía de la vista: no se podía cambiar una sin la otra (dependencia circular) |
| Cohesión | `PantallaJuego.render` mezclaba entrada, lógica, colisiones, dibujo y transición de pantalla | `PantallaJuego` | Clase "dios": cualquier cambio tocaba el mismo método |
| Reutilización | `Nave4`, `Ball2` y `Bullet` repetían posición, velocidad, `Sprite`, dibujo y área de colisión | Las tres | Código duplicado; no existía un tipo común para tratarlas igual |
| Extensibilidad | Solo existía un tipo de enemigo (`Ball2`); agregar otro obligaba a modificar `PantallaJuego` en varios puntos | `PantallaJuego` | Violación del principio Abierto/Cerrado |
| Duplicación | Las tres pantallas repetían la limpieza, la cámara y el `batch.begin/end`, y tenían cinco métodos vacíos de `Screen` cada una | Pantallas | Código repetido y propenso a errores |
| Construcción | Cada ronda se creaba con un constructor de 7 parámetros posicionales (ronda, vidas, puntaje, velocidades y cantidad) | `PantallaJuego` | Difícil de leer y de ampliar con nuevas opciones |
| Consistencia | Dos listas paralelas con los mismos asteroides (`balls1` y `balls2`) | `PantallaJuego` | Había que mantenerlas sincronizadas a mano |
| Portabilidad | Las clases usaban `Gdx.graphics.getWidth()` para los límites del mundo | Varias | El juego se descuadraba al cambiar el tamaño de la ventana |
| Encapsulamiento | Setters sin validación (por ejemplo, `setVidas`) y estado expuesto | `Nave4`, `Ball2` | Cualquier clase podía dejar objetos en estados inválidos |
| Persistencia | El puntaje máximo vivía en un atributo de `SpaceNavigation` | `SpaceNavigation` | Se perdía al cerrar el juego |

### 2.3 Arquitectura propuesta

Reorganizamos el código en **paquetes con una responsabilidad cada uno** y con dependencias en un solo sentido: la vista conoce al modelo, pero el modelo no conoce a la vista.

| Paquete | Responsabilidad | Clases principales |
|---|---|---|
| `puppy.code` | Punto de entrada del juego y constantes del mundo | `SpaceNavigation`, `Constantes` |
| `modelo` | Entidades del juego y las interfaces de comunicación con la vista | `Entidad`, `Nave4`, `Bullet`, `BalaEnemiga`, `Rayo`, `Colisionable`, `ContextoCombate`, `EfectosCombate`, `Personaje`, `Dificultad` |
| `modelo.enemigos` | Jerarquía de enemigos y tipos de jefe | `Enemigo` y sus 6 hijas, `TipoJefe` |
| `modelo.jefes` | Ataques intercambiables de los jefes | `AtaqueJefe` y 8 ataques |
| `modelo.armas` | Armas intercambiables | `EstrategiaDisparo` y 4 armas |
| `modelo.habilidades` | Habilidades especiales | `Habilidad` y 3 habilidades |
| `modelo.powerups` | Power-ups y su creación | `PowerUp` y 4 power-ups, `FabricaPowerUp`, `GeneradorPowerUps` |
| `modelo.niveles` | Construcción de oleadas | `DirectorNiveles`, `NivelBuilder`, `NivelPartidaBuilder`, `Nivel` |
| `vista` | Pantallas y elementos de interfaz | `PantallaBase` y 7 pantallas, `Hud`, `BarraJefe`, `Locutor`, `MenuBotones`, `Particulas`, `FondoEstrellado` |
| `util` | Servicios compartidos | `GestorRecursos`, `GestorRecord`, `Opciones`, `CodigoKonami` |

**Comunicación entre modelo y vista.** Para que el modelo no dependa de la pantalla usamos dos interfaces:
- `ContextoCombate` funciona como un buzón. Los enemigos y las habilidades dejan en él balas, rayos, enemigos invocados, mensajes y pedidos de premio, y la pantalla los recoge una vez por fotograma. El contexto nunca entrega sus listas internas, solo copias.
- `EfectosCombate` es una interfaz que implementa `PantallaJuego`. Los enemigos la reciben cuando aparecen o mueren y piden efectos (anuncio, temblor, música, curar a la nave) sin saber qué pantalla los ejecuta.

**Flujo de pantallas.** `PantallaMenu` lleva a `PantallaSeleccion`, que crea la partida en `PantallaJuego`. Cada oleada completada crea la siguiente `PantallaJuego` con la misma nave y el mismo puntaje. Al perder se pasa a `PantallaGameOver` y al ganar la oleada 20, a `PantallaVictoria`. `PantallaOpciones` y `PantallaGuia` se abren desde el menú y desde la pausa.

**Ciclo de una oleada.** `PantallaJuego` pasa por tres fases (`INTRO`, `JUGANDO` y `COMPLETADA`). Los enemigos de la oleada ya vienen creados dentro del `Nivel` que arma el Builder, y entran en grupos pequeños desde arriba.

### 2.4 Modificaciones y su justificación

| Problema | Decisión de diseño | Patrón o principio | Impacto |
|---|---|---|---|
| Fuga de memoria | Un único gestor que carga cada textura, sonido y música una sola vez y los libera al cerrar el juego | Singleton (`GestorRecursos`) | La memoria usada ya no crece con las rondas |
| Modelo que depende de la vista | `Nave4.disparar()` devuelve las balas; los enemigos usan `ContextoCombate` y `EfectosCombate` | Inversión de dependencias | El modelo no importa ninguna clase de la vista |
| Clase "dios" en el render | El render se divide en pasos fijos y cada pantalla implementa solo los suyos | Template Method (`PantallaBase`) | Cada pantalla tiene métodos cortos y con un solo propósito |
| Código duplicado entre entidades | Clase abstracta `Entidad` con posición, velocidad, sprite, hitbox y dibujo | Herencia y polimorfismo | Nave, balas, enemigos y power-ups comparten una sola implementación |
| Un solo tipo de enemigo | Clase abstracta `Enemigo` con métodos gancho (`atacar`, `alMorir`, `alAparecer`, `alSerDerrotado`...) | Abierto/Cerrado, Liskov | Agregar un enemigo es crear una subclase, sin tocar la pantalla |
| Constructor de 7 parámetros | La oleada se arma paso a paso y el Director tiene las recetas | Builder | Oleadas normales, hardcore y de jefe con el mismo proceso |
| Arma fija | El arma es un objeto intercambiable en tiempo de ejecución | Strategy | Armas, habilidades y ataques de jefe nuevos sin modificar a quien los usa |
| Listas duplicadas y estado expuesto | Una sola lista por tipo de objeto, declarada con la interfaz `List` y nunca entregada hacia afuera | Encapsulamiento | Sin estados inconsistentes |
| Dependencia del tamaño de ventana | Mundo fijo de 1200x800 (`Constantes`) con `FitViewport` | — | El juego mantiene la proporción en cualquier ventana |
| Récord que se perdía | Clase `GestorRecord`, que guarda en archivo y maneja errores de lectura con excepciones | Responsabilidad única | El récord persiste y un archivo dañado no detiene el juego |
| Creación con `switch` por tipo | Fábricas registradas en una lista (`GeneradorPowerUps`) y enumeraciones que saben crear lo suyo (`Personaje`, `TipoJefe`, `Apoyo`) | Abierto/Cerrado | Un power-up, personaje o jefe nuevo se agrega en un solo lugar |
| Dificultad desproporcionada | La agresividad de los enemigos es parte del `Nivel` y crece con cada oleada | Builder | La dificultad sube de forma pareja en toda la campaña |

### 2.5 Nuevas funcionalidades
Sobre esta arquitectura agregamos selección de 3 personajes con arma y habilidad propia, 4 armas, 5 tipos de enemigos con comportamientos distintos, 4 jefes con fases, escudo y ataques propios, 4 power-ups, una campaña de 20 oleadas con oleadas hardcore, tres dificultades, locutor, HUD, guía, pausa con opciones guardadas, música y efectos de sonido, arte pixel-art y fuente propios, y el código Konami.

### 2.6 Métricas antes y después

| Métrica | Juego base | Calypso |
|---|---|---|
| Clases e interfaces (módulo `core`) | 7 | 72 (contando las enumeraciones y las clases internas) |
| Paquetes | 1 | 10 |
| Líneas de código Java (`core` y `lwjgl3`) | 668 | 6.900 aprox. |
| Clases abstractas | 0 | 4 |
| Interfaces propias | 0 | 7 |
| Patrones de diseño | 0 | 4 (Strategy aplicado en 3 lugares) |
| Mayor método de render | `PantallaJuego.render`, que hace todo | Método plantilla de 3 pasos en `PantallaBase` |
| Versión de Java | 11 | 11 |

### 2.7 Verificación

| Qué se prueba | Resultado esperado |
|---|---|
| Compilación con JDK 11 | `gradlew lwjgl3:jar` termina sin errores |
| Menú, selección y juego | Las pantallas cambian en orden; ENTER avanza |
| Cada personaje | Parte con su nave, sus vidas, su arma y su habilidad |
| Armas | Pistola recta; Escopeta en abanico; Railgun atraviesa; BFG grande y lento |
| Enemigos | El Cheater necesita 3 impactos y suelta 2 Lags; el Lag tiembla y se teletransporta; ningún enemigo daña mientras entra |
| Jefes | Barra de vida, escudo en cada cambio de fase que se rompe al derrotar al apoyo, música propia y premio al derrotarlo |
| Power-ups | Cada uno aplica su efecto; con una sola vida caen pizzas |
| Campaña completa | Se juega de la oleada 1 a la 20 hasta la pantalla de victoria sin trabarse |
| Pausa y opciones | ESC y P abren la pausa; el volumen y la dificultad se guardan en `opciones.txt` |
| Récord | Se guarda en `record.txt`; si el archivo está dañado, el juego empieza en 0 sin caerse |
| Ventana | El juego mantiene su proporción al cambiar el tamaño |

**Equilibrio de la dificultad.** Para ajustar la dificultad usamos un jugador automático, que no es parte del juego: juega la campaña completa sin ventana y registra los golpes recibidos en cada oleada. Lo corrimos con tres estilos de juego: uno que esquiva bien, uno torpe que reacciona tarde y se equivoca, y uno que no esquiva. Los resultados están en el anexo.

---

## 3. Diagrama de clases del dominio (GM-3)

El diagrama se generó **leyendo el código fuente de la versión final**, así que incluye todas las clases propias (con sus atributos, constructores y métodos, visibilidad y tipos) y las clases de LibGDX que el juego usa. Notación:

| Elemento | Notación |
|---|---|
| Visibilidad | `+` pública, `-` privada, `#` protegida, `~` de paquete |
| Miembro estático | subrayado |
| Constructor | `<<create>>` |
| Herencia | línea continua con triángulo vacío |
| Realización de interfaz | línea punteada con triángulo vacío |
| Asociación | flecha continua con el nombre del atributo y su multiplicidad (`1` o `*`) |
| Dependencia (usa, crea o recibe) | flecha punteada |
| Clase interna | círculo con cruz |
| Clases de LibGDX | estereotipo `<<LibGDX>>`, en su propio paquete |

### 3.1 Diagrama completo
Todas las clases del proyecto en un solo diagrama, conectadas entre sí y con LibGDX. Por su tamaño se entrega también como archivo aparte en alta resolución: `uml/00_diagrama_completo.png` y `uml/00_diagrama_completo.svg`, que se puede ampliar sin perder calidad.

![Diagrama de clases completo](uml/00_diagrama_completo.png)

### 3.2 El mismo diagrama, por paquete
Para leerlo con comodidad, cada paquete se muestra junto con las clases con las que se relaciona.

![Paquete modelo](uml/01_paquete_modelo.png)

![Paquetes de enemigos y jefes](uml/02_paquete_enemigos_y_jefes.png)

![Paquetes de armas, habilidades y power-ups](uml/03_paquete_armas_habilidades_powerups.png)

![Paquete de niveles](uml/04_paquete_niveles.png)

![Paquete vista](uml/05_paquete_vista.png)

![Paquete principal, util y lanzador](uml/06_paquete_principal_util_lanzador.png)

### 3.3 Decisiones de diseño del dominio
- **Una raíz común para todo lo que se mueve y choca.** `Entidad` agrupa posición, velocidad, sprite, hitbox y dibujo, e implementa `Colisionable`. Gracias a eso la nave revisa golpes contra cualquier cosa que pueda dañarla (enemigos, balas o rayos) con un solo método que recibe un `Colisionable`.
- **El rayo no es una entidad.** `Rayo` no tiene sprite ni velocidad, pero sí puede dañar a la nave. Por eso implementa `Colisionable` sin heredar de `Entidad`: hereda solo el contrato que comparte.
- **Datos fijos en enumeraciones.** `Personaje`, `Dificultad` y `TipoJefe` son enumeraciones: cada valor guarda sus datos y sabe crear lo suyo (el arma, la habilidad o los ataques del jefe). El resto del juego no necesita `switch` por tipo.
- **El modelo no conoce a la vista.** Las clases del modelo se comunican con la pantalla mediante `ContextoCombate` (un buzón de lo que dejan los enemigos) y `EfectosCombate` (una interfaz que implementa la pantalla).
- **Servicios compartidos separados por responsabilidad.** `GestorRecursos` solo carga y entrega recursos; `GestorRecord` solo maneja el récord; `Opciones` solo guarda la configuración del jugador.

---

## 4. Clases abstractas e interfaces (GM-4)

![Clases abstractas e interfaces](uml/10_gm4_clases_abstractas_e_interfaces.png)

### 4.1 Clases abstractas

| Clase abstracta | Por qué es abstracta | Qué define | Hijas |
|---|---|---|---|
| `Entidad` | No existe una "entidad genérica" en el juego: siempre es una nave, una bala, un enemigo o un power-up | Estado común (sprite, velocidad, factor de hitbox), dibujo, hitbox y el método abstracto `actualizar()` | `Nave4`, `Bullet`, `BalaEnemiga`, `Enemigo`, `PowerUp` |
| `Enemigo` | Todo enemigo es de un tipo concreto con comportamiento propio | Vida, puntos, entrada desde arriba, rebote, choques entre enemigos, el método abstracto `getNombre()` y los métodos gancho `atacar`, `alMorir`, `alAparecer`, `alSerDerrotado`, `cuentaParaGrupo`, `dibujarInterfaz`, `seDestruyeAlChocar`, `sueltaPowerUpSeguro`, `empujable` | `EnemigoBot`, `EnemigoLag`, `EnemigoCheater`, `EnemigoSpammer`, `EnemigoSniper`, `EnemigoJefe` |
| `PowerUp` | Cada power-up tiene un efecto distinto | Caída y salida de pantalla, y los métodos abstractos `aplicar(nave)` y `getMensaje()` | `Pizza`, `SillaGamer`, `RGB`, `LootBox` |
| `PantallaBase` | Es el esqueleto común de las pantallas; no tiene contenido propio | El método plantilla `render`, los pasos abstractos `actualizar` y `dibujar` y los ganchos `revisarTransicion` y `getTemblor` | Las 7 pantallas del juego |

Usamos **clase abstracta** cuando las hijas comparten estado y código, no solo un contrato: por ejemplo, todos los enemigos tienen vida y rebotan igual.

### 4.2 Interfaces

| Interfaz | Contrato | Implementaciones | Quién la usa |
|---|---|---|---|
| `Colisionable` | `getArea()` y `chocaCon(otro)` | `Entidad` (y con ella todas sus hijas) y `Rayo` | `Nave4.revisarGolpe` recibe cualquier `Colisionable` |
| `EstrategiaDisparo` | `disparar(x, y)`, `getCooldown()` y `getNombre()` | `Pistola`, `Escopeta`, `Railgun`, `BFG` | `Nave4` (Strategy) |
| `Habilidad` | `activar(nave, contexto)`, `getNombre()` y `getDescripcion()` | `Overdrive`, `TiroCertero`, `Burbuja` | `Nave4` |
| `AtaqueJefe` | `actualizar(jefe, contexto)` | 8 ataques (`AtaqueApuntado`, `AtaqueAnillo`, `AtaqueEspiral`, `AtaqueLluvia`, `AtaqueLaser`, `AtaqueSalto`, `AtaqueEmbestida`, `AtaqueInvocar`) | `EnemigoJefe` |
| `NivelBuilder` | Los pasos para armar una oleada y `getResult()` | `NivelPartidaBuilder` | `DirectorNiveles` (Builder) |
| `EfectosCombate` | Efectos que un enemigo puede pedir: anunciar, temblar, destellar, congelar, cambiar música, explotar, limpiar ataques y curar a la nave | `PantallaJuego` | `Enemigo` y `EnemigoJefe` |
| `FabricaPowerUp` | `crear(x, y)` | Referencias a los constructores de los 4 power-ups | `GeneradorPowerUps` |

Usamos **interfaz** cuando lo que se comparte es solo un comportamiento, sin estado común. Así una misma clase puede cumplir varios contratos: `PantallaJuego`, por ejemplo, hereda de `PantallaBase` e implementa `EfectosCombate`.

### 4.3 Polimorfismo en el juego
- `PantallaJuego` guarda una lista de `Enemigo` y en cada fotograma llama a `actualizar`, `atacar` y `dibujar` sobre todos. Cuando un enemigo entra le avisa con `alAparecer(this)`, y cuando muere, con `alSerDerrotado(this)`. **Nunca pregunta de qué tipo es cada enemigo**: el jefe se anuncia, cambia la música y dibuja su barra de vida porque sobrescribe esos métodos, no porque la pantalla lo detecte.
- La nave dispara con su `EstrategiaDisparo` y usa su `Habilidad` sin saber cuál tiene.
- Los power-ups se recogen con `aplicar(nave)`, y cada uno produce su efecto.
- Las balas, los enemigos y los rayos dañan a la nave a través del mismo método, que recibe un `Colisionable`.

### 4.4 Sobrecarga y sobrescritura
- **Sobrescritura:** cada enemigo sobrescribe `actualizar`, `atacar` y otros ganchos; cada pantalla, los pasos del método plantilla; cada power-up, `aplicar`.
- **Sobrecarga:** `GestorRecursos.reproducirSonido` existe con solo el archivo y con volumen y tono; `Bullet` tiene dos constructores (bala normal y bala que atraviesa).

---

## 5. Encapsulamiento y principios de diseño (GM-5)

### 5.1 Encapsulamiento
- **Atributos privados en todas las clases.** No hay atributos públicos ni de paquete con estado modificable; solo constantes `static final`. Las pantallas acceden al juego y al viewport mediante los métodos protegidos `getGame()` y `getViewport()` de `PantallaBase`.
- **Ninguna clase entrega sus colecciones internas.** `ContextoCombate` no tiene getters de listas: recibe objetos con métodos como `disparar`, `agregarRayo` y `limpiarBalasEnemigas`, y la pantalla los saca con métodos `recoger...`, que devuelven una copia y vacían el buzón. `Nivel.getEnemigos()` devuelve una lista de solo lectura.
- **Copias defensivas.** `Entidad.getArea()` siempre devuelve un `Rectangle` nuevo, nunca el interno del sprite, y `EnemigoJefe.getUmbrales()` devuelve una copia del arreglo de fases.
- **Setters solo cuando tienen reglas.** `Nave4` no tiene `setVidas`: se cura con `curar(cantidad)` (con tope) o se asegura un mínimo con `asegurarVidas`, que nunca baja las vidas. `Opciones` limita el volumen entre 0 y 1 y no acepta una dificultad nula. `SpaceNavigation` expone acciones (`activarModoDios`, `marcarGuiaVista`) en lugar de setters booleanos.
- **Visibilidad de paquete donde corresponde.** Los métodos que modifican un `Nivel` y su constructor solo son visibles dentro del paquete `niveles`, así que solo el Builder puede armar un nivel. Del mismo modo, `TipoJefe` usa métodos de paquete de `EnemigoJefe` para definir sus fases.
- **Métodos privados que esconden detalles.** Por ejemplo, la lectura del teclado y el estado herido de `Nave4` y la lectura y escritura del archivo en `GestorRecord`.

### 5.2 Principio de responsabilidad única (S)
Cada clase tiene una sola razón para cambiar:
- `GestorRecursos` carga recursos.
- `GestorRecord` guarda el récord.
- `Opciones` guarda la configuración.
- `Hud`, `BarraJefe` y `Locutor` dibujan su parte de la interfaz.
- `GeneradorPowerUps` decide qué power-up cae.
- `NivelPartidaBuilder` crea los enemigos de una oleada.

### 5.3 Principio Abierto/Cerrado (O)
El código está abierto a extensión y cerrado a modificación:
- **Enemigos.** Uno nuevo es una subclase de `Enemigo` que sobrescribe sus ganchos. `PantallaJuego` no cambia, porque no consulta tipos con `instanceof` ni hace conversiones de tipo.
- **Armas, habilidades y ataques de jefe.** Uno nuevo es una clase más que implementa su interfaz.
- **Power-ups.** Uno nuevo se registra con una línea en `GeneradorPowerUps`.
- **Personajes y jefes.** Uno nuevo es un valor más en `Personaje` o en `TipoJefe`, que ya incluye su arma, su habilidad o sus ataques. Lo mismo pasa con los enemigos de apoyo del jefe, en `Apoyo`.

### 5.4 Principio de sustitución de Liskov (L)
Cualquier hija puede reemplazar a su padre sin que el código que lo usa cambie de comportamiento:
- Un `EnemigoJefe` se usa en la misma lista y con los mismos métodos que un `EnemigoBot`. Sus diferencias (no se destruye al chocar, no se empuja, tiene escudo) están en métodos gancho que la clase padre declara con un comportamiento por defecto válido.
- Los métodos gancho de `Enemigo` son opcionales y tienen implementación por defecto, así que ninguna hija queda obligada a dejar métodos vacíos o a lanzar excepciones.

### 5.5 Principio de segregación de interfaces (I)
Las interfaces son pequeñas y específicas:
- `Colisionable` tiene 2 métodos.
- `AtaqueJefe` y `FabricaPowerUp` tienen 1 cada una.
- `Habilidad` y `EstrategiaDisparo` tienen 3 cada una.

Ninguna clase implementa métodos que no usa.

### 5.6 Principio de inversión de dependencias (D)
Las clases dependen de abstracciones, no de clases concretas:
- **La nave** conoce `EstrategiaDisparo` y `Habilidad`.
- **El jefe** conoce `AtaqueJefe`.
- **El Director** recibe un `NivelBuilder`.
- **Los enemigos** piden efectos a `EfectosCombate`, sin conocer `PantallaJuego`.
- **La pantalla** solo trabaja con `Enemigo` y `PowerUp`: no importa ninguna clase concreta de enemigo ni de power-up.
- **Las colecciones** se declaran con `List` y `Map`, no con `ArrayList` ni `HashMap`.

### 5.7 Otros principios
- **Ley de Deméter:** la pantalla le pide a la nave que se cure (`curar`), no le cambia el atributo de vidas desde afuera. Los enemigos le piden al contexto que agregue una bala, no manipulan la lista de balas de la pantalla.
- **Experto en información (GRASP):** cada enemigo decide cómo se mueve y ataca, cada arma cómo dispara y cada nivel cuántos enemigos trae.

---

---

*(Continúa en las secciones GM-6 a GM-10.)*
