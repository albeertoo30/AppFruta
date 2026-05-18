package com.appfruta

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_PATH = "extra_image_path"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_CONFIDENCE = "extra_confidence"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH)
        val label = intent.getStringExtra(EXTRA_LABEL) ?: "fresh"
        val confidence = intent.getFloatExtra(EXTRA_CONFIDENCE, 0f)

        val ivResultPhoto = findViewById<ImageView>(R.id.ivResultPhoto)
        val ivResultStatusIcon = findViewById<ImageView>(R.id.ivResultStatusIcon)
        val tvResultStatus = findViewById<TextView>(R.id.tvResultStatus)
        val tvResultMessage = findViewById<TextView>(R.id.tvResultMessage)
        val tvResultConfidence = findViewById<TextView>(R.id.tvResultConfidence)
        val btnAddToInventory = findViewById<MaterialButton>(R.id.btnAddToInventory)
        val btnAnalyzeAgain = findViewById<MaterialButton>(R.id.btnAnalyzeAgain)

        if (!imagePath.isNullOrBlank()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            ivResultPhoto.setImageBitmap(bitmap)
        }

        val isFresh = label == "fresh"
        if (isFresh) {
            ivResultStatusIcon.setImageResource(android.R.drawable.checkbox_on_background)
            tvResultStatus.setText(R.string.state_fresh)
            tvResultStatus.setTextColor(getColor(R.color.fruit_fresh))
            tvResultMessage.setText(R.string.state_fresh_message)
        } else {
            ivResultStatusIcon.setImageResource(android.R.drawable.ic_dialog_alert)
            tvResultStatus.setText(R.string.state_rotten)
            tvResultStatus.setTextColor(getColor(R.color.fruit_rotten))
            tvResultMessage.setText(R.string.state_rotten_message)
        }

        val confidencePercent = (confidence * 100).toInt()
        tvResultConfidence.text = getString(R.string.confidence_format, confidencePercent)

        btnAddToInventory.setOnClickListener {
            AddFruitDialogFragment.newInstance(label = label, confidence = confidence)
                .show(supportFragmentManager, "add_fruit")
        }

        btnAnalyzeAgain.setOnClickListener {
            finish()
        }
    }
}
