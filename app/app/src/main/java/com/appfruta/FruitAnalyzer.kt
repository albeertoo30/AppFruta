package com.appfruta

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class FruitAnalysisResult(
    val label: String,
    val confidence: Float
)

class FruitAnalyzer(context: Context) {

    companion object {
        private const val MODEL_NAME = "modelo_fruta.tflite"
        private const val INPUT_SIZE = 224
        private const val CHANNELS = 3
        private const val BYTES_PER_CHANNEL = 4 // float32
        private const val THRESHOLD_ROTTEN = 0.5f
    }

    private val interpreter: Interpreter = Interpreter(loadModelFile(context))

    /**
     * Analiza una imagen y devuelve:
     * - label: "fresh" o "rotten"
     * - confidence: 0.0..1.0 (confianza de la clase final)
     */
    fun analyze(sourceBitmap: Bitmap): FruitAnalysisResult {
        val resizedBitmap = Bitmap.createScaledBitmap(sourceBitmap, INPUT_SIZE, INPUT_SIZE, true)
        val inputBuffer = bitmapToFloat32Buffer(resizedBitmap)

        // El modelo retorna un valor [0..1], donde >= 0.5 indica rotten.
        val output = Array(1) { FloatArray(1) }
        interpreter.run(inputBuffer, output)

        val rottenScore = output[0][0].coerceIn(0f, 1f)
        val label = if (rottenScore >= THRESHOLD_ROTTEN) "rotten" else "fresh"
        val confidence = if (label == "rotten") rottenScore else 1f - rottenScore

        return FruitAnalysisResult(label = label, confidence = confidence)
    }

    private fun loadModelFile(context: Context): ByteBuffer {
        val modelBytes = context.assets.open(MODEL_NAME).use { input ->
            input.readBytes()
        }
        return ByteBuffer.allocateDirect(modelBytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(modelBytes)
            rewind()
        }
    }

    private fun bitmapToFloat32Buffer(bitmap: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(
            INPUT_SIZE * INPUT_SIZE * CHANNELS * BYTES_PER_CHANNEL
        ).apply {
            order(ByteOrder.nativeOrder())
        }

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        // Normaliza RGB a rango 0..1, requerido por tu modelo.
        for (pixel in pixels) {
            val red = ((pixel shr 16) and 0xFF) / 255f
            val green = ((pixel shr 8) and 0xFF) / 255f
            val blue = (pixel and 0xFF) / 255f
            inputBuffer.putFloat(red)
            inputBuffer.putFloat(green)
            inputBuffer.putFloat(blue)
        }

        inputBuffer.rewind()
        return inputBuffer
    }
}
