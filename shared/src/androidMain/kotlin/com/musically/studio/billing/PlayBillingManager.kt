package com.musically.studio.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

/**
 * Manages the Google Play Billing lifecycle for Mave Studio subscriptions.
 *
 * ### Source of truth for subscription status
 * [currentProductId] reflects whichever subscription is currently ACTIVE and ACKNOWLEDGED
 * on the device, derived from Play's own purchase records — not from the app's local DB.
 * This means it survives app restarts and is valid even if the backend webhook hasn't fired yet.
 *
 * ### Server-side enforcement
 * Client-side quota checks driven by [currentProductId] are UX guardrails only.
 * True entitlement enforcement (preventing abuse on rooted devices, etc.) must be implemented
 * as a server-side check in the Cloud Function or API middleware layer before generation calls.
 * This is a known blocker flagged in the implementation plan and not resolved here.
 */
class PlayBillingManager(
    private val context: Context,
    /** Invoked on the calling thread whenever a new purchase is acknowledged successfully. */
    private val onPurchaseAcknowledged: (productId: String) -> Unit = {},
) : PurchasesUpdatedListener {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails: StateFlow<List<ProductDetails>> = _productDetails.asStateFlow()

    /**
     * The Play product ID of the user's currently active subscription, or null if none.
     * Updated on connect (queryActivePurchases) and after each purchase acknowledgement.
     * Drives [SubscriptionTierLimits.forProductId] throughout the app.
     */
    private val _currentProductId = MutableStateFlow<String?>(null)
    val currentProductId: StateFlow<String?> = _currentProductId.asStateFlow()

    init {
        connectToBilling()
    }

    private fun connectToBilling() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("BillingClient connected successfully")
                    queryProductDetails()
                    queryActivePurchases()
                } else {
                    Timber.e("BillingClient setup failed: ${billingResult.debugMessage}")
                }
            }
            override fun onBillingServiceDisconnected() {
                Timber.w("BillingClient disconnected, will retry...")
                connectToBilling()
            }
        })
    }

    /**
     * Triggers a manual sync of active purchases.
     */
    fun restorePurchases() {
        queryActivePurchases()
    }

    /**
     * Queries Play for any currently active (PURCHASED + ACKNOWLEDGED) subscription and
     * updates [currentProductId]. Called on every successful billing client connection so
     * users who subscribed in a previous session are recognised immediately.
     */
    private fun queryActivePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Timber.e("queryPurchasesAsync failed: ${billingResult.debugMessage}")
                return@queryPurchasesAsync
            }
            // Find the first active, acknowledged subscription among all purchases
            val activePurchase = purchases
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && it.isAcknowledged }
                .maxByOrNull { it.purchaseTime }

            val activeProductId = activePurchase?.products?.firstOrNull()
            _currentProductId.value = activeProductId
            Timber.d("Active subscription product: $activeProductId")
        }
    }

    private fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_basic")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_pro")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_ultra")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        billingClient.queryProductDetailsAsync(params) { billingResult, result ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = result.productDetailsList ?: emptyList()
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productId: String) {
        val productDetails = _productDetails.value.find { it.productId == productId }
        if (productDetails == null) {
            Timber.e("Product details not found for $productId")
            return
        }
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { handlePurchase(it) }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.i("User canceled purchase")
            }
            else -> {
                Timber.e("Purchase error: ${billingResult.debugMessage}")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) {
            // Already acknowledged — just make sure our state reflects it
            purchase.products.firstOrNull()?.let { productId ->
                _currentProductId.value = productId
            }
            return
        }
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productId = purchase.products.firstOrNull()
                Timber.d("Purchase acknowledged successfully for token ${purchase.purchaseToken.take(10)}... productId: $productId")
                if (productId != null) {
                    _currentProductId.value = productId
                    onPurchaseAcknowledged(productId)
                }
            } else {
                Timber.e("Acknowledgement failed: ${billingResult.debugMessage}")
            }
        }
    }
}
