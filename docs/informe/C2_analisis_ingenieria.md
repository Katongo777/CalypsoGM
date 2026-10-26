# Análisis desde la ingeniería de software: juego base y modificaciones (GM-2)

> Requerimiento GM-2: *Análisis del juego desde la perspectiva de ingeniería de software, especificando el juego base y las modificaciones propuestas.*

## 1. El juego base (SpaceNav2024)

El juego base es un proyecto LibGDX (con Gradle) que nos entregó el profe. Es un juego tipo *Asteroids*: una nave se mueve por la pantalla y dispara hacia arriba; hay asteroides que rebotan por todos lados; cada asteroide destruido da 10 puntos; si chocas con uno pierdes una de tus 3 vidas; al limpiar la pantalla empieza otra ronda con más asteroides y más velocidad; si pierdes todas las vidas aparece "Game Over" y con cualquier tecla se reinicia.

Tenía **7 clases** en un solo paquete:

| Clase | Rol |
|---|---|
| `SpaceNavigation` | Clase principal (hereda de `Game` de LibGDX); guarda el batch, la fuente y el puntaje máximo |
| `PantallaMenu`, `PantallaGameOver` | Pantallas de inicio y de fin |
| `PantallaJuego` | La pantalla donde se juega (la clase más grande) |
| `Nave4` | La nave del jugador |
| `Ball2` | Los asteroides |
| `Bullet` | Las balas |

## 2. Problemas que encontramos (análisis)

Al revisar el código de acuerdo a lo visto en el curso, detectamos estos problemas:

| # | Problema | Dónde estaba | Consecuencia |
|---|---|---|---|
| 1 | **Fuga de memoria:** se cargaba una imagen nueva por cada asteroide y por cada ronda, y `dispose()` no liberaba todo | `PantallaJuego` | El juego gasta más memoria mientras más se juega |
| 2 | **Dependencia del modelo hacia la vista:** la nave recibía la pantalla por parámetro | `Nave4` | Cambiar la pantalla rompe la nave |
| 3 | **Sin jerarquía de clases:** nave, balas y asteroides repetían la lógica de posición, dibujo y colisión | `Nave4`, `Ball2`, `Bullet` | Código duplicado y difícil de extender |
| 4 | **Constructor de 7 parámetros** para crear cada ronda | `PantallaJuego` | Difícil de leer, de mantener y de ampliar |
| 5 | **Código repetido en las pantallas** y 6 métodos vacíos en cada una | `PantallaMenu`, `PantallaGameOver`, `PantallaJuego` | Duplicación; la cámara de `PantallaJuego` se creaba pero nunca se usaba |
| 6 | **Dos listas idénticas** de asteroides (`balls1` y `balls2`) | `PantallaJuego` | Hay que mantener ambas sincronizadas; fuente de errores |
| 7 | **Error en `Ball2`:** el constructor corregía la posición inicial pero luego usaba los valores sin corregir | `Ball2` | Los asteroides podían nacer pegados al borde |
| 8 | **Una bala podía matar a dos enemigos** que estuvieran superpuestos | `PantallaJuego` | Comportamiento injusto |
| 9 | **El juego se "congelaba"** unos segundos cada vez que te golpeaban | `PantallaJuego` | Mala experiencia de juego |
| 10 | **Tamaño dependiente de la ventana** (`Gdx.graphics.getWidth()` en cada clase) y puntaje máximo que se perdía al cerrar | Varias | El juego se descuadra al cambiar el tamaño; no hay persistencia |

## 3. Modificaciones realizadas

### 3.1 Correcciones y mejoras de diseño

| Problema | Solución | Requerimiento |
|---|---|---|
| 1 Fuga de memoria | Patrón **Singleton** (`GestorRecursos`): una sola carga de cada recurso | GM-6 |
| 2 Modelo → vista | `Nave4.disparar()` devuelve las balas; el modelo ya no importa la vista | GM-5 |
| 3 Sin jerarquía | Clases abstractas `Entidad`, `Enemigo` y `PowerUp`; interfaz `Colisionable` | GM-4 |
| 4 Constructor de 7 parámetros | Patrón **Builder** (`Nivel`, `NivelBuilder`, `NivelPartidaBuilder`, `DirectorNiveles`) | GM-9 |
| 5 Código repetido en pantallas | Patrón **Template Method** (`PantallaBase`) + `FitViewport` con tamaño fijo (`Constantes`) | GM-8 |
| 6 Dos listas | Una sola lista `ArrayList<Enemigo>` | GM-5 |
| 7 Error de `Ball2` | Nueva jerarquía de enemigos; `ubicarDentroDePantalla()` corrige la posición de verdad | GM-4 |
| 8 Una bala, dos enemigos | `Bullet.revisarColision()` registra a qué enemigos ya golpeó | GM-5 |
| 9 Congelamiento | La nave queda **invulnerable y parpadeando** sin detener el juego | — |
| 10 Tamaño y récord | Clase `Constantes` y récord guardado en archivo con `try-catch` | GM-5 |

### 3.2 Nuevas funcionalidades (la parte "divertida")
- **Cambio de tono a humor gamer** (título, textos, locutor, frases del Game Over, easter egg).
- **Selección de personaje** (3 personajes, cada uno con nave, vidas, velocidad y arma distintas) → usa el patrón **Strategy** (GM-7).
- **4 armas intercambiables:** Pistola, Escopeta, Railgun y BFG 9000 (GM-7).
- **3 tipos de enemigos** con comportamientos distintos (Bot, Lag, Cheater), un enemigo que se divide al morir, **Spammer y Sniper** que disparan y **4 jefes** con fases y escudo (GM-4).
- **4 power-ups:** Pizza, Silla gamer, Teclado RGB y Caja de botín (GM-4).
- **Campaña de 20 oleadas** con dificultad progresiva, oleadas hardcore, cuatro jefes y pantalla de victoria (GM-9).
- **Locutor gamer, HUD con corazones, fondo estrellado y pausa.**
- **Arte pixel-art original** para todos los personajes, enemigos, jefes, armas y power-ups, y una **fuente pixel-art propia**.
- **Intro de cada oleada** (cartel, nave que aparece parpadeando, enemigos que entran de a uno desde arriba) y **guía "Cómo sobrevivir"**.
- **Código Konami** (modo dios).

### 3.3 Mejoras después de probar el juego

Cuando jugamos la versión anterior notamos varios problemas y los corregimos:

| Problema que vimos | Solución |
|---|---|
| La partida empezaba de golpe, con enemigos apareciendo al azar y velocidades que cambiaban bruscamente | Fases de la oleada (`INTRO`, `JUGANDO`, `COMPLETADA`), enemigos que entran de a uno desde arriba y velocidades más suaves |
| No se entendía qué enemigos había | Guía "Cómo sobrevivir" y avisos de enemigo nuevo en las primeras oleadas |
| No se sabía cuántos niveles había ni qué era un jefe | Campaña fija (hoy de 20 oleadas), HUD con "Oleada n/20" y "Enemigos: N", y jefes con barra de vida |
| **Bug:** en la oleada 7 se mataba a todos y no pasaba nada | Un enemigo quedaba atrapado fuera de la pantalla (invisible e imposible de matar), porque al rebotar solo se invertía su velocidad. Ahora `moverConRebote()` lo devuelve dentro y le da velocidad hacia adentro |
| El Lag parecía un círculo de "cargando" y no un enemigo | Nuevo sprite: burbuja roja con el ping en 999, que tiembla antes de teletransportarse |
| La letra se veía fea | Fuente pixel-art propia, cargada por el Singleton |
| Faltaba un final | Pantalla de victoria al derrotar al jefe final |

### 3.4 Segunda ronda de mejoras (hacia un juego más profesional)

Después de jugar la versión anterior hicimos una lista de problemas y de cosas que faltaban:

| Problema o falta | Solución | Commit |
|---|---|---|
| Los enemigos se atascaban al chocar entre sí (cada uno invertía su velocidad pero seguían superpuestos) | Primero se **separan** y después rebotan; el jefe es "pesado" (no se empuja) | 22 |
| Los choques se sentían injustos | Hitbox más chica que el dibujo (`factorHitbox`) | 22 |
| La invulnerabilidad después de un golpe duraba muy poco | 3 segundos; mientras dura, las balas hacen la mitad de daño para que no sea una herramienta "chetada" | 22 |
| Aparecían todos los enemigos de una vez, hasta al lado de la nave | Entran en **grupos pequeños** desde arriba y se mueven en la zona de arriba; nunca cerca de la nave | 23 |
| Caían demasiados power-ups y los enemigos eran muy rápidos | Menos probabilidad, enfriamiento de 10 s entre power-ups y velocidades con tope | 23 |
| No se alcanzaba a leer el cartel de la oleada; menú solo con teclas; sin pausa ni opciones | Cartel más largo y saltable, menú con **mouse y teclado**, menú de pausa (ESC/P) y opciones guardadas (volumen y dificultad) | 24 |
| Los enemigos solo chocaban, no disparaban | `BalaEnemiga`, `ContextoCombate` y el gancho `atacar()` | 25 |
| El juego quedaba corto y con poca variedad | Spammer y Sniper nuevos y campaña de **20 oleadas** con una curva de dificultad definida | 26 |
| Una sola canción | Música de menú, de juego y de jefe, y efectos de sonido, con volumen en las opciones | 27 |
| Faltaba una mecánica propia de cada personaje | **Habilidad especial** con barra de energía (Strategy) | 28 |
| Los jefes eran fáciles y siempre hacían lo mismo | 4 jefes con **fases**, escudo, rayos con aviso, espirales, teletransporte y apoyo (Strategy) | 29 |
| Poca respuesta ante los golpes | Temblor de pantalla, partículas, destello, congelado de un instante y **corazón que se rompe** | 30 |

Un detalle técnico que descubrimos en esta etapa: `Sprite.draw()` usa el color del propio sprite y no el del `SpriteBatch`, por lo que el parpadeo y el tinte no funcionaban; ahora `Entidad` tiene `setTinte()`.

## 4. Decisiones de diseño: ¿por qué cada patrón en ese lugar?

| Patrón | Dónde lo pusimos | Por qué ahí |
|---|---|---|
| Singleton | Recursos y récord | Es lo único que de verdad debe existir una sola vez y que todos necesitan |
| Strategy | Armas de la nave | Una misma acción (disparar) con varias formas intercambiables, que además cambian durante la partida |
| Template Method | Pantallas | Todas siguen la misma secuencia de pasos (actualizar → dibujar → transición) |
| Builder | Oleadas (niveles) | Objeto con muchas partes opcionales y distintas variantes (normal, hardcore y de jefe) |

## 5. Plan de pruebas (qué verificamos)

| Qué se prueba | Resultado esperado |
|---|---|
| Menú → selección → juego | Las pantallas cambian en orden; ENTER avanza |
| Cada personaje | Cada uno parte con su nave, sus vidas y su arma |
| Armas | Pistola recta; Escopeta en abanico; Railgun atraviesa; BFG grande y lento |
| Enemigos | El Cheater necesita 3 impactos y suelta 2 Lags; el Lag tiembla y se teletransporta; cada enemigo entra desde arriba sin dañar mientras entra |
| Jefes | La barra de vida baja con cada impacto; invocan Lags; no desaparecen al chocar con la nave; dejan un power-up |
| Power-ups | Pizza suma vida; Silla da escudo con halo; RGB acelera; Caja cambia el arma por 15 s |
| Oleadas | Al limpiar la pantalla aparece "oleada completada" y empieza la siguiente; hay oleadas hardcore y jefes en la 5, 10, 15 y 20 |
| Campaña completa | Con el modo dios se jugaron las 20 oleadas seguidas hasta la pantalla de victoria, sin quedarse trabado en ninguna |
| Pausa | P detiene todo y lo reanuda |
| Guía y victoria | La guía aparece la primera vez; H la abre; al ganar aparece la victoria |
| Colisiones | Los enemigos que chocan se separan y no quedan pegados; la nave solo choca con el centro de su dibujo |
| Jefes (fases) | Al bajar de cada umbral se pone el escudo, el escudo se rompe al derrotar al apoyo y el jefe sigue con otros ataques |
| Pausa y opciones | ESC y P abren la pausa con botones; el volumen y la dificultad se guardan en `opciones.txt` |
| Habilidad | La barra se llena con las bajas, baja al ser golpeado y X la usa solo si está llena |
| Récord | Se guarda en `record.txt` y se conserva al cerrar y abrir el juego |
| Archivo de récord dañado | Si `record.txt` tiene texto inválido, el juego no se cae y empieza en 0 |
| Modo dios | El código Konami en el menú da 30 vidas y BFG |
| Cambio de tamaño de ventana | El juego mantiene su proporción |
| Compilación | `gradlew lwjgl3:compileJava` termina sin errores |
