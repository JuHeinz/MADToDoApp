plugins {
    id("com.android.application")
}

android {
    namespace = "org.julheinz.madtodoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.julheinz.madtodoapp"
        minSdk = 31
        targetSdk = 33
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

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.databinding:databinding-runtime:8.2.0")
    // retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.7.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.7.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
}