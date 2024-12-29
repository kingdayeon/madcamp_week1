//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//}
//
//android {
//    namespace = "com.example.myapplication_test1"
//    compileSdk = 34
//
//    defaultConfig {
//        applicationId = "com.example.myapplication_test1"
//        minSdk = 24
//        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//    buildFeatures {
//        viewBinding = true
//    }
//}
//
//dependencies {
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
//    implementation(libs.androidx.constraintlayout)
//    implementation(libs.androidx.lifecycle.livedata.ktx)
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    implementation(libs.androidx.navigation.fragment.ktx)
//    implementation(libs.androidx.navigation.ui.ktx)
//    implementation("androidx.recyclerview:recyclerview:1.3.1")
//    implementation("io.coil-kt:coil:2.5.0")
//    implementation(libs.play.services.maps)
//    implementation ("com.google.android.libraries.places:places:3.3.0")
//    implementation(libs.play.services.places)
//    implementation(libs.androidx.room.compiler)
////    implementation(libs.androidx.media3.common.ktx)  // Coil 추가
//    implementation ("org.jetbrains:annotations:23.0.0")  // 최신 버전 사용
//
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.myapplication_test1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication_test1"
        minSdk = 24
        targetSdk = 34
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

configurations {
    implementation {
        exclude(group = "com.intellij", module = "annotations")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("io.coil-kt:coil:2.5.0")
    implementation(libs.play.services.maps)
    implementation ("com.google.android.libraries.places:places:3.3.0")
    implementation(libs.play.services.places)
    implementation(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}