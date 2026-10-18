plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // Provided at runtime by the app that loads the patches (Morphe Manager / CLI),
    // so it must not be bundled into patches.mpp.
    compileOnly(files("$rootDir/libs/morphe-patcher-1.15.1.jar"))

    // SMALI (asm/dexlib2) is also provided by the loading app; for compilation it can
    // be resolved from JitPack like any other library.
    compileOnly("com.github.MorpheApp.smali:smali-dexlib2:d856bad65f")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}
