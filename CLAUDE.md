# CLAUDE.md — Instrucciones para el agente

## Descripción del proyecto

FrutaIA es una aplicación móvil Android desarrollada como proyecto personal
a partir de una Prueba de Concepto realizada para NTT Data. El objetivo es
detectar el estado de frescura de frutas y verduras mediante IA, y evolucionar
hacia una aplicación con funcionalidad completa.

---

## Estructura del repositorio
AppFruta/
├── data/                  ← Dataset de imágenes (no incluido en el repo)
├── model/
│   ├── notebooks/         ← Jupyter Notebooks de exploración y entrenamiento
│   ├── scripts/           ← Scripts Python (export_tflite.py)
│   ├── saved_model/       ← Modelo .keras entrenado (no incluido en el repo)
│   └── exports/           ← modelo_fruta.tflite (SÍ incluido en el repo)
├── app/                   ← Proyecto Android completo (abrir con Android Studio)
│   └── app/src/main/
│       ├── assets/        ← Aquí reside modelo_fruta.tflite
│       ├── java/com/appfruta/
│       │   ├── MainActivity.kt
│       │   ├── ResultActivity.kt
│       │   └── FruitAnalyzer.kt
│       └── res/
├── docs/
│   ├── arquitectura.md
│   └── guia_contribucion.md
├── CLAUDE.md              ← Este archivo
├── README.md
└── .gitignore

---

## Stack tecnológico

### Modelo IA
- Python 3.11 + TensorFlow ≥ 2.13.0
- Arquitectura: MobileNetV2 con Transfer Learning (Feature Extraction)
- Clasificación binaria: fresh (0) / rotten (1)
- Exportado a TFLite sin cuantización → modelo_fruta.tflite (9,2 MB, float32)

### Aplicación Android
- Lenguaje: Kotlin 2.0.21
- SDK mínimo: API 26 (Android 8.0) | Compilación: API 34 (Android 14)
- Build: Gradle 8.7 con Kotlin DSL (ficheros .kts) + AGP 8.5.2
- UI: Material Design 3
- TensorFlow Lite 2.15.0 para inferencia local (offline, sin backend)
- Gestión de dependencias centralizada en app/gradle/libs.versions.toml

### Clases principales
- `MainActivity.kt` — Pantalla principal, captura de imagen, orquesta el flujo
- `ResultActivity.kt` — Pantalla de resultados, muestra clasificación y confianza
- `FruitAnalyzer.kt` — Encapsula toda la lógica de inferencia TFLite

---

## Estado actual de la aplicación (PoC funcional)

La PoC implementa el flujo core completo:
1. Usuario captura foto con cámara o selecciona desde galería
2. FruitAnalyzer preprocesa el Bitmap (redimensión 224×224, normalización 0-1)
3. Inferencia con TFLite → output float32 en rango [0,1]
4. Umbral 0.5: < 0.5 = fresh, > 0.5 = rotten
5. ResultActivity muestra resultado con código de color y porcentaje de confianza

---

## Roadmap de desarrollo (próximas fases)

### Fase 1 — Autenticación e infraestructura (EN CURSO)
- [ ] Integrar Firebase en el proyecto (google-services.json)
- [ ] Pantalla de registro e inicio de sesión (email/contraseña + Google Sign-In)
- [ ] Perfil de usuario básico
- [ ] Navegación correcta: login → main flow

### Fase 2 — Inventario de frutas
- [ ] Pantalla de inventario con lista de alimentos
- [ ] Al analizar fruta, opción de añadirla al inventario
- [ ] Formulario: nombre, cantidad, fecha de caducidad
- [ ] Firestore como base de datos del inventario por usuario
- [ ] Editar y eliminar entradas

### Fase 3 — Notificaciones push de caducidad
- [ ] Integrar Firebase Cloud Messaging (FCM)
- [ ] Lógica de alertas configurables (1 día, 3 días, 1 semana antes)
- [ ] Notificaciones locales programadas
- [ ] Pantalla de configuración de preferencias

### Fase 4 — Integración OpenAI
- [ ] Sugerencia de recetas con alimentos próximos a caducar
- [ ] Información nutricional y tiempos de conservación
- [ ] API key gestionada via Firebase Cloud Functions (nunca en el cliente)

---

## Reglas y convenciones de desarrollo

### Código Android/Kotlin
- Un archivo por clase, nombre de archivo igual al nombre de la clase
- Comentarios en español
- Seguir convenciones de Material Design 3 para cualquier componente nuevo
- Toda nueva Activity debe declararse en AndroidManifest.xml
- Las dependencias nuevas siempre se añaden a libs.versions.toml, nunca
  hardcodeadas en build.gradle.kts

### Firebase
- El archivo google-services.json NO se sube al repositorio (está en .gitignore)
- Las API keys y credenciales sensibles NUNCA van en el código del cliente
- Las llamadas a APIs externas (OpenAI) deben ir a través de Cloud Functions

### Archivos sensibles (ya en .gitignore, no modificar)
- google-services.json
- *.keystore
- local.properties
- data/raw/ y data/processed/ (dataset de imágenes)
- model/saved_model/ (modelo .keras, demasiado grande)

---

## Comandos útiles

```bash
# Activar entorno virtual Python (para trabajo con el modelo)
cd C:\Users\pc_fo\AppFruta
venv\Scripts\activate

# Reexportar el modelo TFLite
cd model/scripts
python export_tflite.py

# Compilar la app Android desde terminal
cd app
./gradlew assembleDebug
```

---

## Notas importantes para el agente

- El proyecto Android está en la subcarpeta `app/`, no en la raíz
- Android Studio debe abrirse apuntando a `app/` específicamente
- El archivo `modelo_fruta.tflite` debe estar siempre en
  `app/app/src/main/assets/` para que Gradle lo empaquete en el APK
- La cuantización del modelo está desactivada intencionadamente (float32)
  para mantener compatibilidad máxima durante el desarrollo
- No modificar FruitAnalyzer.kt sin revisar que el ByteBuffer sigue siendo
  compatible con el modelo TFLite actual (input: 224×224×3 float32)