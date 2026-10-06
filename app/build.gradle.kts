import java.util.Properties
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    jacoco
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")




}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use(::load)
    }
}

val newsApiKey = localProperties.getProperty("newsApiKey")
    ?: throw GradleException("Missing newsApiKey in local.properties")

android {
    namespace = "com.avanade.devnews"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.avanade.devnews"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "NEWS_API_BASE_URL", "\"https://newsapi.org/\"")
        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
    }

    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            versionNameSuffix = "-dev"
        }
        create("hml") {
            dimension = "environment"
            versionNameSuffix = "-hml"
        }
        create("prod") {
            dimension = "environment"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    testOptions {
        unitTests.all {
            it.extensions.configure(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class.java) {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation("com.google.firebase:firebase-crashlytics")
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.coil.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoDevDebugUnitTestReport") {
    dependsOn("testDevDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val coverageIncludes = listOf(
        "**/data/mapper/*.class",
        "**/domain/usecase/**/*.class",
        "**/feature/news/list/presentation/*ViewModel*.class",
        "**/feature/favorites/presentation/*ViewModel*.class"
    )

    classDirectories.setFrom(
        files(
            fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/devDebug/compileDevDebugKotlin/classes")) {
                include(coverageIncludes)
                exclude(
                    "**/*\$*",
                    "**/BuildConfig.*",
                    "**/R.class",
                    "**/R$*.class"
                )
            },
            fileTree(layout.buildDirectory.dir("intermediates/javac/devDebug/compileDevDebugJavaWithJavac/classes")) {
                include(coverageIncludes)
                exclude(
                    "**/*\$*",
                    "**/BuildConfig.*",
                    "**/R.class",
                    "**/R$*.class"
                )
            }
        )
    )

    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(
        fileTree(buildDir) {
            include(
                "outputs/unit_test_code_coverage/devDebugUnitTest/testDevDebugUnitTest.exec",
                "jacoco/testDevDebugUnitTest.exec"
            )
        }
    )
}