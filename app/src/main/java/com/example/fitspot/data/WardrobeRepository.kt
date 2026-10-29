package com.example.fitspot.data

import com.example.fitspot.models.WardrobeItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class WardrobeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("wardrobe")

    fun getWardrobeItems(): Flow<List<WardrobeItem>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val items = snapshot.toObjects(WardrobeItem::class.java)
                trySend(items)
            }
        }
        awaitClose { subscription.remove() }
    }

    fun updateWardrobeItemPrice(itemId: String, newPrice: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("wardrobe").document(itemId)
            .update("price", newPrice)
            .addOnSuccessListener {
                // Successfully updated in Firestore
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

    fun addItem(item: WardrobeItem) = collection.add(item)

    fun updateItem(item: WardrobeItem) = collection.document(item.id).set(item)

    fun deleteItem(itemId: String) = collection.document(itemId).delete()
}