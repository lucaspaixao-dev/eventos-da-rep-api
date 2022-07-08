plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.kotlin.kapt") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.4.1"
    id("io.micronaut.library") version "3.4.1"
}

version = "0.1"
group = "io.github.xuenqui"

val kotlinVersion = project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.data:micronaut-data-document-processor")

    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")

    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.apache.logging.log4j:log4j-core:2.18.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    implementation("com.google.firebase:firebase-admin:9.0.0")

    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")

    implementation("io.micronaut.rabbitmq:micronaut-rabbitmq")

    implementation("org.valiktor:valiktor-core:0.12.0")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.mongodb:mongodb-driver-sync")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
}

application {
    mainClass.set("io.github.xuenqui.eventosdarep.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

tasks.register("stage") {
    dependsOn("build")
    dependsOn("clean")

    tasks.findByName("build")?.mustRunAfter("clean")
}

tasks.register("copyToLib", Copy::class) {
    into("$buildDir/libs")
    from("build/libs/eventos-da-rep-api-0.1-all.jar")
}

tasks.findByName("stage")?.dependsOn("copyToLib")

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("io.github.xuenqui.*")
    }
}
