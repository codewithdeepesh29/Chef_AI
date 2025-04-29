<!-- Title & Badges -->
<h1 align="center">👨‍🍳 Chef AI – Smart Recipe Generator</h1>

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" width="100" alt="ChefAI logo" />
</p>

<p align="center">
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack%20Compose-1.5%2B-blue.svg?logo=kotlin" /></a>
  <a href="https://square.github.io/retrofit/"><img src="https://img.shields.io/badge/Retrofit-2.x-orange.svg" /></a>
  <a href="https://kotl.in"><img src="https://img.shields.io/badge/Kotlin-1.9.x-purple.svg" /></a>
</p>

---

## ✨ Overview
Chef AI is an Android application that **turns the ingredients in your fridge into complete recipes** powered by the ChatGPT API.  
Features include:

- **AI recipe generation** (GPT 4o) from an idea + ingredient list  
- **Beautiful recipe card** with image, timings, servings, step-by-step instructions  
- **Offline cache & searchable history** via Room database  
- **Share-sheet integration** – send a recipe to friends or open it in a browser  
- **Dark-mode toggle & dietary preference** stored in DataStore  

<table>
  <tr>
    <td align="center"><img src="docs/screenshots/home_light.png" width="200"/></td>
    <td align="center"><img src="docs/screenshots/recipe_card_dark.png" width="200"/></td>
    <td align="center"><img src="docs/screenshots/search.png" width="200"/></td>
  </tr>
  <tr>
    <td align="center">Home</td>
    <td align="center">Recipe details</td>
    <td align="center">Search saved recipes</td>
  </tr>
</table>

---

## 🧩 Tech Stack

| Layer | Library / Tool |
|-------|----------------|
| UI    | **Jetpack Compose**, Material 3, Coil |
| State | **ViewModel**, Kotlin Coroutines |
| Data  | **Retrofit 2** (ChatGPT API) • **Room** (local cache) • **DataStore** (preferences) |
| DI    | Hilt (optional – see `build.gradle`) |
| Testing | JUnit 5, Turbine (Flow), Robolectric |

---

## 🚀 Getting Started

### 1. Prerequisites
* Android Studio **Giraffe / Hedgehog** or newer  
* JDK 17  
* A GitHub personal-access token (PAT) if cloning privately  

### 2. Clone the repo
```bash
git clone https://github.com/your-username/ChefAI.git
cd ChefAI
