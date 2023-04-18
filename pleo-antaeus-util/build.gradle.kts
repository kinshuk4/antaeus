plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation("dev.inmo:krontab:0.7.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("org.apache.kafka:kafka-clients:3.3.2")
}
