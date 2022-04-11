import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    antlr
    application
}

group = "me.ivank"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    antlr("org.antlr:antlr4:4.9.3")
}

tasks {
    test {
        useJUnit()
    }
    withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileKotlin {
        dependsOn(generateGrammarSource)
    }
}

application {
    mainClass.set("MainKt")
}