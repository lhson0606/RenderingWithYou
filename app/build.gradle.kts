plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dy.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dy.app"
        minSdk = 28
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.compose.animation:animation-core-android:1.5.4")
    testImplementation("junit:junit:4.13.2")

    //JSON
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.airbnb.android:lottie:4.2.0")

    //emoji lib
    //noinspection GradleCompatible
    implementation("com.android.support:support-emoji:28.0.0")
    implementation("com.vanniktech:emoji-google:0.6.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //daimajia carousel
    //https://github.com/daimajia/AndroidImageSlider
    implementation("androidx.legacy:legacy-support-v4:1.0.0"){
        exclude(group = "com.android.support")
    }
    implementation("com.squareup.picasso:picasso:2.3.2")
    implementation("com.nineoldandroids:library:2.4.0")
    implementation("com.daimajia.slider:library:1.1.5@aar")

    //for playing gift
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    // FirebaseUI for Auth
    // FirebaseUI for Firebase Realtime Database
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    // FirebaseUI for Cloud Firestore
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    // FirebaseUI for Firebase Auth
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    // FirebaseUI for Cloud Storage
    implementation("com.firebaseui:firebase-ui-storage:8.0.2")
    //facebook auth
    //implementation("com.facebook.android:facebook-android-sdk:16.2.0")
    implementation ("com.facebook.android:facebook-android-sdk:[8,9)")
    //firebase dynamic link
    implementation ("com.google.firebase:firebase-dynamic-links")
    implementation ("com.google.firebase:firebase-analytics")

    //for graphs
    implementation ("com.github.AnyChart:AnyChart-Android:1.1.5")
}