plugins {
    java
    application
}

val lwjglVersion = "3.3.4"
val os = org.gradle.internal.os.OperatingSystem.current()

// Determine the correct LWJGL natives for the current platform
val lwjglNatives = when {
    os.isMacOsX -> if (System.getProperty("os.arch") == "aarch64") "natives-macos-arm64" else "natives-macos"
    os.isWindows -> if (System.getProperty("os.arch") == "amd64") "natives-windows" else "natives-windows-x86"
    os.isLinux -> if (System.getProperty("os.arch") == "amd64") "natives-linux" else "natives-linux-arm64"
    else -> throw Error("Unrecognized or unsupported platform.")
}

repositories {
    mavenCentral()
}

dependencies {
    // LWJGL core
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")

    // LWJGL natives for the current platform
    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
}

application {
    mainClass.set("com.slope.game.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
