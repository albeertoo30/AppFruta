# AppFruta
## Detector de estado de frutas y verduras

Aplicación móvil Android que utiliza inteligencia artificial para detectar
si una fruta o verdura está en buen o mal estado a través de una fotografía.

---

## Alcance del POC

Esta prueba de concepto cubre únicamente la funcionalidad mínima:
fotografiar una fruta y obtener si está en buen o mal estado.

Funcionalidades opcionales (inventario, notificaciones push, recetas)
quedan fuera del alcance actual.

---

## Tecnologías

- **Modelo IA**: Python + TensorFlow + MobileNetV2 (Transfer Learning)
- **Exportación**: TensorFlow Lite (.tflite)
- **App móvil**: Android + Kotlin

---

## Estructura del repositorio
```
AppFruta/
├── data/
│   ├── raw/          ← Imágenes originales (no incluidas en el repo)
│   └── processed/    ← Imágenes preprocesadas (no incluidas en el repo)
├── model/
│   ├── notebooks/    ← Jupyter Notebooks de exploración y entrenamiento
│   ├── scripts/      ← Scripts Python listos para ejecutar
│   ├── saved_model/  ← Modelo entrenado (no incluido en el repo)
│   └── exports/      ← Modelo exportado a .tflite (incluido en el repo)
├── app/              ← Proyecto Android
└── docs/             ← Documentación técnica
```

---

## Cómo empezar

### Modelo

1. Clonar el repositorio
```bash
   git clone https://github.com/TU_USUARIO/fruta-ia.git
   cd fruta-ia
```

2. Instalar dependencias Python
```bash
   pip install -r model/requirements.txt
```

3. Descargar el dataset desde Kaggle y colocarlo en `data/raw/`

4. Seguir los notebooks en orden dentro de `model/notebooks/`

### App

1. Abrir la carpeta `app/` con Android Studio
2. Asegurarse de que el archivo `modelo_fruta.tflite` está en `app/app/src/main/assets/`
3. Ejecutar en emulador o dispositivo físico

---

## Equipo

Proyecto desarrollado por 5 personas como parte de una prueba de concepto
para NTT Data.
