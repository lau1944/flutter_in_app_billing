package com.vau.flutter_in_app_billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.lang.Exception

/** FlutterInAppBillingPlugin */
class FlutterInAppBillingPlugin: FlutterPlugin, MethodCallHandler,
        ActivityAware, BillingProcessor.IBillingHandler {

  private lateinit var channel: MethodChannel
  private var bp: BillingProcessor? = null
  private lateinit var context: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_inApp_billing")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    methodResult = result

    when (call.method) {
      "init_bp" -> {
        LICENSE_KEY = call.argument("key")
        bp = BillingProcessor(context, LICENSE_KEY, this)
        initBillingProcessor()
      }

      "check_purchase" -> {
        val isBought = checkProduct(call.argument<String>("productId").toString())
        if (bp != null) {
          result.success(hashMapOf("result" to isBought))
        } else {
          result.error("1",
                  "billing processor has not been initialize",
                  null)
        }
      }

      "check_subscription" -> {
        val isSubscribed = checkProduct(call.argument<String>("productId").toString())
        if (bp != null) {
          result.success(hashMapOf("result" to isSubscribed))
        } else {
          result.error("1",
                  "billing processor has not been initialize",
                  null)
        }
      }

      "purchase_info" -> {
        PRODUCT_ID = call.argument<String>("productId").toString()
        getPurchaseInfo(PRODUCT_ID)
      }

      "purchase" -> {
        PRODUCT_ID = call.argument<String>("productId").toString()
        if (!checkProduct(PRODUCT_ID)) {
          bp?.purchase(context as Activity, PRODUCT_ID)
        } else {
          result.success(hashMapOf("result" to "bought"))
        }
      }

      "subscribe" -> {
        PRODUCT_ID = call.argument<String>("productId").toString()
        if (!checkProduct(PRODUCT_ID)) {
          bp?.subscribe(context as Activity, PRODUCT_ID)
        } else {
          result.success(hashMapOf("result" to "subscribed"))
        }
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivity() {
    if (bp != null) {
      bp?.release()
    }
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    context = binding.activity

    binding.addActivityResultListener { requestCode, resultCode, data ->
      bp?.handleActivityResult(requestCode, resultCode, data)
      false
    }
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }

  private fun initBillingProcessor() {
    if (bp != null) {
      bp?.initialize()
    }
  }

  private fun getPurchaseInfo(productId: String?) {
    if (bp != null) {
      val skuDetails = bp?.getPurchaseListingDetails(productId)
      if (skuDetails != null) {
        methodResult?.success(hashMapOf(
                "result" to true,
                "skuDetails" to hashMapOf(
                        "currency" to skuDetails.currency,
                        "description" to skuDetails.description,
                        "priceText" to skuDetails.priceText,
                        "title" to skuDetails.title,
                        "subscriptionFreeTrialPeriod" to skuDetails.subscriptionFreeTrialPeriod,
                        "introductoryPricePeriod" to skuDetails.introductoryPricePeriod,
                        "introductoryPriceText" to skuDetails.introductoryPriceText,
                        "subscriptionFreeTrialPeriod" to skuDetails.subscriptionFreeTrialPeriod
                )))
      } else {
        methodResult?.error("2",
                "skuDetails return null",
                null)
      }
    } else {
      methodResult?.error("1",
              "billing processor has not been initialize",
              null)
    }
  }

  private fun checkProduct(productId: String?): Boolean {
    return bp?.isPurchased(productId) ?: false
  }

  override fun onBillingInitialized() {
    Log.i(TAG, "Billing Processor Initialized")
    methodResult?.success(hashMapOf("result" to true))
  }

  override fun onPurchaseHistoryRestored() {}

  override fun onProductPurchased(productId: String, details: TransactionDetails?) {
    methodResult?.success(hashMapOf(
            "result" to true,
            "productId" to productId,
            "details" to hashMapOf(
                    "responseData" to details?.purchaseInfo?.responseData,
                    "developerPayload" to details?.purchaseInfo?.purchaseData?.developerPayload,
                    "orderId" to details?.purchaseInfo?.purchaseData?.orderId,
                    "purchaseToken" to details?.purchaseInfo?.purchaseData?.purchaseToken,
                    "purchaseTime" to details?.purchaseInfo?.purchaseData?.purchaseTime.toString()
            )))
  }

  override fun onBillingError(errorCode: Int, error: Throwable?) {
    Log.i(TAG, "Billing Processor Error")
    try {
      result.error("2",
              "Billing Processor Error",
              null)
    } catch (e: Exception) {
      Log.i(TAG, e.toString())
    }
  }

  companion object {
    @JvmStatic
    var LICENSE_KEY: String? = ""

    @JvmStatic
    var PRODUCT_ID: String? = ""

    @JvmStatic
    var methodResult: Result? = null

    const val TAG : String = "FlutterInAppBilling"
  }
}
