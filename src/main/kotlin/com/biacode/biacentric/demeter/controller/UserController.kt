package com.biacode.biacentric.demeter.controller

import com.biacode.biacentric.demeter.model.User
import com.biacode.biacentric.demeter.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Created by Arthur Asatryan.
 * Date: 11/7/17
 * Time: 1:17 AM
 */
@RestController
@RequestMapping("/user")
class UserController {

    //region Dependencies
    @Autowired
    private lateinit var userService: UserService
    //endregion

    //region Public methods
    @GetMapping("create")
    fun create(@RequestParam email: String, @RequestParam password: String): Mono<User> = userService
            .create(email, password)

    @GetMapping("by-email")
    fun getByEmail(@RequestParam email: String): Mono<User> = userService
            .getByEmail(email)

    @GetMapping("all")
    fun getAll(): Flux<User> = userService.getAll()
    //endregion

}