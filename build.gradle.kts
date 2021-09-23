buildscript {
    val kotlinVersion: String by project

    repositories {
        gradlePluginPortal()
        google()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

plugins {
    base
}

allprojects {
    repositories {
        jcenter()
        google()
	    mavenCentral()
    }
}

tasks.clean {
    delete(buildDir)
}
