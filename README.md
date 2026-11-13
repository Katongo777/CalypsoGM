# Calypso: Skill Issue Edition

Juego de naves estilo arcade (tipo *Asteroids* / *Space Invaders*) hecho en **Java con LibGDX**, con humor de comunidad gamer. Es el **Proyecto Juego** del curso *Programación Avanzada* (ICI2241).

Partimos del juego base **SpaceNav2024** que nos entregó el profe y lo rediseñamos: corregimos sus problemas, aplicamos encapsulamiento y principios de diseño, y le agregamos los 4 patrones de diseño pedidos (Singleton, Strategy, Template Method y Builder) junto con nuevas funcionalidades.

## ¿De qué trata?

El servidor *Calypso* está infestado de **bots**, **lags** y **cheaters**. Elige a tu personaje, sobrevive a las oleadas, recoge power-ups (pizza, silla gamer, teclado RGB, cajas de botín) y supera tu récord. La campaña tiene **20 oleadas** con enemigos que disparan, oleadas hardcore y 4 jefes con fases y escudo (**El Toxic**, **El Hacker**, **El Sweaty** y el jefe final **El Admin**). Cada personaje tiene una **habilidad especial** (tecla X). Si derrotas al Admin, ganas. Y si sabes el truco de los gamers de la vieja escuela, hay un código secreto...

## Controles

| Tecla | Acción |
|---|---|
| **Flechas** | Mover la nave |
| **ESPACIO** (mantener) | Disparar |
| **P** | Pausa |
| **ENTER** | Aceptar en los menús; saltar el cartel de oleada |
| **Mouse** | Elegir botones y personajes |
| **X** | Habilidad especial (con la barra llena) |
| **P** o **ESC** | Pausa |
| **H** | Abrir la guía "Cómo sobrevivir" (desde el menú) |
| **ESC** | Volver al menú (desde la guía) |
| **← →** | Elegir personaje |
| **F** | "Presentar tus respetos" en el Game Over (volver a jugar) |
| **Código Konami** en el menú | Modo dios (30 vidas y BFG 9000) |

## Cómo ejecutarlo

**Requisitos:** JDK 11. El proyecto compila y se ejecuta con Java 11.

Desde una consola, en la carpeta del proyecto:

```bash
# Windows
gradlew.bat lwjgl3:run

# Linux / macOS
./gradlew lwjgl3:run
```

Para generar un `.jar` ejecutable: `gradlew.bat lwjgl3:jar` (queda en `lwjgl3/build/libs/`).

Desde Eclipse o IntelliJ: *Importar proyecto Gradle* y ejecutar la clase `puppy.code.lwjgl3.Lwjgl3Launcher`.

> El récord y las opciones se guardan en `record.txt` y `opciones.txt`, que se crean solos en la carpeta `assets`. No se suben a git.
>
> Para probar un jefe sin jugar todo: ejecutar con la opción de la JVM `-Dcalypso.oleada=20` (ver `docs/informe/C5_ejecucion_y_github.md`).

## Estructura del proyecto

```
core/src/main/java/puppy/code/
├── SpaceNavigation.java        Clase principal del juego (hereda de Game de LibGDX)
├── Constantes.java             Tamaño fijo del mundo del juego
├── modelo/                     Entidad, Nave4, Bullet, BalaEnemiga, Rayo, Personaje, Dificultad,
│   │                           Colisionable, ContextoCombate y EfectosCombate (interfaces con la vista)
│   ├── enemigos/               Enemigo (abstracta), EnemigoBot, EnemigoLag, EnemigoCheater, EnemigoSpammer, EnemigoSniper, EnemigoJefe, TipoJefe
│   ├── jefes/                  AtaqueJefe (interfaz) y los 8 ataques de los jefes
│   ├── armas/                  EstrategiaDisparo (interfaz), Pistola, Escopeta, Railgun, BFG
│   ├── habilidades/            Habilidad (interfaz), Overdrive, TiroCertero, Burbuja
│   ├── powerups/               PowerUp (abstracta), Pizza, SillaGamer, RGB, LootBox, FabricaPowerUp, GeneradorPowerUps
│   └── niveles/                Nivel, NivelBuilder, NivelPartidaBuilder, DirectorNiveles
├── vista/                      PantallaBase y las 7 pantallas, MenuBotones, Hud, BarraJefe, Particulas, Locutor, FondoEstrellado
└── util/                       GestorRecursos (Singleton de recursos y audio), GestorRecord, Opciones, CodigoKonami
```

## Patrones de diseño aplicados

| Patrón | Clases principales |
|---|---|
| **Singleton** | `GestorRecursos` |
| **Strategy** | Armas (`EstrategiaDisparo`), habilidades (`Habilidad`) y ataques de jefes (`AtaqueJefe`) |
| **Template Method** | `PantallaBase` y sus 7 pantallas hijas |
| **Builder** | `DirectorNiveles`, `NivelBuilder`, `NivelPartidaBuilder` y `Nivel` |

El reporte completo está en [`docs/INFORME.md`](docs/INFORME.md). Los diagramas UML, generados desde el código, están en [`docs/uml/`](docs/uml/): el diagrama completo, uno por paquete y uno por patrón, en `.png`, `.svg` y `.puml`. La hoja de datos para ajustar jefes y dificultad es [`docs/informe/C6_diseno_jefes_y_dificultad.md`](docs/informe/C6_diseno_jefes_y_dificultad.md).

## Integrantes

- Renato Agustín Bermúdez Salfate
- Iván Octavio García Briones
- Nicolás Daniel Torres González
