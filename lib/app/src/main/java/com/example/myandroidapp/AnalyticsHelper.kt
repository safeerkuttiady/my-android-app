package com.example.myandroidapp

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsHelper(context: Context) {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logEvent(eventName: String, params: Map<String, Any>? = null) {
        val bundle = Bundle()
        params?.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putBoolean(key, value)
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun logScreenView(screenName: String, screenClass: String? = null) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass ?: screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    fun setUserId(id: String) {
        firebaseAnalytics.setUserId(id)
    }

    // Predefined events
    fun logLogin(method: String) {
        val params = mapOf(
            FirebaseAnalytics.Param.METHOD to method
        )
        logEvent(FirebaseAnalytics.Event.LOGIN, params)
    }

    fun logSignUp(method: String) {
        val params = mapOf(
            FirebaseAnalytics.Param.SIGN_UP_METHOD to method
        )
        logEvent(FirebaseAnalytics.Event.SIGN_UP, params)
    }

    fun logShare(contentType: String, itemId: String) {
        val params = mapOf(
            FirebaseAnalytics.Param.CONTENT_TYPE to contentType,
            FirebaseAnalytics.Param.ITEM_ID to itemId
        )
        logEvent(FirebaseAnalytics.Event.SHARE, params)
    }

    fun logPurchase(value: Double, currency: String = "USD") {
        val params = mapOf(
            FirebaseAnalytics.Param.VALUE to value,
            FirebaseAnalytics.Param.CURRENCY to currency
        )
        logEvent(FirebaseAnalytics.Event.PURCHASE, params)
    }
}