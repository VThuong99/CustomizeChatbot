import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}


fun getLocalProperty(propertyName: String): String {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { input ->
            properties.load(input)
        }
    }
    return properties.getProperty(propertyName) ?: throw IllegalArgumentException("Missing property: $propertyName in local.properties")
}

android {
    namespace = "com.example.customizechatbot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.customizechatbot"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "GEMINI_API_KEY", getLocalProperty("geminiApiKey"))
        }
        debug {
            buildConfigField("String", "GEMINI_API_KEY", getLocalProperty("geminiApiKey"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}