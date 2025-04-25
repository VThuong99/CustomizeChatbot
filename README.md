# Customize Chatbot

This is a simple Android application that allows you to chat with an AI chatbot with a customizable persona, currently set as "Tom" - a friendly and empathetic companion. The chatbot is powered by the Google Gemini API.

This project was built as a basic example of how to integrate the Gemini API into an Android application using Java, Volley, and RecyclerView.

## Features

*   Simple and familiar chat interface.
*   AI responses from Google Gemini.
*   Pre-defined chatbot persona (Tom).
*   **Easy customization** to change the chatbot's personality and name.

## Screenshots 

![startScreen](docs/images/img.png)
 ![chatScreen](docs/images/img_1.png)

## Installation and Setup

1.  **Clone the Repository:**
    ```bash
    git clone git@github.com:VThuong99/CustomizeChatbot.git
    cd CustomizeChatbot
    ```

2.  **Open the project in Android Studio:**
    *   Open Android Studio.
    *   Select "Open" or "Open an Existing Project".
    *   Navigate to the project directory you just cloned and select it.
    *   Wait for Android Studio to sync Gradle.

3.  **Configure the API Key (Important):**
    This project uses `BuildConfig` to access the API Key securely without hardcoding it in the source code.
    *   **Find the `local.properties` file:** In the root directory of the project (same level as `app`, `gradle`). If the file doesn't exist, create it.
    *   **Add your API Key to `local.properties`:** Open the `local.properties` file and add the following line at the end (replace `YOUR_API_KEY_HERE` with your actual API Key):
        ```properties
        geminiApiKey="YOUR_API_KEY_HERE"
        ```
        **Note:** The `local.properties` file is typically configured in `.gitignore` to prevent it from being committed to Git. Do not commit this file!
    *   **Check `app/build.gradle.kts`:** Ensure this file has the following configurations to read the key from `local.properties` and add it to `BuildConfig`:
        *   The `getLocalProperty` function at the top of the file.
        *   The `buildFeatures { buildConfig = true }` block inside the `android { ... }` block.
        *   The `buildConfigField("String", "GEMINI_API_KEY", getLocalProperty("geminiApiKey"))` line inside both the `release { ... }` and `debug { ... }` blocks within `buildTypes`.
            *(Review the `app/build.gradle.kts` file in the code you provided; it already includes these parts)*.
    *   For simpler: U can use *hardcode*, create a variable and assign ApiKey into it. 

4.  **Sync Gradle:**
    *   Click the "Sync Project with Gradle Files" icon (elephant icon) in the Android Studio toolbar or go to **File > Sync Project with Gradle Files**.
    *   Wait for the sync process to complete without errors.

## Running the App

1.  **Select Device:** Choose a connected physical Android device (with USB/Wireless Debugging enabled) or an Emulator from the dropdown list in the toolbar.
2.  **Press the Run button:** Click the "Run 'app'" icon (green triangle) or go to **Run > Run 'app'**.
3.  Android Studio will build, install, and launch the application on your selected device.
