plugins {
    id("com.android.application")
    id("com.google.gms.google-services") version "4.4.2" apply false
}

android {
    namespace = "com.example.repartidor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.repartidor"
        minSdk = 28
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

    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("com.android.volley:volley:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Api de Drive
    implementation("com.google.android.gms:play-services-auth:21.2.0") // Para autenticación de Google
    implementation("com.google.api-client:google-api-client-android:1.33.0") // Para Google API Client
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0") // Para Google Drive API

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging:24.0.3")
}

apply(plugin = "com.google.gms.google-services")