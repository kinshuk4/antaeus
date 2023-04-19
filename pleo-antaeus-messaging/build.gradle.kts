plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("org.apache.kafka:kafka-clients:3.3.2")
    api(project(":pleo-antaeus-util"))
    implementation(project(":pleo-antaeus-core"))
}

