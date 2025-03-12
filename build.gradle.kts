plugins {
    id("java")
}

group = "me.petrolingus"
version = "1.1.0"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

// Saving the build to the plugins folder
//tasks.jar {
//    archiveBaseName.set("zanuda-infinity-rails")
//    destinationDirectory.set(file("C:\\Users\\petrolingus\\Downloads\\paper-1.21.4-207\\plugins"))
//}