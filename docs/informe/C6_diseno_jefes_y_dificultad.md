# Diseño de jefes, enemigos y dificultad (apoyo para ajustar el balance)

Este documento no responde a un requerimiento GM: es la "hoja de datos" del juego para que cualquiera del grupo pueda entender y **ajustar** los números sin leer todo el código. Dice qué archivo cambiar para cada cosa.

## 1. Los jefes

Cada jefe es un `EnemigoJefe` con un `TipoJefe` (nombre, imagen, vida, puntos, velocidad) y una lista de ataques por fase. Cuando su vida baja de un umbral pasa de fase y se pone un **escudo**: es invulnerable, lanza un ataque especial e invoca apoyo. El escudo se rompe al derrotar a todo el apoyo (o a los 15 segundos) y el jefe sigue con ataques más fuertes y más rápido.

| Jefe | Oleada | Vida | Umbrales de fase | Idea |
|---|---|---|---|---|
| **El Toxic** | 5 | 28 | 50 % | Escupe veneno apuntado e invoca un Lag de vez en cuando. Escudo "MUTE": llueve veneno (poco) y llegan 2 bots |
| **El Hacker** | 10 | 36 | 50 % | Se teletransporta soltando anillos de balas. Escudo "firewall": 2 rayos verticales y 1 Spammer |
| **El Sweaty** | 15 | 48 | 66 % y 33 % | Espirales de balas y embestidas en picada. Dos escudos con Cheaters de apoyo |
| **El Admin** | 20 | 55 | 66 % y 33 % | Rayos de "ban" (verticales y horizontales), anillos, balas apuntadas y apoyo variado. Tres fases |

Los ataques de cada fase están en `EnemigoJefe.armarAtaques()` (`modelo/enemigos/EnemigoJefe.java`). Cada ataque recibe sus números en el constructor:

| Ataque | Parámetros | Qué hace |
|---|---|---|
| `AtaqueApuntado(intervalo, cantidad, rapidez, color)` | cada cuántos fotogramas, cuántas balas en abanico | Dispara hacia la nave |
| `AtaqueAnillo(intervalo, cantidad, rapidez, color)` | | Anillo de balas desde el centro del jefe |
| `AtaqueEspiral(cadaFrames, brazos, rapidez, color)` | | Remolino continuo de balas |
| `AtaqueLluvia(intervalo, rapidez, color)` | | Balas que caen desde arriba |
| `AtaqueLaser(intervalo, cantidad, horizontal)` | | Rayos con aviso de 75 fotogramas (×1.25 en Normal y ×1.5 en Fácil); el primero cae sobre la nave |
| `AtaqueInvocar(primeraEn, intervalo, tipo, cantidad)` | intervalo 0 = una sola vez | Invoca Bots, Lags, Cheaters, Spammers o Snipers |
| `AtaqueSalto(intervalo, balas, color)` | | Teletransporte + anillo |
| `AtaqueEmbestida(intervalo)` | | Aviso (tiembla en rojo) y picada hacia abajo |

*Nota:* los intervalos se miden en fotogramas (60 = 1 segundo) y se ajustan con la dificultad (`ContextoCombate.intervalo()`). La dificultad también cambia la **velocidad de las balas** (`ContextoCombate.disparar()`) y el **tiempo de aviso de los rayos** (`ContextoCombate.avisoRayo()`).

**Ayudas durante un jefe:** el escudo dura como máximo 10 segundos; al empezar y al romperse el escudo cae un power-up (una pizza si te quedan 2 vidas o menos); antes de cada jefe te dan hasta 3 vidas; y al derrotarlo ganas +2 vidas.

## 2. Los enemigos normales

| Enemigo | Aparece desde | Vida | Puntos | Ataque |
|---|---|---|---|---|
| Bot | 1 | 1 | 10 | Desde la oleada 2: una bala lenta hacia abajo cada 2,5 a 5,5 s |
| Lag | 2 | 1 | 15 | Ninguno; se teletransporta y tiembla antes |
| Cheater | 3 | 3 | 50 | 3 balas apuntadas en abanico; al morir suelta 2 Lags |
| Spammer | 6 | 1 | 20 | Ráfagas de 3 balas hacia abajo |
| Sniper | 11 | 2 | 30 | Línea roja de 70 fotogramas y bala rápida a ese punto |

Los enemigos normales se mueven solo en la parte de arriba (`Constantes.ZONA_ENEMIGOS_MIN_Y = 220`) y entran por arriba en **grupos de 2 a 4** (`PantallaJuego.actualizarEntradaDeEnemigos()`); el siguiente grupo entra cuando quedan 2 enemigos o pasan 7 segundos.

## 3. La curva de dificultad (`DirectorNiveles`)

| Dato | Fórmula | Tope |
|---|---|---|
| Velocidad | `0.9 + 0.025 × oleada` (hardcore: +0.1) | 1.35 |
| Bots (normal) | `3 + oleada / 3` | 7 |
| Lags (normal, desde la 2) | `1 + oleada / 5` | 4 |
| Cheaters (normal, desde la 3) | `1 + (oleada - 3) / 5` | 3 |
| Spammers (normal, desde la 6) | `1 + (oleada - 6) / 4` | 3 |
| Snipers (normal, desde la 11) | `1 + (oleada - 11) / 4` | 3 |
| Probabilidad de power-up | 0.12 normal, 0.18 hardcore, 0.10 jefe | |

Oleadas hardcore: 4, 8, 12, 17 y 19. Jefes: 5, 10, 15 y 20. Además, cada dificultad de las opciones cambia la velocidad de los enemigos, cuánto disparan y cuántas vidas tienes (`modelo/Dificultad.java`):

| Dificultad | Velocidad enemigos | Tiempo entre disparos | Vidas |
|---|---|---|---|
| Fácil | ×0.75 | ×2.0 (disparan la mitad) | +3 |
| Normal | ×0.9 | ×1.4 | +1 |
| Difícil | ×1.0 | ×1.0 | +0 |

Además, en Fácil las balas enemigas van al 70 % de su velocidad (85 % en Normal, 100 % en Difícil) y los rayos avisan más tiempo.

## 4. Otros números que se pueden ajustar

| Qué | Dónde | Valor actual |
|---|---|---|
| Invulnerabilidad después de un golpe | `Nave4.tiempoHeridoMax` | 180 fotogramas (3 s) |
| Daño mientras es invulnerable | `Nave4.disparar()` | la mitad |
| Hitbox de la nave | `Nave4` (`setFactorHitbox`) | 45 % |
| Vidas iniciales | `Personaje` | Tryhard 4, Camper 4, Main Support 5 (más las de la dificultad) |
| Cadencia de la Railgun | `Railgun.getCooldown()` | 32 fotogramas |
| Escudo del jefe | `EnemigoJefe.DURACION_MAXIMA_ESCUDO` | 600 fotogramas (10 s) |
| Ayuda con 1 vida | `PantallaJuego.soltarPowerUp()` | probabilidad de power-up ×3 y siempre es una pizza |
| Energía por baja | `PantallaJuego` (`getPuntos() * 0.8`) | un Bot da 8 %, un Cheater 40 % |
| Energía perdida al ser golpeado | `Nave4.recibirDano()` | 25 % |
| Enfriamiento entre power-ups | `PantallaJuego.ENFRIAMIENTO_POWERUP` | 600 fotogramas (10 s) |
| Máximo de enemigos a la vez | `PantallaJuego.MAX_ENEMIGOS` | 14 |
| Duración del cartel de oleada | `PantallaJuego.DURACION_INTRO` | 330 fotogramas (se salta con ENTER) |

## 5. Habilidades especiales (tecla X)

La barra se llena eliminando enemigos y baja un poco al recibir daño.

| Personaje | Habilidad | Efecto |
|---|---|---|
| Tryhard | Overdrive | 6 s de escopeta al doble de velocidad |
| Camper | Tiro certero | Rayo gigante (12 de daño) que atraviesa todo |
| Main Support | Burbuja | +1 vida, escudo de 5 s y borra las balas enemigas |
