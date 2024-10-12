plugins {
    java
    kotlin("jvm") version "1.6.10"
    application
}

val lwjglVersion = "3.3.0"
val os = org.gradle.internal.os.OperatingSystem.current()

// Determine the correct LWJGL natives for the current platform
val lwjglNatives = when {
    os.isMacOsX -> "natives-macos-arm64"
    os.isWindows -> if (System.getProperty("os.arch") == "amd64") "natives-windows" else "natives-windows-x86"
    os.isLinux -> if (System.getProperty("os.arch") == "amd64") "natives-linux" else "natives-linux-arm64"
    else -> throw Error("Unrecognized or unsupported platform.")
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/it.unimi.dsi/fastutil
    implementation("it.unimi.dsi:fastutil:8.2.1")

    // Add Apache Commons Lang3 library
    implementation(kotlin("stdlib"))

    // Add Java OpenGL Math
    implementation("org.joml:joml:1.10.5")

    // LWJGL core
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")

    // Assimp library
    implementation("org.lwjgl:lwjgl-assimp:$lwjglVersion")

    // LWJGL natives for the current platform
    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")

    // Assimp natives
    runtimeOnly("org.lwjgl:lwjgl-assimp:$lwjglVersion:$lwjglNatives")
    
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("com.slope.game.Main")

    // Initialize an empty list for JVM arguments
    val jvmArgs = mutableListOf<String>()

    // Conditionally add -XstartOnFirstThread if running on macOS
    if (os.isMacOsX) {
        jvmArgs.add("-XstartOnFirstThread")
    }

    // Set the JVM arguments
    applicationDefaultJvmArgs = jvmArgs
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
