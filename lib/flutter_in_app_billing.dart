import 'dart:async';

import 'package:flutter/services.dart';

/// A flutter plugin for google play in app billing
/// * all method return map object
class FlutterInAppBilling {
  static const MethodChannel _channel =
  const MethodChannel('flutter_inApp_billing');

  /// Init billing processor before any operation begins
  /// recommend to call this method on main() before runApp()\
  /// [licenseKey] your google play license key
  /// return form
  /// [{ 'result' : true }]// true when init success
  /// [{'result' : false, 'error' : errorMessage}] //false when init error
  static Future initBp(String licenseKey) async {
    return _channel.invokeMethod("init_bp", {"key": licenseKey});
  }

  /// Check if user had purchased
  /// [productId] your product id
  /// return form
  /// [{ 'result' : true }] // true -> user had already purchased, false -> user had not purchased
  static Future checkPurchase(String productId) async {
    return _channel.invokeMethod("check_purchase",
        {'productId': productId});
  }

  /// Check if user had subscribed
  /// [productId] your product id
  /// return form
  /// [{ 'result' : true }] // true -> user had already subscribed, false -> user had not subscribed
  static Future checkSubscription(String productId) async {
    return _channel.invokeMethod("check_subscription",
        {'productId': productId});
  }

  /// Get information of user's purchase status
  /// [productId] your product id
  /// return form
  ///  "result" : true,
  ///  "skuDetails" : {
  ///      "currency" to skuDetails.currency,
  ///       "description" : skuDetails.description,
  ///       "priceText" : skuDetails.priceText,
  ///       "title" : skuDetails.title,
  ///       "subscriptionFreeTrialPeriod" : skuDetails.subscriptionFreeTrialPeriod,
  ///       "introductoryPricePeriod" : skuDetails.introductoryPricePeriod,
  ///       "introductoryPriceText" : skuDetails.introductoryPriceText,
  ///      "subscriptionFreeTrialPeriod" : skuDetails.subscriptionFreeTrialPeriod }
  static Future getPurchaseInfo(String productId) async {
    return _channel.invokeMethod('purchase_info', {
      'productId' : productId
    });
  }

  /// Purchase
  /// return form
  /// ["result" : true,
  ///     "productId" : productId,
  ///     "details" : {
  ///        "responseData" : details?.purchaseInfo?.responseData,
  ///         "developerPayload" : details?.purchaseInfo?.purchaseData?.developerPayload,
  ///          "orderId" : details?.purchaseInfo?.purchaseData?.orderId,
  ///          "purchaseToken" : details?.purchaseInfo?.purchaseData?.purchaseToken,
  ///          "purchaseTime" : details?.purchaseInfo?.purchaseData?.purchaseTime.toString()
  ///                 }] -> User had purchased
  ///
  /// [( "result" : false,
  ///  "error" : error.toString())]
  /// -> User had not purchased
  static Future purchase(String productId) async {
    return _channel.invokeMethod('purchase', {
      'productId' : productId
    });
  }

  /// Subscribe
  /// return form same as [purchase]
  static Future subscribe(String productId) async {
    return _channel.invokeMethod('subscribe', {
      'productId' : productId
    });
  }
}
