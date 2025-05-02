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

## ✨ What is Chef AI?

**Chef AI** is an Android app that turns the ingredients you already own into delicious, step-by-step recipes—powered by the ChatGPT 4o model.  
Stop wasting food, find inspiration fast.

| Home → Generate | Recipe Card | Search History |
|:---:|:---:|:---:|


---

## 📋 Table of Contents
1. [Features](#features)
2. [Tech stack](#tech-stack)
3. [Prerequisites](#prerequisites)
4. [Quick start](#quick-start)
5. [Running the project](#running-the-project)

---

## Features

- **AI recipe generation** — GPT 4o creates a full recipe from an idea & fridge contents  
- **Beautiful recipe cards** — ingredients, timings, instructions, AI-generated image  
- **Offline cache** — Room database stores every generated recipe, searchable offline  
- **Dark-mode & dietary preference** — persisted in DataStore   
- **Responsive UI** — works both portrait & landscape

---

## Tech stack

| Layer | Library / Tool |
|-------|----------------|
| UI            | **Jetpack Compose**, Material 3, Coil |
| State & DI    | ViewModel, Kotlin Coroutines, (optional) Hilt |
| Networking    | **Retrofit 2** + OkHttp logging |
| AI backend    | **OpenAI ChatGPT API** |
| Persistence   | **Room** database • **DataStore** (preferences) |

---

## Prerequisites

| Tool | Version |
|------|---------|
| **Android Studio** | Hedgehog | 
| **Gradle**         | Wrapper 8.x (bundled) |
| **JDK**            | 17 |
| **OpenAI API key** | ChatGPT 4o access |

---

## Quick start

```bash
# 1 • Clone the repo
git clone https://github.com/your-username/ChefAI.git
cd ChefAI

# 2 • Add your OpenAI key (local.properties is NOT committed)
echo "OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxx" >> local.properties

# 3 • Open the project
#    Android Studio → File ▸ Open... ▸ select project root

# 4 • Sync & run
#    Click ▶ or use:
./gradlew installDebug
adb shell am start -n "com.example.chefai/.MainActivity"
