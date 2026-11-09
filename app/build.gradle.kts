plugins {
    alias(libs.plugins.android.application)
}

val releaseSigningValues = listOf(
    "RELEASE_STORE_FILE",
    "RELEASE_STORE_PASSWORD",
    "RELEASE_KEY_ALIAS",
    "RELEASE_KEY_PASSWORD"
).associateWith { providers.environmentVariable(it).orNull }

fun requiredReleaseSigningValue(name: String): String =
    releaseSigningValues[name]?.takeIf { it.isNotBlank() }
        ?: throw GradleException("Missing release signing environment variable: $name")

android {
    namespace = "me.naptila.neteasemumu"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "me.naptila.neteasemumu"
        minSdk = 28
        targetSdk = 37
        versionCode = providers.gradleProperty("releaseVersionCode").map { it.toInt() }.orElse(1).get()
        versionName = providers.gradleProperty("releaseVersionName").orElse("1.0").get()
    }

    signingConfigs {
        if (releaseSigningValues.values.any { !it.isNullOrBlank() }) {
            create("release") {
                storeFile = rootProject.file(requiredReleaseSigningValue("RELEASE_STORE_FILE"))
                storePassword = requiredReleaseSigningValue("RELEASE_STORE_PASSWORD")
                keyAlias = requiredReleaseSigningValue("RELEASE_KEY_ALIAS")
                keyPassword = requiredReleaseSigningValue("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            signingConfigs.findByName("release")?.let { signingConfig = it }

            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    compileOnly(libs.xposed)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
}
