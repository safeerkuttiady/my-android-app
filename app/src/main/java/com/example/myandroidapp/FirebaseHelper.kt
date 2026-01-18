package com.example.myandroidapp

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseHelper(private val context: Context) {
    private val TAG = "FirebaseHelper"

    // Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    // Firebase Realtime Database instance
    private lateinit var database: DatabaseReference

    init {
        initializeFirebase()
    }

    private fun initializeFirebase() {
        auth = Firebase.auth
        database = Firebase.database.reference
        Log.d(TAG, "Firebase initialized")
    }

    // Auth methods
    fun signInWithEmailAndPassword(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    callback(true, null)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    callback(false, task.exception?.message)
                }
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    callback(true, null)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    callback(false, task.exception?.message)
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    // Database methods
    fun writeUserData(userId: String, name: String, email: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email
        )

        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }

    fun getUserData(userId: String, callback: (Map<String, Any>?) -> Unit) {
        database.child("users").child(userId).get().addOnSuccessListener { dataSnapshot ->
            val userData = dataSnapshot.value as? Map<String, Any>
            callback(userData)
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Getting user data error", exception)
            callback(null)
        }
    }
}