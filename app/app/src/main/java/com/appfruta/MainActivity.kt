package com.appfruta

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var analyzer: FruitAnalyzer
    private var cameraImageUri: Uri? = null

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, getString(R.string.camera_permission_needed), Toast.LENGTH_LONG).show()
            }
        }

    // Lanza camara nativa y guarda imagen en una Uri temporal creada por FileProvider.
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess && cameraImageUri != null) {
                processImageFromUri(cameraImageUri!!)
            } else {
                Toast.makeText(this, getString(R.string.image_error), Toast.LENGTH_SHORT).show()
            }
        }

    // Selector de galeria con la Activity Result API moderna.
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                processImageFromUri(uri)
            }
        }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null) {
            goToLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        analyzer = FruitAnalyzer(this)

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            goToLogin()
        }

        findViewById<MaterialButton>(R.id.btnCamera).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        findViewById<MaterialButton>(R.id.btnGallery).setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    private fun openCamera() {
        val imageFile = createTempImageFile()
        cameraImageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )
        takePictureLauncher.launch(cameraImageUri)
    }

    private fun createTempImageFile(): File {
        val imagesDir = File(cacheDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        return File.createTempFile("fruit_", ".jpg", imagesDir)
    }

    private fun processImageFromUri(uri: Uri) {
        val bitmap = decodeBitmap(uri)
        if (bitmap == null) {
            Toast.makeText(this, getString(R.string.image_error), Toast.LENGTH_LONG).show()
            return
        }

        val analysis = try {
            analyzer.analyze(bitmap)
        } catch (exception: Exception) {
            Toast.makeText(this, getString(R.string.analysis_error), Toast.LENGTH_LONG).show()
            return
        }

        val imagePath = saveBitmapForResult(bitmap)
        openResultScreen(imagePath, analysis.label, analysis.confidence)
    }

    private fun decodeBitmap(uri: Uri): Bitmap? {
        return contentResolver.openInputStream(uri).use { inputStream: InputStream? ->
            if (inputStream != null) BitmapFactory.decodeStream(inputStream) else null
        }
    }

    // Guardamos una copia en cache para mostrarla en ResultActivity sin pasar Bitmaps grandes por Intent.
    private fun saveBitmapForResult(bitmap: Bitmap): String {
        val imageFile = File(cacheDir, "last_result.jpg")
        imageFile.outputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, output)
        }
        return imageFile.absolutePath
    }

    private fun openResultScreen(imagePath: String, label: String, confidence: Float) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_PATH, imagePath)
            putExtra(ResultActivity.EXTRA_LABEL, label)
            putExtra(ResultActivity.EXTRA_CONFIDENCE, confidence)
        }
        startActivity(intent)
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}