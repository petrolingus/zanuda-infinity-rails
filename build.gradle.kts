plugins {
    id("java")
}

group = "me.petrolingus"
version = "v1.0.0"

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

tasks.jar {
    archiveBaseName.set("zanuda-infinity-rails") // Имя файла
    destinationDirectory.set(file("C:\\Users\\petrolingus\\Downloads\\paper-1.21.4-207\\plugins")) // Кастомная папка
}