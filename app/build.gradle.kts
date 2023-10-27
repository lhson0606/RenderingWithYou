plugins {
    id("com.android.application")
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
}