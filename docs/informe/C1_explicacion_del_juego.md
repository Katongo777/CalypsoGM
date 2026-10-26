# Explicación del juego desde la perspectiva del jugador (GM-1)

> Requerimiento GM-1: *Explicación del juego desde la perspectiva del jugador, incluyendo objetivos, narrativa, elementos y controles, utilizando maquetas o pantallazos.*

## Narrativa
El servidor **Calypso** está infestado. Hay **bots** que solo saben avanzar, **lags** con el ping en 999 que se teletransportan, **cheaters** con lentes oscuros, **spammers** que inundan el chat de balas y **snipers** que te apuntan desde lejos. Al final de cada bloque de oleadas te espera un jefe: **El Toxic**, **El Hacker**, **El Sweaty** y, al final de todo, **El Admin**, que tiene el poder de banear. Tú eres un jugador que acaba de entrar a la partida y tiene que sobrevivir y bajar al Admin.

El tono del juego es de humor de comunidad gamer: el locutor grita *FIRST BLOOD* o *KILLING SPREE*, y si te hacen daño te dice *"Skill issue"* o *"Eso fue lag, lo juro"*.

## Objetivo
Pasar las **20 oleadas** de la campaña eliminando a todos los enemigos de cada una, derrotar al jefe final (El Admin) y juntar puntos para **superar tu récord** (que queda guardado). Si pierdes todas tus vidas, aparece el Game Over.

## Cómo se juega

1. El **menú** es de botones (se maneja con mouse o con flechas y ENTER): *Jugar, Cómo sobrevivir, Opciones y Salir*. La primera vez que juegas se abre la **guía "Cómo sobrevivir"**.
2. Eliges a tu **personaje** (con flechas o con el mouse). Cada uno tiene su arma y su **habilidad especial**.
3. Cada oleada empieza con un cartel (*"OLEADA 2/20"*, su nombre y un aviso si hay un enemigo nuevo o un jefe). Se puede saltar con ENTER. Tu nave aparece parpadeando desde abajo y los enemigos entran en grupos desde arriba.
4. Mueves la nave con las **flechas** y disparas manteniendo **ESPACIO**. Los enemigos también disparan: esquiva sus balas y las líneas rojas que avisan los rayos.
5. Eliminar enemigos llena la **barra de habilidad**; cuando está llena, aprieta **X**.
6. Si te golpean pierdes una vida (el corazón se rompe), la pantalla tiembla y quedas invulnerable 3 segundos, pero haces la mitad de daño. Cuando se acaban las vidas aparece el **Game Over**, donde presionas **F** para "presentar tus respetos" y volver a jugar.
7. Con **ESC** o **P** abres el menú de pausa (Continuar, Opciones, Salir al menú).
8. Si derrotas al jefe de la oleada 20 aparece la pantalla de **VICTORIA**.

### Controles

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

## Elementos del juego

### Personajes
| Personaje | Arma inicial | Vidas | Velocidad | Frase |
|---|---|---|---|---|
| **Tryhard** | Pistola | 3 | 7 | "Suda hasta en el tutorial." |
| **Camper** | Railgun | 2 | 5 | "Una bala, una baja." |
| **Main Support** | Escopeta | 4 | 6 | "Cura con amor... y con perdigones." |

### Armas
| Arma | Cómo dispara |
|---|---|
| **Pistola** | Una bala recta, cadencia normal |
| **Escopeta** | Tres perdigones en abanico, cadencia lenta |
| **Railgun** | Un rayo muy rápido que atraviesa a todos los enemigos (2 de daño) |
| **BFG 9000** | Una esfera enorme y lenta que atraviesa todo (5 de daño) |

### Habilidades especiales (tecla X)
| Personaje | Habilidad | Efecto |
|---|---|---|
| **Tryhard** | Overdrive | 6 segundos de escopeta al doble de velocidad |
| **Camper** | Tiro certero | Un rayo gigante que atraviesa todo |
| **Main Support** | Burbuja | Cura 1 vida, da escudo 5 s y borra las balas enemigas |

### Enemigos
| Enemigo | Descripción | Vida | Puntos |
|---|---|---|---|
| **Bot** | Robot que avanza recto y rebota | 1 | 10 |
| **Lag** | Burbuja roja con el ping en 999. Se teletransporta (*lag spike*), pero tiembla y parpadea justo antes de saltar | 1 | 15 |
| **Cheater** | Grande y resistente, con lentes oscuros; al morir se parte en 2 Lags | 3 | 50 |
| **Spammer** (desde la 6) | Globo de chat que flota de lado a lado y dispara ráfagas de 3 balas | 1 | 20 |
| **Sniper** (desde la 11) | Se detiene, apunta con una línea roja y dispara una bala rápida | 2 | 30 |
| **Jefes** (5, 10, 15 y 20) | El Toxic, El Hacker, El Sweaty y El Admin. Barra de vida arriba, ataques propios y un escudo en cada cambio de fase | 45 / 70 / 100 / 150 | 500 / 1000 / 1500 / 3000 |

Los enemigos normales se mueven solo en la parte de arriba de la pantalla; abajo queda espacio para maniobrar. Los jefes no desaparecen al chocar con tu nave y siempre dejan un power-up al morir. El detalle de cada jefe está en `C6_diseno_jefes_y_dificultad.md`.

### La campaña
| Oleadas | Qué pasa |
|---|---|
| 1 a 3 | Bots, luego Lags y luego Cheaters |
| 4 | **Hardcore** |
| 5 | **Jefe: El Toxic** |
| 6 a 9 | Llegan los Spammers (la 8 es hardcore) |
| 10 | **Jefe: El Hacker** |
| 11 a 14 | Llegan los Snipers (la 12 es hardcore) |
| 15 | **Jefe: El Sweaty** |
| 16 a 19 | Todo junto (la 17 y la 19 son hardcore) |
| 20 | **Jefe final: El Admin** |

### Power-ups (los sueltan los enemigos al morir)
| Power-up | Efecto |
|---|---|
| **Pizza** | +1 vida |
| **Silla gamer** | Escudo durante 10 segundos |
| **Teclado RGB** | Disparo al doble de velocidad durante 10 segundos |
| **Caja de botín** | Un arma al azar durante 15 segundos (Escopeta, Railgun o, con suerte, el BFG 9000) |

### Otros detalles
- **Locutor:** anuncia *FIRST BLOOD*, *COMBO x3*, *KILLING SPREE*, *RAMPAGE* y *GODLIKE* según tu racha de eliminaciones.
- **Fuente pixel-art propia** para todos los textos.
- **Récord y opciones:** el récord y las opciones (volumen y dificultad) se guardan en archivos.
- **Música:** una canción para el menú, otra para el juego y otra más tensa para los jefes, todas generadas por código (originales).
- **Efectos de impacto:** temblor de pantalla, partículas, destello rojo al ser golpeado y corazón que se rompe.
- **Modo dios (easter egg):** el código Konami en el menú da 30 vidas y el BFG 9000.

## Pantallazos

### Menú principal
![Menú](../capturas/menu.png)

![Menú con modo dios](../capturas/menu_modo_dios.png)

### Guía "Cómo sobrevivir" (2 páginas)
![Guía página 1](../capturas/guia_1.png)

![Guía página 2](../capturas/guia_2.png)

### Selección de personaje
![Selección de personaje](../capturas/seleccion.png)

### Cartel de inicio de oleada
![Cartel de oleada](../capturas/intro_oleada.png)

### Jugando (barra de habilidad abajo a la izquierda, enemigos que disparan)
![Oleada 1](../capturas/oleada1.png)

![Balas enemigas](../capturas/oleada2_balas.png)

![Power-ups](../capturas/powerups.png)

![Combo](../capturas/combo.png)

### Armas y habilidad
![Railgun](../capturas/railgun_camper.png)

![Habilidad del Camper](../capturas/habilidad_camper.png)

![BFG](../capturas/bfg.png)

### Oleada hardcore
![Hardcore](../capturas/hardcore.png)

### Jefes
![El Toxic](../capturas/jefe_toxic.png)

![El Toxic con escudo](../capturas/jefe_toxic_escudo.png)

![El Hacker](../capturas/jefe_hacker.png)

![El Hacker: rayos y escudo](../capturas/jefe_hacker_rayos.png)

![El Sweaty](../capturas/jefe_sweaty.png)

![El Admin](../capturas/jefe_admin.png)

![El Admin: rayos de ban](../capturas/jefe_admin_rayos.png)

### Pausa y opciones
![Pausa](../capturas/pausa.png)

![Opciones](../capturas/opciones.png)

### Game Over y Victoria
![Game Over](../capturas/game_over.png)

![Victoria](../capturas/victoria.png)
