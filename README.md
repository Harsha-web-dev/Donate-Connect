📱 DonationConnect – Android App (Jetpack Compose)

DonationConnect is a modern Android application built using Jetpack Compose and Material 3, designed to simplify the process of donating and requesting essential items.
The app offers a smooth user experience with clean UI, efficient navigation, and role-based features.

✨ Features
🔐 Authentication

User Login

User Registration (Donator / Receiver)

🏠 Home & Navigation

Home browsing with categories

Search items by keywords & category

Item detail page with contact information

🎁 Donator Features

Post new donation items

Manage posted items in profile

🙏 Receiver Features

Request items

View request history in profile

👤 Profile Management

User information

Stats section

Donation/request history

Logout

🛠️ Technology Stack

Kotlin

Jetpack Compose

Material 3

State Management: remember, mutableStateOf

In-memory data storage for users and items

📂 Project Structure
app/
 ├── manifests/
 │    └── AndroidManifest.xml
 │
 ├── java/
 │    └── com.example.donationconnect/
 │         ├── MainActivity.kt
 │         ├── (All screens and data models are inside this file)
 │         └── ui/theme/
 │              ├── Color.kt
 │              ├── Theme.kt
 │              └── Type.kt
 │
 ├── res/
 │    ├── drawable/
 │    ├── values/
 │    └── mipmap/
 │
 ├── test/com.example.donationconnect/
 └── androidTest/com.example.donationconnect/

▶️ Getting Started

Open the project in Android Studio

Allow Gradle to sync

Run on an emulator or physical device

No additional setup required



📸 UI Highlights

Modern Material 3 components

Smooth navigation between screens

Card-based item display

Category chips, dialogs, and dropdowns

Responsive layouts

📌 Notes

Data is stored in-memory during runtime

Ideal for learning Jetpack Compose architecture and UI building

Can be extended with backend/database integration

👨‍💻 Developer

Harsha S
Dheeraj B K
Android Developer | MCA Graduate

Screenshots

![IMG-20251120-WA0011](https://github.com/user-attachments/assets/3c47df87-09ae-4b7d-a62b-827bf2264626)


