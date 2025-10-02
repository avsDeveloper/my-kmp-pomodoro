pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "my-kmp-pomodoro"

include(":shared")
include(":androidApp")
include(":desktopApp")
