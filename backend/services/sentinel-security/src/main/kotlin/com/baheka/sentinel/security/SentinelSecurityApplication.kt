package com.baheka.sentinel.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableKafka
@EnableAsync
class SentinelSecurityApplication

fun main(args: Array<String>) {
    runApplication<SentinelSecurityApplication>(*args)
}
