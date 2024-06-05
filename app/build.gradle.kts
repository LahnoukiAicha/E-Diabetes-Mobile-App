plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.slfb"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.slfb"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures{
        viewBinding=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.fragment)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.firebase.auth)
    implementation (libs.glide)
    implementation (libs.picasso)
    implementation (libs.firebase.messaging)
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")





}
