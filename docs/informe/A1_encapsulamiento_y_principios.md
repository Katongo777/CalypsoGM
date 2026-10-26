# Encapsulamiento y principios de diseño (GM-5)

> Requerimiento GM-5: *Aplicación de encapsulamiento y principios de orientación a objetos.*

## 1. ¿Qué problema tenía el juego base?

El juego que nos dio el profe funcionaba, pero tenía varias cosas que, al revisarlas con lo que vimos en clases, no eran buenas prácticas:

- **El modelo conocía a la vista.** La nave (`Nave4`) recibía la pantalla (`PantallaJuego`) por parámetro y le entregaba las balas con `juego.agregarBala(...)`. O sea, una clase que representa *datos del juego* dependía de una clase que representa *lo que se ve en pantalla*. Si cambiábamos la pantalla, se rompía la nave.
- **Los hijos tocaban los atributos del padre directamente.** Cuando creamos la clase abstracta `Entidad`, los atributos (el sprite y la velocidad) quedaron como `protected`, así que cualquier hija podía modificarlos sin pasar por ningún método.
- **Código repetido** (cada clase tenía su propia lógica de bordes, de dibujo, etc.) y clases que hacían demasiadas cosas a la vez (por ejemplo, `PantallaJuego` dibujaba el HUD, manejaba las colisiones y cargaba las imágenes).

## 2. ¿Qué hicimos? (con evidencia)

### 2.1 Atributos privados y acceso controlado
En `core/src/main/java/puppy/code/modelo/Entidad.java` todos los atributos son **privados** (`spr`, `xVel`, `yVel`). Las demás clases los usan solo a través de:

- **getters y setters públicos**: `getX()`, `getY()`, `getAncho()`, `getAlto()`, `getXVel()`, `setXVel(...)`...
- **métodos protegidos** pensados solo para las hijas: `setPosicion(x, y)`, `setTamano(ancho, alto)` y `rotar(grados)`.

Así, si algún día cambiamos cómo se guarda la posición, solo hay que modificar `Entidad`.

### 2.2 La nave ya no depende de la vista
En `Nave4.java` el método `disparar()` ahora **devuelve** la lista de balas (`List<Bullet>`) y la pantalla decide qué hacer con ellas (`PantallaJuego.actualizar()` hace `balas.addAll(nave.disparar())`). Con esto el paquete `modelo` ya no importa nada del paquete `vista`.

### 2.3 Métodos privados que esconden los detalles
Cada clase muestra hacia afuera solo lo necesario y esconde el "cómo":

| Clase | Método privado | Qué esconde |
|---|---|---|
| `Nave4` | `manejarInputTeclado()`, `actualizarPosicion()`, `recibirDano()` | Cómo se lee el teclado y cómo se pierde una vida |
| `Bullet` | `comprobarLimitesPantalla()`, `destruir()` | Cuándo una bala desaparece |
| `GestorRecursos` | `leerRecord()`, `guardarRecord()` | Cómo se guarda el récord en el archivo |
| `PantallaJuego` | `crearEnemigos()`, `soltarPowerUp()` | Cómo se arma una oleada y cuándo cae un power-up |

### 2.4 Otras decisiones de encapsulamiento
- `Personaje` es un `enum` con atributos `final`: sus datos (vidas, velocidad, nave) no se pueden modificar una vez creados.
- `Constantes` y `GestorRecursos` tienen constructor **privado** (no se pueden instanciar desde afuera).
- Las listas (`enemigos`, `balas`, `powerUps`) son `private` dentro de `PantallaJuego`; nadie más puede agregarles o sacarles elementos.

## 3. Principios SOLID aplicados

| Principio | Cómo lo aplicamos | Dónde está |
|---|---|---|
| **S** – Responsabilidad única | Cada clase hace una sola cosa: `Hud` dibuja el HUD, `Locutor` los mensajes, `FondoEstrellado` el fondo, `GestorRecursos` los recursos. `PantallaJuego` quedó solo coordinando. | `vista/Hud.java`, `vista/Locutor.java`, `vista/FondoEstrellado.java` |
| **O** – Abierto/Cerrado | Para agregar un arma nueva solo se crea una clase que implemente `EstrategiaDisparo`; no se toca `Nave4`. Lo mismo con un enemigo nuevo (hereda de `Enemigo`) o un power-up nuevo (hereda de `PowerUp`). | `modelo/armas/`, `modelo/enemigos/`, `modelo/powerups/` |
| **L** – Sustitución de Liskov | `PantallaJuego` guarda `ArrayList<Enemigo>` y `ArrayList<PowerUp>` y trata igual a todos los hijos: un `EnemigoCheater` puede usarse donde se espera un `Enemigo` sin romper nada. | `vista/PantallaJuego.java` |
| **I** – Segregación de interfaces | Las interfaces son chicas: `Colisionable` tiene 2 métodos y `EstrategiaDisparo` 3. Nadie está obligado a implementar métodos que no usa. | `modelo/Colisionable.java`, `modelo/armas/EstrategiaDisparo.java` |
| **D** – Inversión de dependencias | `Nave4` depende de la abstracción `EstrategiaDisparo`, no de `Pistola` ni de `Escopeta`. | `modelo/Nave4.java` |

## 4. Principios GRASP y Ley de Demeter

- **Experto en información:** quien tiene los datos hace el trabajo. `Enemigo` sabe cuánta vida le queda (`recibirDano`, `estaMuerto`); `Nave4` sabe cuándo puede volver a disparar (`cooldownRestante`).
- **Creador:** `DirectorNiveles` crea los `Nivel`; `GestorRecursos` crea las texturas; `EnemigoCheater` crea los `EnemigoLag` que suelta.
- **Alta cohesión y bajo acoplamiento:** las clases pequeñas y sin dependencias cruzadas entre modelo y vista.
- **Polimorfismo:** `enemigo.actualizar()` y `powerUp.aplicar(nave)` se llaman sin saber el tipo concreto.
- **Ley de Demeter ("habla solo con tus amigos cercanos"):** el HUD necesita el nombre del arma. Al principio hacía `nave.getEstrategia().getNombre()` (pedirle algo a un objeto que le pedí a otro). Lo corregimos agregando `Nave4.getNombreArma()`, así el HUD solo habla con la nave.

## 5. Limitaciones (para ser honestos)
- `Nivel` tiene setters públicos porque el `Builder` necesita llenarlo; una mejora sería hacer el `Nivel` inmutable (dejar los setters solo para el Builder).
- `PantallaJuego` sigue siendo la clase más larga (~330 líneas) porque coordina todo el juego; se podría separar el manejo de colisiones en otra clase.


## Más ejemplos de la etapa final

- **Hitbox encapsulada.** Las imágenes tienen bordes transparentes y los choques se sentían injustos. En `Entidad` agregamos un atributo privado `factorHitbox` y su método protegido `setFactorHitbox()`. Cada hija elige qué parte de su dibujo cuenta para chocar (la nave 55 %, los enemigos 80 %) y nadie más toca el rectángulo de colisión directamente.
- **`ContextoCombate` (menos dependencias).** Los enemigos que disparan o invocan necesitan saber dónde está la nave y dónde dejar sus balas. En vez de pasarles `PantallaJuego` (que sería volver al problema del juego base), la pantalla les entrega un `ContextoCombate`: un objeto pequeño con solo lo que pueden usar. Así `modelo` sigue sin conocer a `vista`.
- **Ley de Demeter.** Para dibujar la barra de habilidad, el HUD le pregunta a la nave (`nave.getHabilidad().getNombre()`, `nave.getEnergia()`), no a las clases internas de la habilidad.
- **Tinte con método propio.** Descubrimos que `Sprite.draw()` ignora el color del `SpriteBatch`. En vez de que cada clase manipule el sprite, `Entidad` ofrece `setTinte()`, y las balas, el Lag y los jefes lo usan.
- **Responsabilidad única.** Sacamos de `PantallaJuego` el dibujo de la barra del jefe (`BarraJefe`), las partículas (`Particulas`), los botones (`MenuBotones`) y las opciones guardadas (`Opciones`).
