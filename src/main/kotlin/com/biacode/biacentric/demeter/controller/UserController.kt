package com.biacode.biacentric.demeter.controller

import com.biacode.biacentric.demeter.model.User
import com.biacode.biacentric.demeter.repository.exception.UserNotFoundException
import com.biacode.biacentric.demeter.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


/**
 * Created by Arthur Asatryan.
 * Date: 11/7/17
 * Time: 1:17 AM
 */
@RestController
@RequestMapping("user")
class UserController {

    //region Dependencies
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var webClient: WebClient
    //endregion

    //region Public methods
    @GetMapping("create")
    fun create(@RequestParam email: String, @RequestParam password: String): Mono<User> = userService
            .create(email, password)

    @GetMapping("email/{email}")
    fun getByEmail(@PathVariable email: String): Mono<User> = userService
            .getByEmail(email)

    @GetMapping("all")
    fun getAll(): Flux<User> = userService.getAll()

    @ExceptionHandler(UserNotFoundException::class)
    fun handleNotFound(): ResponseEntity<UserNotFoundException> {
        return ResponseEntity.notFound().build()
    }

    @GetMapping("client")
    fun clientRequest(): Flux<*> {
        return webClient.get().uri("https://jsonplaceholder.typicode.com/posts")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve().bodyToFlux(Post::class.java)
    }
    //endregion

    data class Post(val userId: Long, val id: Long, val title: String, val body: String)

}