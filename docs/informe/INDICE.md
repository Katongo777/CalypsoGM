# Índice del reporte — Calypso: Skill Issue Edition

Este reporte responde punto por punto a cada requerimiento del **Proyecto Juego (GM)**, indicando cómo se aplicó y dónde está la evidencia (archivos `.java`, diagramas y pantallazos).

| Requerimiento | Qué pide | Dónde está |
|---|---|---|
| **GM-1** | Explicación del juego desde el jugador (objetivos, narrativa, elementos, controles, pantallazos) | [C1_explicacion_del_juego.md](C1_explicacion_del_juego.md) |
| **GM-2** | Análisis desde la ingeniería de software: juego base y modificaciones | [C2_analisis_ingenieria.md](C2_analisis_ingenieria.md) |
| **GM-3** | UML con las clases del dominio y su código en Java | [B1_herencia_clases_abstractas_interfaces.md](B1_herencia_clases_abstractas_interfaces.md) (diagrama `docs/uml/herencia.png`) |
| **GM-4** | 1 clase abstracta (padre de al menos 2) y 1 interfaz (implementada por al menos 2), utilizadas | [B1_herencia_clases_abstractas_interfaces.md](B1_herencia_clases_abstractas_interfaces.md) — `Entidad`, `Enemigo` (con `EnemigoJefe`), `PowerUp` y las interfaces `EstrategiaDisparo` y `Colisionable` |
| **GM-5** | Encapsulamiento y principios de orientación a objetos | [A1_encapsulamiento_y_principios.md](A1_encapsulamiento_y_principios.md) |
| **GM-6** | Patrón **Singleton** | [C3_patron_singleton.md](C3_patron_singleton.md) — `GestorRecursos` |
| **GM-7** | Patrón **Strategy** | [A2_patron_strategy.md](A2_patron_strategy.md) — `EstrategiaDisparo` y las armas |
| **GM-8** | Patrón **Template Method** | [C4_patron_template_method.md](C4_patron_template_method.md) — `PantallaBase` |
| **GM-9** | Patrón **Builder** | [B2_patron_builder.md](B2_patron_builder.md) — `DirectorNiveles`, `NivelBuilder`, `Nivel` |
| **GM-10** | GitHub (al menos 6 commits) + instrucciones de instalación y ejecución | [C5_ejecucion_y_github.md](C5_ejecucion_y_github.md) |

## Diagramas UML
Todos los diagramas están en [`docs/uml/`](../uml/) (cada uno con su imagen `.png` y su código fuente `.puml`):

- `herencia.png` — Jerarquía de clases (Entidad, Enemigo, PowerUp, Colisionable)
- `strategy.png` — Patrón Strategy (armas)
- `strategy-habilidades-jefes.png` — Strategy en habilidades y en ataques de jefes
- `singleton.png` — Patrón Singleton
- `template-method.png` — Patrón Template Method
- `builder.png` — Patrón Builder

## Documento de apoyo
- [C6_diseno_jefes_y_dificultad.md](C6_diseno_jefes_y_dificultad.md): hoja de datos de jefes, enemigos y dificultad (qué número cambiar y dónde).

## Pantallazos
En [`docs/capturas/`](../capturas/).
