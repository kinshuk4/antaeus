plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {

    testImplementation(project(":pleo-antaeus-data"))
    api(project(":pleo-antaeus-core"))
    api(project(":pleo-antaeus-models"))
    api(project(":pleo-antaeus-util"))


//    implementation(project(":pleo-antaeus-core"))
//    implementation(project(":pleo-antaeus-models"))
//    implementation(project(":pleo-antaeus-util"))
}
