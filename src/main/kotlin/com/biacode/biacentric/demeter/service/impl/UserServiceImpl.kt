package com.biacode.biacentric.demeter.service.impl

import com.biacode.biacentric.demeter.model.User
import com.biacode.biacentric.demeter.repository.UserRepository
import com.biacode.biacentric.demeter.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Created by Arthur Asatryan.
 * Date: 11/7/17
 * Time: 1:13 AM
 */
@Service
class UserServiceImpl : UserService {

    //region Dependencies
    @Autowired
    private lateinit var userRepository: UserRepository
    //endregion

    //region Public methods
    override fun create(email: String, password: String): Mono<User> = userRepository
            .save(User(email, password))

    override fun getByEmail(email: String): Mono<User> = userRepository
            .findByEmail(email)

    override fun getAll(): Flux<User> = userRepository
            .findAll()
    //endregion
}