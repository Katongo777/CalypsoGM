# Ejecución del proyecto y uso de GitHub (GM-10)

> Requerimiento GM-10: *Utilización de GitHub (realización de al menos 6 commits).*
> Además, la entrega pide: *instrucciones de instalación y ejecución del proyecto.*

## Repositorio
https://github.com/Katongo777/CalypsoGM

## Commits
El proyecto tiene muchos más de 6 commits. La historia empieza con la subida inicial del juego base y la primera refactorización (paquetes, encapsulamiento de las clases y la clase abstracta `Entidad`), y continúa con los 33 commits de esta etapa (Iván hizo los 15 primeros, la base del juego; el resto lo repartieron Nicolás y Renato/Kato. Los commits 31, 32 y 33, de documentación, no dependen del código), repartidos entre los tres integrantes. Cada commit deja el proyecto compilando.

| N° | Integrante | Qué se hizo |
|---|---|---|
| 01 | A | Singleton `GestorRecursos` |
| 02 | A | Encapsulamiento de `Entidad`; la nave ya no depende de la vista |
| 03 | A | Jerarquía de enemigos (`Enemigo` abstracta + `EnemigoBot`) |
| 04 | A | Template Method: `PantallaBase` y mundo de tamaño fijo |
| 05 | A | Strategy: `EstrategiaDisparo` y arma `Pistola` |
| 06 | A | Enemigos `Lag` y `Cheater` |
| 07 | A | Récord persistente con manejo de excepciones |
| 08 | A | Armas `Escopeta` y `Railgun` |
| 09 | A | Builder: `Nivel`, `NivelBuilder`, `NivelPartidaBuilder`, `DirectorNiveles` |
| 10 | A | Fondo estrellado, `Locutor`, HUD con corazones y pausa |
| 11 | A | Selección de personaje |
| 12 | A | Power-ups (`PowerUp`, `Pizza`, `SillaGamer`, `RGB`) |
| 13 | A | Menú y Game Over con humor, código Konami |
| 14 | A | Arma `BFG` y armas temporales |
| 15 | A | `LootBox`, anuncio de oleadas y limpieza de imágenes sin uso |
| 16 | B | La nave aparece con animación de entrada |
| 17 | B | Intro de oleada, enemigos que entran desde arriba y arreglo del enemigo atrapado |
| 18 | B | Fuente pixel-art propia y texto del Game Over corregido |
| 19 | B | Primer jefe (`EnemigoJefe`) y barra de vida |
| 20 | B | Campaña con jefes (Builder con jefe y aviso) y nuevo Lag |
| 21 | B | Guía, pantalla de victoria y HUD con oleada y enemigos restantes |
| 22 | B | Colisiones sin atascos, hitboxes justas e invulnerabilidad de 3 s con la mitad de daño |
| 23 | B | Enemigos en grupos dentro de la zona alta, velocidades calmadas y menos power-ups |
| 24 | B | Menú con mouse y teclado, pausa, opciones guardadas y cartel saltable |
| 25 | C | Enemigos que disparan: `BalaEnemiga`, `ContextoCombate` y gancho `atacar()` |
| 26 | C | Spammer y Sniper y campaña de 20 oleadas con la curva nueva |
| 27 | C | Música (menú, juego y jefe) y efectos de sonido con volumen |
| 28 | C | Habilidad especial por personaje (Strategy) con barra de energía |
| 29 | C | Jefes con fases, escudo y ataques propios (Strategy) |
| 30 | C | Efectos de impacto (temblor, partículas, corazón roto) y guía de 2 páginas |
| 31 | C | Documentación (Strategy, encapsulamiento) |
| 32 | B | Documentación (herencia, Builder, jefes y dificultad) |
| 33 | C | README y documentación restante |

## Instalación y ejecución

### Requisitos
- **JDK 17** para ejecutar Gradle (el plugin `construo` del módulo `lwjgl3` de la plantilla del profe lo exige). El código se compila con compatibilidad Java 11 (`sourceCompatibility = 11`).
- Conexión a internet la primera vez (Gradle descarga LibGDX).

### Pasos
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Katongo777/CalypsoGM.git
   cd CalypsoGM
   ```
2. Ejecutar el juego:
   ```bash
   gradlew.bat lwjgl3:run        # Windows
   ./gradlew lwjgl3:run          # Linux / macOS
   ```
3. (Opcional) generar el `.jar` ejecutable:
   ```bash
   gradlew.bat lwjgl3:jar
   ```
   El archivo queda en `lwjgl3/build/libs/SpaceNav2024-1.0.0.jar`.

### Desde un IDE (Eclipse / IntelliJ)
Importar la carpeta como **proyecto Gradle** y ejecutar la clase `puppy.code.lwjgl3.Lwjgl3Launcher` (módulo `lwjgl3`). La carpeta de trabajo debe ser `assets` (Gradle ya lo configura en la tarea `run`).

### Archivos que se generan al jugar
`assets/record.txt` guarda el récord. Está en el `.gitignore`, así que no se sube al repositorio.

### Probar los jefes sin jugar todo el juego
Para empezar directamente en una oleada (por ejemplo, la del jefe final) se puede iniciar el juego con una opción de la JVM: `-Dcalypso.oleada=20`. En Eclipse o IntelliJ se pone en *VM options* de la configuración de ejecución. Si no se pone, el juego empieza en la oleada 1. Con el código Konami (modo dios) en el menú se tiene mucha vida para ver los ataques.

### Archivos que se generan al jugar
`assets/record.txt` (récord) y `assets/opciones.txt` (volumen y dificultad). Los dos están en el `.gitignore`.
