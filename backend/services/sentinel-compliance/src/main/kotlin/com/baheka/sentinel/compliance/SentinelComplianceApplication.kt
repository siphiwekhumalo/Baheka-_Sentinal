package com.baheka.sentinel.compliance

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableKafka
@EnableAsync
class SentinelComplianceApplication

fun main(args: Array<String>) {
    runApplication<SentinelComplianceApplication>(*args)
}
