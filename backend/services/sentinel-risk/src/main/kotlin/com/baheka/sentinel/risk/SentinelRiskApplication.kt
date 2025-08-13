package com.baheka.sentinel.risk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableKafka
@EnableAsync
class SentinelRiskApplication

fun main(args: Array<String>) {
    runApplication<SentinelRiskApplication>(*args)
}
