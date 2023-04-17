plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":pleo-antaeus-core"))
    implementation(project(":pleo-antaeus-models"))
    implementation(project(":pleo-antaeus-util"))
}
