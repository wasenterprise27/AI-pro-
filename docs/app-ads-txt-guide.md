# Google AdMob Authorized Digital Sellers (app-ads.txt) Guide

## 1. Application & Publisher Identifiers
* **Application ID**: `ca-app-pub-2572430667748626~1973658266`
* **Publisher Account**: `pub-2572430667748626`
* **Package**: `com.aistudio.contentgenai.vyrkzp`

## 2. Authorized Record
```text
google.com, pub-2572430667748626, DIRECT, f08c47fec0942fa0
```

### Breakdown of Fields:
| Field | Value | Description |
|---|---|---|
| **Domain of the Ad System** | `google.com` | Canonical domain for Google AdMob and Google Ad Manager systems. |
| **Publisher Account ID** | `pub-2572430667748626` | Your unique Google AdMob publisher account identifier. |
| **Relationship** | `DIRECT` | Indicates that you, as the app developer/publisher, directly control the ad inventory and account. |
| **Certification Authority ID** | `f08c47fec0942fa0` | Google's Trustworthy Accountability Group (TAG) ID. |

---

## 2. Where to Host `app-ads.txt`

1. **Root Domain Requirement**:
   Google's crawler fetches the developer website URL declared in your **Google Play Console** app listing:
   * Go to **Google Play Console** > **Store presence** > **Store settings** > **Website**.
   * If your website is `https://example.com`, the file MUST be accessible at:
     ```
     https://example.com/app-ads.txt
     ```
   * If your website is `https://www.example.com`, it must be accessible at:
     ```
     https://www.example.com/app-ads.txt
     ```

2. **File Format & MIME Type**:
   * Text file encoded in **UTF-8**.
   * Content-Type: `text/plain`.
   * HTTP status code must be **200 OK** (not redirected via 301/302 to external sites).

---

## 3. How to Verify in Google AdMob Console

1. Sign in to your AdMob account: [https://admob.google.com](https://admob.google.com).
2. In the sidebar, navigate to **Apps** > **View all apps**.
3. Click the **app-ads.txt** tab at the top.
4. Verify your app status:
   * **Authorized**: Your `app-ads.txt` is verified and active.
   * **Crawled, but needs attention**: Ensure there are no typos in `pub-2572430667748626`.
   * **Not found**: Ensure your developer website in Google Play Console is reachable and public.
5. Click **Check for updates** to trigger an on-demand crawl.

---

## 4. In-App Integration
* **Asset Location**: `app/src/main/assets/app-ads.txt`
* **Root Location**: `/app-ads.txt` and `/docs/app-ads.txt`
* **In-App Management**: Go to **Monetization & Ad Revenue Suite** > **app-ads.txt Authorized Seller** card to view, copy, or update identifiers dynamically.
