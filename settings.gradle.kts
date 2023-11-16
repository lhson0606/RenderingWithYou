pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

//see https://stackoverflow.com/questions/60463052/android-gradle-sync-failed-could-not-resolve-all-artifacts-for-configuration
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven ("https://jitpack.io")
    }
}

rootProject.name = "renderingWithYou"
include(":app")
 