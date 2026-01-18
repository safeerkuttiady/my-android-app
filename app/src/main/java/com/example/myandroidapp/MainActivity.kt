package com.example.myandroidapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var adView: AdView
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var locationHelper: LocationHelper
    private lateinit var analyticsHelper: AnalyticsHelper
    private lateinit var adHelper: AdHelper

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // Test ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Helper
        firebaseHelper = FirebaseHelper(this)

        // Initialize Location Helper
        locationHelper = LocationHelper(this)
        locationHelper.onLocationUpdate = { location ->
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Location: ${location.latitude}, ${location.longitude}",
                    Toast.LENGTH_LONG
                ).show()

                // Log location event
                analyticsHelper.logEvent("location_updated", mapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude
                ))
            }
        }

        // Initialize Analytics Helper
        analyticsHelper = AnalyticsHelper(this)
        analyticsHelper.logScreenView("MainActivity")

        // Initialize Ad Helper
        adHelper = AdHelper(this)
        MobileAds.initialize(this) {}

        // Load and display banner ad
        adView = findViewById(R.id.adView)
        adHelper.loadBannerAd(adView)

        // Load interstitial ad for later use
        adHelper.loadInterstitialAd(this, INTERSTITIAL_AD_UNIT_ID) {
            analyticsHelper.logEvent("interstitial_loaded")
            runOnUiThread {
                Toast.makeText(this, "Interstitial ad loaded", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up button listeners
        setupButtons()

        analyticsHelper.logEvent("MainActivity_created")
    }

    private fun setupButtons() {
        val authButton = findViewById<Button>(R.id.auth_button)
        val locationButton = findViewById<Button>(R.id.location_button)
        val analyticsButton = findViewById<Button>(R.id.analytics_button)

        authButton.setOnClickListener {
            analyticsHelper.logEvent("auth_button_clicked")

            // For demo purposes, we'll use a test account
            firebaseHelper.signInWithEmailAndPassword("test@example.com", "password123") { success, errorMessage ->
                runOnUiThread {
                    if (success) {
                        analyticsHelper.logLogin("demo_method")
                        Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show()

                        // Show interstitial ad after successful login
                        if (adHelper.isInterstitialAdLoaded()) {
                            adHelper.showInterstitialAd {
                                analyticsHelper.logEvent("interstitial_closed_after_login")
                            }
                        }
                    } else {
                        Toast.makeText(this, "Sign in failed: ${errorMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        locationButton.setOnClickListener {
            analyticsHelper.logEvent("location_button_clicked")
            checkLocationPermissionAndRequestLocation()
        }

        analyticsButton.setOnClickListener {
            analyticsHelper.logEvent("analytics_button_clicked", mapOf(
                "button_color" to "blue",
                "session_id" to System.currentTimeMillis().toString()
            ))

            // Show interstitial ad when analytics button is clicked
            if (adHelper.shouldShowAds() && adHelper.isInterstitialAdLoaded()) {
                adHelper.showInterstitialAd {
                    analyticsHelper.logEvent("interstitial_closed_after_analytics_click")
                }
            } else {
                Toast.makeText(this, "Enhanced analytics event logged", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissionAndRequestLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            locationHelper.requestCurrentLocation()
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    analyticsHelper.logEvent("location_permission_granted")
                    locationHelper.requestCurrentLocation()
                } else {
                    analyticsHelper.logEvent("location_permission_denied")
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        locationHelper.removeLocationUpdates()
        adHelper.destroyBannerAd()
        super.onDestroy()
    }
}