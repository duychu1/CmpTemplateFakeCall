
package com.common.control.manager

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.billingclient.api.*
import com.android.billingclient.api.PendingPurchasesParams
import com.common.control.interfaces.PurchaseCallback
import com.common.control.model.PurchaseModel
import java.time.Period

class PurchaseManagerKt private constructor() {
    var isTrial = false
    private val purchaseList = mutableListOf<Purchase>()
    private var productDetailsList: List<ProductDetails>? = null
    private var callback: PurchaseCallback? = null
    private lateinit var billingClient: BillingClient
    private var purchaseModelList: List<PurchaseModel>? = null

    private val acknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener { _ ->
        queryPurchase()
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                handlePurchase(purchase)
            }
            callback?.purchaseSuccess()
        } else {
            callback?.purchaseFail()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PurchaseManagerKt? = null

        fun getInstance(): PurchaseManagerKt {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PurchaseManagerKt().also { INSTANCE = it }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertBillingPeriodToDays(inputBillingPeriod: String): String {
            val period = Period.parse(inputBillingPeriod)
            val days = period.days
            return "$days Days"
        }
    }

    fun setCallback(callback: PurchaseCallback) {
        this.callback = callback
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener)
            }
        }
    }

    fun init(context: Context, purchaseModelList: List<PurchaseModel>) {
        this.purchaseModelList = purchaseModelList
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .build()
        connectGooglePlay()
    }

    private fun connectGooglePlay() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchase()
                    queryProductDetails()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun queryProductDetails() {
        val productList = mutableListOf<QueryProductDetailsParams.Product>()
        purchaseModelList?.forEach { purchaseModel ->
            val product = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(purchaseModel.productId)
                .setProductType(purchaseModel.type)
                .build()
            productList.add(product)
        }

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { _, productDetailsList ->
            this.productDetailsList = productDetailsList.productDetailsList
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBillingPeriod(productId: String): String {
        productDetailsList?.forEach { productDetails ->
            if (productDetails.productId == productId) {
                val billingPeriod = productDetails.subscriptionOfferDetails
                    ?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.billingPeriod
                billingPeriod?.let {
                    return convertBillingPeriodToDays(it)
                }
            }
        }
        return ""
    }

    private fun queryPurchase() {
        purchaseList.clear()
        
        var param = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(param) { _, purchases ->
            purchaseList.addAll(purchases)
        }

        param = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(param) { _, purchases ->
            purchaseList.addAll(purchases)
        }
    }

    fun consume(productId: String) {
        val purchase = getPurchase(productId)
        purchase?.let {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(it.purchaseToken)
                .build()

            val listener = ConsumeResponseListener { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("android_log", "onConsumeResponse: OK")
                    queryPurchase()
                } else {
                    Log.d("android_log", "onConsumeResponse: Failed")
                }
            }

            billingClient.consumeAsync(consumeParams, listener)
        }
    }

    private fun getPurchase(productId: String): Purchase? {
        purchaseList.forEach { purchase ->
            try {
                if (purchase.products[0] == productId) {
                    return purchase
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun isPurchased(): Boolean {
        if (!AdmobManagerKt.getInstance().hasAds) {
            return true
        }
        purchaseList.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                return true
            }
        }
        return false
    }

    fun launchPurchaseSubs(activity: Activity, productId: String) {
        val productDetails = getProductDetail(productId)
        if (productDetails == null) {
            callback?.purchaseFail()
            queryPurchase()
            queryProductDetails()
            return
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(productDetails.subscriptionOfferDetails?.get(0)?.offerToken ?: "")
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        Log.d("android_log", "launchPurchase: ${billingResult.debugMessage}")
    }

    fun launchPurchaseInApp(activity: Activity, productId: String) {
        val productDetails = getProductDetail(productId)
        if (productDetails == null) {
            callback?.purchaseFail()
            queryPurchase()
            queryProductDetails()
            return
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        Log.d("android_log", "launchPurchase: ${billingResult.debugMessage}")
    }

    private fun getProductDetail(productId: String): ProductDetails? {
        productDetailsList?.forEach { productDetails ->
            if (productDetails.productId == productId) {
                return productDetails
            }
        }
        return null
    }

    fun getPriceSub(productId: String): String {
        productDetailsList?.forEachIndexed { index, productDetails ->
            if (productDetails.productId == productId) {
                return productDetails.subscriptionOfferDetails
                    ?.get(index)?.pricingPhases?.pricingPhaseList?.get(index)?.formattedPrice ?: ""
            }
        }
        return ""
    }

    fun getPriceInApp(productId: String): String {
        productDetailsList?.forEach { productDetails ->
            if (productDetails.productId == productId) {
                return productDetails.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
            }
        }
        return ""
    }
}
