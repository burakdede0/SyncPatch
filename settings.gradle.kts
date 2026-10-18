rootProject.name = "sync-titlefont-patches"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
    }
}

include(":patches")
include(":validator")
