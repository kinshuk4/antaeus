plugins {
    kotlin("jvm")
}

kotlinProject()

dataLibs()

dependencies {

    testImplementation(project(":pleo-antaeus-data"))
    api(project(":pleo-antaeus-core"))
    api(project(":pleo-antaeus-models"))
    api(project(":pleo-antaeus-util"))

    testImplementation ("com.github.stefanbirkner:system-lambda:1.2.0")
}
