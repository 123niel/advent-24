plugins {
    kotlin("jvm") version "2.1.0"
//    id("org.jetbrains.kotlin.jvm") version "2.1.0"
}

group = "de.niel123"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.toldoven.aoc:aoc-kotlin-notebook:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

}

sourceSets {
    main {
        kotlin.srcDirs("src")
    }
}