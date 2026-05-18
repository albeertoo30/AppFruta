package com.appfruta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFruitDialogFragment : BottomSheetDialogFragment() {

    private val repository = InventoryRepository()
    private var selectedExpiryMillis: Long = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L

    companion object {
        private const val ARG_LABEL = "label"
        private const val ARG_CONFIDENCE = "confidence"
        private const val ARG_ITEM_ID = "item_id"
        private const val ARG_ITEM_NAME = "item_name"
        private const val ARG_ITEM_QUANTITY = "item_quantity"
        private const val ARG_ITEM_EXPIRY = "item_expiry"

        fun newInstance(label: String = "fresh", confidence: Float = 0f) =
            AddFruitDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LABEL, label)
                    putFloat(ARG_CONFIDENCE, confidence)
                }
            }

        fun forEdit(item: FruitItem) = AddFruitDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ITEM_ID, item.id)
                putString(ARG_ITEM_NAME, item.name)
                putInt(ARG_ITEM_QUANTITY, item.quantity)
                putLong(ARG_ITEM_EXPIRY, item.expiryDate.seconds * 1000L)
                putString(ARG_LABEL, item.label)
                putFloat(ARG_CONFIDENCE, item.confidence)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_add_fruit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run { dismiss(); return }

        val tvTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val tilName = view.findViewById<TextInputLayout>(R.id.tilName)
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etQuantity = view.findViewById<TextInputEditText>(R.id.etQuantity)
        val etExpiryDate = view.findViewById<TextInputEditText>(R.id.etExpiryDate)
        val tilExpiryDate = view.findViewById<TextInputLayout>(R.id.tilExpiryDate)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)

        val args = arguments ?: Bundle()
        val existingId = args.getString(ARG_ITEM_ID)
        val isEditing = existingId != null

        tvTitle.setText(if (isEditing) R.string.dialog_edit_title else R.string.dialog_add_title)

        if (isEditing) {
            etName.setText(args.getString(ARG_ITEM_NAME, ""))
            etQuantity.setText(args.getInt(ARG_ITEM_QUANTITY, 1).toString())
            selectedExpiryMillis = args.getLong(ARG_ITEM_EXPIRY, selectedExpiryMillis)
        }
        updateDateDisplay(etExpiryDate)

        etExpiryDate.setOnClickListener { showDatePicker(etExpiryDate) }
        tilExpiryDate.setEndIconOnClickListener { showDatePicker(etExpiryDate) }

        btnSave.setOnClickListener {
            val name = etName.text?.toString()?.trim() ?: ""
            if (name.isEmpty()) {
                tilName.error = getString(R.string.error_empty_name)
                return@setOnClickListener
            }
            tilName.error = null
            val quantity = etQuantity.text?.toString()?.toIntOrNull()?.coerceAtLeast(1) ?: 1
            val label = args.getString(ARG_LABEL, "fresh") ?: "fresh"
            val confidence = args.getFloat(ARG_CONFIDENCE, 0f)
            val item = FruitItem(
                id = existingId ?: "",
                name = name,
                quantity = quantity,
                expiryDate = Timestamp(Date(selectedExpiryMillis)),
                label = label,
                confidence = confidence
            )
            lifecycleScope.launch {
                try {
                    if (isEditing) repository.update(uid, item) else repository.add(uid, item)
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_save, Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancel.setOnClickListener { dismiss() }
    }

    private fun showDatePicker(etDate: TextInputEditText) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.hint_expiry_date)
            .setSelection(selectedExpiryMillis)
            .build()
        picker.addOnPositiveButtonClickListener { millis ->
            selectedExpiryMillis = millis
            updateDateDisplay(etDate)
        }
        picker.show(parentFragmentManager, "date_picker")
    }

    private fun updateDateDisplay(etDate: TextInputEditText) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etDate.setText(sdf.format(Date(selectedExpiryMillis)))
    }
}
