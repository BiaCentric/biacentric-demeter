package com.biacode.biacentric.demeter.controller

import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.zeroturnaround.exec.ProcessExecutor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.time.Duration


/**
 * Created by Arthur Asatryan.
 * Date: 11/6/17
 * Time: 11:55 PM
 */
@RestController
@RequestMapping("/demo")
class DemoController {

    @GetMapping("execute")
    fun execute() {
        ProcessExecutor().command("java", "-version").execute()
    }

    @GetMapping("/hello")
    fun hello(@RequestParam name: String): Mono<String> = "hello $name".toMono()

    @GetMapping("/exchange")
    fun exchange(exchange: ServerWebExchange): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.OK
        response.headers.contentType = MediaType.TEXT_PLAIN
        return response.writeWith(DefaultDataBufferFactory().allocateBuffer().write("Exchange...".toByteArray(Charsets.UTF_8)).toMono())
    }

    @GetMapping("/wait", produces = arrayOf(MediaType.APPLICATION_OCTET_STREAM_VALUE))
    fun waitForIt(): Mono<String> {
        return Mono.never()
    }

    @CrossOrigin
    @GetMapping("/stream", produces = arrayOf(MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE))
    fun withStream(): Flux<Long> {
        return Flux.interval(Duration.ofSeconds(1))
    }

    @GetMapping("/error")
    fun throwAnError(): Mono<String> {
        return Mono.error(IllegalArgumentException("Unexpected error occur..."))
    }

}