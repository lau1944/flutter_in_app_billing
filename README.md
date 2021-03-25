# flutter_in_app_billing

A Flutter Plugin for google play in app billing

## Getting Started

### Init

Init billing processor before any operation begins

`licenseKey` your google play license key

```dart

  void main() async {
    FlutterInAppBilling.initBp(yourlicenseKey);
    runApp(MyApp());
}

```

### Check purchase/subscribe

`productId` your google play product id

check if the user had purchased/subscribed

```dart
  FlutterInAppBilling.checkPurchase(productId);
  FlutterInAppBilling.checkSubscription(productId);
```

### Purchase/Subscribe

```dart
  FlutterInAppBilling.purchase(productId);
  FlutterInAppBilling.subscribe(productId);

```

### Get Purchase Info
```dart
  FlutterInAppBilling.getPurchaseInfo(productId);
```