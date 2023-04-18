plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":pleo-antaeus-data"))
    api(project(":pleo-antaeus-models"))
    api(project(":pleo-antaeus-util"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("org.apache.kafka:kafka-clients:3.3.2")
}