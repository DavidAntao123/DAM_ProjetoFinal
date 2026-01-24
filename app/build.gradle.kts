plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "pt.ipt.dam.projfinal"
    compileSdk = 36

    defaultConfig {
        applicationId = "pt.ipt.dam.projfinal"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.2.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // CameraX core library using the camera2 implementation
    // val cameraxVersion = "1.5.1"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    // implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation(libs.androidx.camera.core)
    // implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation(libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    // implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation(libs.androidx.camera.lifecycle)
    // If you want to additionally use the CameraX VideoCapture library
    //    implementation "androidx.camera:camera-video:${camerax_version}"
    // If you want to additionally use the CameraX View class
    // implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation(libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    //       implementation "androidx.camera:camera-mlkit-vision:${camerax_version}"
    // If you want to additionally use the CameraX Extensions library
    // implementation("androidx.camera:camera-extensions:${cameraxVersion}")
    implementation(libs.androidx.camera.extensions)


}