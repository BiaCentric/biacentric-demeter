package com.biacode.biacentric.demeter.controller

import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

/**
 * Created by Arthur Asatryan.
 * Date: 11/6/17
 * Time: 11:55 PM
 */
@RestController
@RequestMapping("/demo")
class DemoController {

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

    @GetMapping("/error")
    fun throwAnError(): Mono<String> {
        return Mono.error(IllegalArgumentException("Unexpected error occur..."))
    }

}