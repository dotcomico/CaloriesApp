plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.calories"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.calories"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.google.code.gson:gson:2.11.0")   //לשמירת איברי רשימה כמחלקות בתוך אחסון המכשיר
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0") //להצגת סריקת הבר קוד
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
        //   implementation ("com.android.car.ui:car-ui-lib:2.6.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation  ("com.google.android.material:material:1.4.0")

}