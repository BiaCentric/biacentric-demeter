package com.biacode.biacentric.demeter

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class DemeterApplication

fun main(args: Array<String>) {
    runApplication<DemeterApplication>(*args)
}
