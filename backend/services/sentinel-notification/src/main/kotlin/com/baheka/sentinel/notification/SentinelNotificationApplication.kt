package com.baheka.sentinel.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableKafka
@EnableAsync
class SentinelNotificationApplication

fun main(args: Array<String>) {
    runApplication<SentinelNotificationApplication>(*args)
}
