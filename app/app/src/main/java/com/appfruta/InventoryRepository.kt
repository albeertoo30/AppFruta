package com.appfruta

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InventoryRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun collection(uid: String) =
        db.collection("users").document(uid).collection("inventory")

    fun observeItems(uid: String): Flow<List<FruitItem>> = callbackFlow {
        val listener = collection(uid)
            .orderBy("expiryDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                trySend(snapshot.toObjects(FruitItem::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun add(uid: String, item: FruitItem) {
        collection(uid).add(item).await()
    }

    suspend fun update(uid: String, item: FruitItem) {
        collection(uid).document(item.id).set(item).await()
    }

    suspend fun delete(uid: String, itemId: String) {
        collection(uid).document(itemId).delete().await()
    }
}
