package com.biacode.biacentric.demeter.service

import com.biacode.biacentric.demeter.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Created by Arthur Asatryan.
 * Date: 11/7/17
 * Time: 1:13 AM
 */
interface UserService {
    fun create(email: String, password: String): Mono<User>

    fun getByEmail(email: String): Mono<User>

    fun getAll(): Flux<User>
}