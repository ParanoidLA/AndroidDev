plugins {
    // Apply the Android application plugin
    id("com.android.application")
}

android {
    // Define app namespace
    namespace = "com.example.mantratester"
    compileSdk=34

    defaultConfig {
        // Configure default app settings
        applicationId = "com.example.mantratester"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Disable code minification and obfuscation for release build
            isMinifyEnabled = false
            // Specify ProGuard files
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Set Java source and target compatibility
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Add dependencies from the libs plugin
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.activity:activity:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")

    // Mantra SDK dependency
    implementation(files("libs/mantra.mfs100.aar"))

    // Testing dependencies
    testImplementation("junit:junit:4.+") // Change version as per requirement
    androidTestImplementation("androidx.test.ext:junit:1.1.3") // Change version as per requirement
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0") // Change version as per requirement
}
