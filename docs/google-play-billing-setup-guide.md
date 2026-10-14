# Google Play Console: In-App Products Configuration Guide
**Application:** ContentGen AI Pro (`AI pro 001`)  
**Package Name:** `com.aistudio.contentgenai.vyrkzp`  
**Billing Library:** Google Play Billing Library v7.1.1 (`billing-ktx`)

This guide walks you through setting up, configuring, and testing one-time in-app products in the **Google Play Console** for ContentGen AI Pro.

---

## 1. Prerequisites in Google Play Console
1. **Google Play Developer Account**: Ensure you have an active Google Play Developer account and merchant profile configured under **Settings > Developer account > Payment settings**.
2. **Draft/Internal Release**: Google Play requires at least one APK/AAB containing the `com.android.vending.BILLING` permission uploaded to an active testing track (e.g., **Internal testing** or **Closed testing**).
3. **License Testers**: Add your test Gmail accounts under **Settings > License testing** with license test response set to `RESPOND_NORMALLY`.

---

## 2. In-App Products to Create in Play Console
Navigate to:
> **Monetize > Products > In-app products > Create product**

Create each of the following 3 products exactly with the matching **Product ID**:

### Product 1: Pro Plan Lifetime Access
* **Product ID:** `pro_lifetime_access`
* **Name:** Pro Plan Lifetime Access
* **Description:** Unlimited viral hooks, AI video scripts, teleprompter, and full multi-platform scheduler.
* **Type:** In-app product (Non-consumable / One-time)
* **Default Price:** `$19.99` (or your preferred local pricing)
* **Status:** Set to **Active**

### Product 2: 100 AI Generation Credits Pack
* **Product ID:** `credits_100_pack`
* **Name:** 100 AI Generation Credits
* **Description:** Refill pack of 100 fast generative AI tokens for viral video scripts and sales copy.
* **Type:** In-app product (Consumable)
* **Default Price:** `$4.99` (or your preferred local pricing)
* **Status:** Set to **Active**

### Product 3: Business VIP Creator Pass
* **Product ID:** `business_creator_pass`
* **Name:** Business VIP Creator Pass
* **Description:** Agency workspace, brand kits, white-label exports, and priority cloud processing.
* **Type:** In-app product (Non-consumable / One-time)
* **Default Price:** `$49.99` (or your preferred local pricing)
* **Status:** Set to **Active**

---

## 3. How the App Handles Purchases (Life Cycle)

### 1. Product Discovery
When the app launches, `PlayBillingManager` connects to the Google Play Billing service via `BillingClient.startConnection`. Once connected, it queries Google Play with `QueryProductDetailsParams` for the product IDs above and loads official localized pricing and descriptions.

### 2. Purchase Flow Launch
When the user taps **Buy** or **Upgrade**, `PlayBillingManager.launchPurchaseFlow(activity, productId)` opens the native Google Play bottom sheet.

### 3. Verification & Security (Requirement 7)
When the user completes payment:
* Google Play triggers `PurchasesUpdatedListener.onPurchasesUpdated`.
* The purchase state is inspected:
  * If `PURCHASED`: The purchase is validated locally (signature presence, matching package name, and non-empty token).
  * If `PENDING`: A pending event is emitted (`BillingPurchaseEvent.Pending`). **No premium entitlement is granted yet.**
  * If `USER_CANCELED`: Safely dismissed without modifying user state.

### 4. Acknowledgement & Consumption (Requirement 5)
* **For Consumables (`credits_100_pack`):**
  * The app calls `BillingClient.consumePurchase(consumeParams)`.
  * Only when the consumption response is `OK`, `AuthManager.addCredits(100)` is called.
* **For Non-Consumables (`pro_lifetime_access`, `business_creator_pass`):**
  * If not yet acknowledged, the app calls `BillingClient.acknowledgePurchase(acknowledgeParams)`.
  * Once acknowledged, the entitlement is saved to SharedPreferences, `AuthManager.updatePlanTier(...)` is triggered, and `viewModel.setPlanTier(...)` updates the active tier reactively.

### 5. Restoring Purchases (Requirement 5)
* When the user taps **Restore Google Play Purchases** or opens the app on a new device, `PlayBillingManager.restorePurchases()` queries Google Play with `queryPurchasesAsync`.
* Any unacknowledged purchases are re-acknowledged and active entitlements are immediately restored.

---

## 4. Testing with Google Play License Testers
1. Add your Google email address to **License Testers** in Google Play Console.
2. Under **License test response**, choose `RESPOND_NORMALLY` (to test standard payment card or test card) or `TEST_PURCHASE_CONFIRMED` (to test instant free test purchases).
3. Under **Slow test responses**, you can simulate **Pending Purchases** (e.g. cash payments or delayed banking approvals).
4. Download the test build from the **Internal testing** opt-in URL on your Android device.
