plugins {
    kotlin("jvm")
}

val assetsDir = file("./assets")

dependencies {
    val gdxVersion: String by project
    val ktxVersion: String by project

    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")

    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-bullet:$gdxVersion")

    // LibKTX kotlin extensions, optional but recommended.
    // The complete list of modules is available at https://github.com/libktx/ktx
    implementation("io.github.libktx:ktx-async:$ktxVersion")
    implementation("io.github.libktx:ktx-collections:$ktxVersion")
    implementation("io.github.libktx:ktx-math:$ktxVersion")
    implementation("io.github.libktx:ktx-log:$ktxVersion")
    implementation("io.github.libktx:ktx-style:$ktxVersion")

    // If you're using https://github.com/BlueBoxWare/LibGDXPlugin
    // this dependency provides the @GDXAssets annotation.
    compileOnly("com.gmail.blueboxware:libgdxpluginannotations:1.16")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions { jvmTarget = "1.8" }
        sourceCompatibility = "1.8"
    }
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}