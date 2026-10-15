# 🐾 Cerberus: Multi-Agent Enterprise Intelligence System

<div align="center">

[![Architecture](https://img.shields.io/badge/Architecture-Hybrid%20MAS-orange.svg)](#-arquitectura)
[![Status](https://img.shields.io/badge/Status-Active%20Development-success.svg)]()

*Un sistema multi-agente (MAS) de modelo híbrido diseñado para potenciar, automatizar y sincronizar múltiples áreas organizacionales — desde Desarrollo y QA hasta Recursos Humanos — mediante la orquestación inteligente de LLMs especializados.*

</div>

---

## 🏛️ ¿Qué es Cerberus?

**Cerberus** es un Sistema Multi-Agente (MAS) de nivel empresarial que simula la vigilancia y guardia coordinada de múltiples cabezas, cada una especializada en un dominio corporativo crítico. A diferencia de las soluciones basadas en un único LLM genérico, Cerberus implementa un **enfoque híbrido y modular**, delegando tareas específicas a los modelos de lenguaje más eficientes, económicos o potentes según las necesidades del departamento.

Ya sea analizando bases de código complejas, automatizando pipelines de pruebas o gestionando procesos de talento humano, Cerberus centraliza la inteligencia artificial manteniendo la especialización por área.

---

## 🧬 Arquitectura del Sistema

El ecosistema de Cerberus opera bajo un esquema cooperativo donde cada área cuenta con su propio "sub-enjambre" de agentes autónomos:

```
                      [ 🌐 Orquestador Central / Router ]
                                       │
         ┌─────────────────────────────┼─────────────────────────────┐
         ▼                             ▼                             ▼
  [ 💻 Desarrollo ]               [ 🧪 QA & Testing ]            [ 👥 Recursos Humanos ]
  • Arquitecto de Software        • Ingeniero de Pruebas         • Reclutador IA
  • Revisor de Código (PR)        • Generador de Casos (BDD)     • Onboarding Assistant

```

### 🔀 Modelo Híbrido por Dominio
* **Desarrollo (Dev Head):** Optimizado para la generación de código, refactorización y revisión de pull requests utilizando modelos con alta ventana de contexto y razonamiento lógico profundo.
* **Calidad (QA Head):** Enfocado en la detección de bugs, análisis estático, cobertura de pruebas y generación de escenarios de integración.
* **Recursos Humanos (HR Head):** Especializado en la privacidad y gestión de datos sensibles, filtrado de currículums, soporte a empleados y políticas internas (ideal para modelos de código abierto alojados localmente por seguridad).

---

## 🤝 Contribución

¡Las contribuciones son bienvenidas! Si deseas añadir un nuevo agente departamental, mejorar el motor de enrutamiento o proponer optimizaciones, por favor contactanos y abre un Pull Request.
