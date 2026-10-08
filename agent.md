# Project Agent Context & Notes

This file serves as a persistent reference for project information, requirements, use cases, guidelines, notes, and context provided during development.

---

## Project Overview
- **Project Name:** FitSpot
- **Root Directory:** 
- **Course:** CSE 5236 - Mobile App Dev
- **Team Members (Group 5):**
  - John 
  - Dylan 
  - Jake 

---

## Project Narrative
The primary target audience for this application is the everyday fashion-conscious consumer who frequently discovers inspiring outfits or clothing items in public or social media. The consumer’s primary goal is to recreate these looks, but they often face a barrier in identifying specific brands or locating where to purchase similar pieces. The user requires an efficient tool to instantly identify apparel and locate accessible purchasing options, eliminating the need for manual, keyword-based internet searches.

To utilize this system, the user downloads the application and creates a User account. Upon discovering an inspiring outfit or clothing item, the user opens the application and accesses the device’s camera (an on-device hardware sensor) to capture an Image. Alternatively, the user may upload a saved Image from their device’s local storage (like a screenshot of an outfit online). The application then transmits this Image to an image recognition service (such as Google Cloud Vision API). This internet-based service scans photos, isolates individual ClothingItems, and categorizes them, such as jackets, shirts, or footwear.

Following the scan, the application presents the user with a detailed breakdown of the detected apparel. Leveraging an external shopping API (an internet-based service), the system queries a database of retailers to generate direct PurchaseLinks for exact matches and visually similar alternatives. Furthermore, this application can access the device’s location sensor to suggest nearby brick-and-mortar Retailers with available inventory. Finally, the user can save favorited items to a DigitalWardrobe within their account, successfully bridging the gap between visual inspiration and retail checkout.

---

## Categorized Use Cases

### 1. Account Management
- **Create Account:** User clicks "New Account", enters email and password. App hashes password via SHA-256 and creates account in DB.
- **Login:** User enters email and password, clicks "Login". App compares email and SHA-256 password hash against DB records. On match, navigates to home screen; otherwise displays error message.
- **Reset Password:** User clicks "Forgot Password", enters email. App sends recovery link. When link is followed and new password submitted, app updates stored hash.
- **Logout:** User taps Profile icon, selects "Logout". App clears active session token and returns to Login screen.

### 2. Outfit Capture & Scanning
- **Capture Outfit Photo:** User taps Camera button. App requests camera permission. Viewfinder opens, user taps shutter to take photo, and app presents image for review.
- **Upload Saved Image:** User taps Gallery button. App requests storage permission, opens photo picker, and user selects an existing image.
- **Crop Image:** App presents bounding box after image upload/capture. User can drag corners to isolate specific clothing items or select "Don't Crop" to scan full outfit.
- **Identify Clothing Items:** User taps Scan button. Image is sent to external Image Recognition Service (e.g., Google Cloud Vision API). App overlays labeled bounding boxes on detected apparel pieces (e.g., "Denim Jacket", "Sneakers").

### 3. Shopping & Recommendations
- **View Purchase Links:** Tapping a labeled clothing item queries external Shopping API. Displays a scrollable list of exact matches and similar products with retailer name, price, and purchase link.
- **Follow Purchase Link:** Clicking a product recommendation opens device's default web browser to retailer's product page for checkout.

### 4. Digital Wardrobe
- **Save to Digital Wardrobe:** Tapping Heart icon next to item/outfit saves metadata and image URL to user's profile in database, showing a "Saved to Wardrobe" toast.
- **View Wardrobe:** Opening Wardrobe tab retrieves saved items/outfits from database and renders them in a grid layout.
- **Remove from Digital Wardrobe:** Clicking 'X' icon on saved wardrobe item prompts for confirmation and deletes the database record upon confirmation.

### 5. Mapping & Store Discovery
- **Find Nearby Stores:** Clicking "Nearby Stores" requests GPS location permission, queries Shopping API for local inventory, and displays nearby retail locations on a map.
- **View Store Details:** Tapping a map pin pops up store name, distance, and estimated in-stock status.
- **Navigate to Store:** Clicking "Get Directions" hands destination coordinates to native map app (Google Maps/Apple Maps) for turn-by-turn navigation.

---

## Architectural & Design Artifacts Reference
- **ER Diagram:** Database schema (User, Digital Wardrobe, Clothing Items, Retailers, Links).
- **UML Diagram:** Class and architecture diagrams.
- **Storybook Sketch / Wireframes:** Screen layouts and UI mockups.
- **Sequence Diagram:** Outfit scanning flow.
