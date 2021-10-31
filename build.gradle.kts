import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
}

group = "me.ppcr3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }

}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.5.1-RC1")
    // https://mvnrepository.com/artifact/org.kohsuke/github-api
    implementation("org.slf4j:slf4j-simple:1.7.30")
//    api("org.kohsuke:github-api:1.133")
    // https://mvnrepository.com/artifact/com.jcabi/jcabi-github
    implementation("com.jcabi:jcabi-github:1.1.2")
    // https://mvnrepository.com/artifact/javax.json/javax.json-api
    implementation("javax.json:javax.json-api:1.1.4")


}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}