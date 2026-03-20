import tensorflow as tf
import numpy as np
import os

# ============================================================
# 1. Cargar el modelo entrenado
# ============================================================
print("Cargando modelo...")
model = tf.keras.models.load_model("../saved_model/modelo_fruta.keras")
print("Modelo cargado correctamente")

# ============================================================
# 2. Convertir a TensorFlow Lite
# ============================================================
print("\nConvirtiendo a TFLite...")
converter = tf.lite.TFLiteConverter.from_keras_model(model)

# Optimización: reduce el tamaño del modelo sin perder precisión
converter.optimizations = [tf.lite.Optimize.DEFAULT]

tflite_model = converter.convert()
print("Conversión completada")

# ============================================================
# 3. Guardar el archivo .tflite
# ============================================================
output_path = "../exports/modelo_fruta.tflite"
os.makedirs(os.path.dirname(output_path), exist_ok=True)

with open(output_path, "wb") as f:
    f.write(tflite_model)

size_mb = os.path.getsize(output_path) / (1024 * 1024)
print(f"\nModelo exportado en: {output_path}")
print(f"Tamaño del archivo: {size_mb:.2f} MB")

# ============================================================
# 4. Verificar que el modelo .tflite funciona correctamente
# ============================================================
print("\nVerificando el modelo exportado...")
interpreter = tf.lite.Interpreter(model_path=output_path)
interpreter.allocate_tensors()

input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

print(f"Input esperado:  shape={input_details[0]['shape']}, dtype={input_details[0]['dtype']}")
print(f"Output esperado: shape={output_details[0]['shape']}, dtype={output_details[0]['dtype']}")

# Prueba con una imagen aleatoria para confirmar que no da errores
imagen_prueba = np.random.rand(1, 224, 224, 3).astype(np.float32)
interpreter.set_tensor(input_details[0]['index'], imagen_prueba)
interpreter.invoke()
resultado = interpreter.get_tensor(output_details[0]['index'])

print(f"\nPrueba con imagen aleatoria:")
print(f"  Resultado raw: {resultado[0][0]:.4f}")
print(f"  Interpretación: {'rotten' if resultado[0][0] > 0.5 else 'fresh'}")
print("\n✅ Modelo TFLite verificado y listo para usar en Android")