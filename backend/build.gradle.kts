plugins {
    id 'org.springframework.boot' version '3.1.5'
    id 'io.spring.dependency-management' version '1.1.3'
    id 'org.jetbrains.kotlin.jvm' version '1.8.22'
    id 'org.jetbrains.kotlin.plugin.spring' version '1.8.22'
    id 'org.jetbrains.kotlin.plugin.jpa' version '1.8.22'
}

allprojects {
    group = 'com.baheka.sentinel'
    version = '1.0.0'
    
    repositories {
        mavenCentral()
        maven { url 'https://repo.spring.io/milestone' }
    }
}

subprojects {
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'
    apply plugin: 'org.jetbrains.kotlin.jvm'
    apply plugin: 'org.jetbrains.kotlin.plugin.spring'
    apply plugin: 'org.jetbrains.kotlin.plugin.jpa'

    java.sourceCompatibility = JavaVersion.VERSION_17

    configurations {
        compileOnly {
            extendsFrom annotationProcessor
        }
    }

    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        implementation 'org.springframework.boot:spring-boot-starter-actuator'
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
        implementation 'org.springframework.boot:spring-boot-starter-data-redis'
        implementation 'org.springframework.boot:spring-boot-starter-security'
        implementation 'org.springframework.boot:spring-boot-starter-validation'
        implementation 'org.springframework.kafka:spring-kafka'
        implementation 'org.springframework.cloud:spring-cloud-starter-gateway:4.0.7'
        implementation 'org.springframework.security:spring-security-oauth2-resource-server'
        implementation 'org.springframework.security:spring-security-oauth2-jose'
        
        // Kotlin
        implementation 'org.jetbrains.kotlin:kotlin-reflect'
        implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'
        implementation 'com.fasterxml.jackson.module:jackson-module-kotlin'
        
        // Database
        implementation 'org.postgresql:postgresql'
        implementation 'org.flywaydb:flyway-core'
        
        // Monitoring & Observability
        implementation 'io.micrometer:micrometer-registry-prometheus'
        implementation 'io.opentelemetry:opentelemetry-api'
        implementation 'net.logstash.logback:logstash-logback-encoder:7.4'
        
        // Utils
        implementation 'org.apache.commons:commons-lang3'
        implementation 'org.apache.commons:commons-csv:1.10.0'
        
        // Documentation
        implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
        
        // Testing
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        testImplementation 'org.springframework.kafka:spring-kafka-test'
        testImplementation 'org.springframework.security:spring-security-test'
        testImplementation 'org.testcontainers:junit-jupiter'
        testImplementation 'org.testcontainers:postgresql'
        testImplementation 'org.testcontainers:kafka'
        testImplementation 'io.mockk:mockk:1.13.8'
        
        annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile) {
        kotlinOptions {
            freeCompilerArgs = ['-Xjsr305=strict']
            jvmTarget = '17'
        }
    }

    tasks.named('test') {
        useJUnitPlatform()
    }
}
