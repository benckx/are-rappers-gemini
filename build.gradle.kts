plugins {
    alias(libs.plugins.versions)
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.jackson.datatype.joda)
    implementation(libs.jackson.module.kotlin)

    implementation(libs.commons.text)
    implementation(libs.commons.io)

    implementation(libs.unirest.java)
    implementation(libs.jsoup)
}

application {
    mainClass = "dev.encelade.gemini.MainKt"
}
