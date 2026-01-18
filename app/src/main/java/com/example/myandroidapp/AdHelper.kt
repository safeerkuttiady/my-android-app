package com.example.myandroidapp

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdHelper(private val context: Context) {
    private val TAG = "AdHelper"
    
    // Banner Ad
    private var bannerAd: AdView? = null
    
    // Interstitial Ad
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoaded = false
    
    // Rewarded Ad
    private var rewardedAd: com.google.android.gms.ads.rewarded.RewardedAd? = null
    private var isRewardedLoaded = false

    fun loadBannerAd(adView: AdView): AdView {
        bannerAd = adView
        val adRequest = AdRequest.Builder().build()
        bannerAd?.loadAd(adRequest)
        return adView
    }

    fun loadInterstitialAd(activityContext: Context, adUnitId: String, onLoaded: (() -> Unit)? = null) {
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            activityContext,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@AdHelper.interstitialAd = interstitialAd
                    isInterstitialLoaded = true
                    Log.d(TAG, "Interstitial ad loaded.")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.d(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    isInterstitialLoaded = false
                }
            })
    }

    fun showInterstitialAd(onClosed: (() -> Unit)? = null) {
        if (isInterstitialLoaded && interstitialAd != null) {
            interstitialAd?.show((context as? android.app.Activity)) { 
                Log.d(TAG, "Interstitial ad closed.")
                isInterstitialLoaded = false
                onClosed?.invoke()
            }
        } else {
            Log.d(TAG, "Interstitial ad is not loaded yet.")
            onClosed?.invoke()
        }
    }

    fun isInterstitialAdLoaded(): Boolean {
        return isInterstitialLoaded
    }

    fun destroyBannerAd() {
        bannerAd?.destroy()
    }

    // Method to check if ads should be shown (for ad-free version consideration)
    fun shouldShowAds(): Boolean {
        // In a real app, this might check if user has purchased ad-free version
        return true
    }

    // Method to get ad request with additional targeting info
    fun getAdRequest(): AdRequest {
        return AdRequest.Builder()
            // Add additional targeting info here if needed
            .build()
    }
}