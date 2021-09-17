plugins {
    kotlin("jvm")
}

val assetsDir = file("../core/assets")
val mainClassName = "quevedo.soares.leandro.kmine.desktop.DesktopLauncher"

dependencies {
    val gdxVersion: String by project

    implementation(project(":core"))

    implementation(kotlin("stdlib"))

    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-tools:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
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

// Use this task to run the game if IntelliJ run application configuration doesn't work.
tasks.register<JavaExec>("run") {
    main = mainClassName
    classpath = sourceSets.main.get().runtimeClasspath
    standardInput = System.`in`
    workingDir = assetsDir
    isIgnoreExitValue = true

    if ("mac" in System.getProperty("os.name").toLowerCase()) {
        jvmArgs("-XstartOnFirstThread", "-Xdock:name=KMine", "-Xdock:icon=${project.projectDir.absolutePath}/core/assets/icon128.png")
    }
}

// Use this task to create a fat jar.
tasks.register<Jar>("dist") {
//    dependsOn(configurations.runtimeClasspath, "classes")

    from(files(sourceSets.main.get().output.classesDirs))
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    from(assetsDir)

    manifest {
        attributes["Main-Class"] = mainClassName
    }
}
