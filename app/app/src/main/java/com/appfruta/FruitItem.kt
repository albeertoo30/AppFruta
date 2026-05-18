package com.appfruta

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class FruitItem(
    @DocumentId val id: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val expiryDate: Timestamp = Timestamp.now(),
    val label: String = "fresh",
    val confidence: Float = 0f,
    val addedAt: Timestamp = Timestamp.now()
)
