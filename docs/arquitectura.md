# Arquitectura del proyecto

## Stack tecnológico
- **Modelo IA**: Python + TensorFlow + MobileNetV2 (Transfer Learning)
- **Exportación**: TensorFlow Lite (.tflite)
- **App móvil**: Android + Kotlin

## Alcance del POC
Prueba de concepto centrada en la funcionalidad mínima: fotografiar una fruta y obtener si está en buen o mal estado. Funcionalidades opcionales (inventario, notificaciones, recetas) 
quedan fuera del alcance actual.

## Flujo de datos
1. El usuario fotografía una fruta desde la app
2. La imagen se pasa al modelo .tflite integrado en local
3. El modelo devuelve la clasificación (bueno/malo + tipo de fruta)

## Estructura del repositorio
Ver README.md
