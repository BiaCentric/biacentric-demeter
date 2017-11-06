package com.biacode.biacentric.demeter.repository

import com.biacode.biacentric.demeter.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

/**
 * Created by Arthur Asatryan.
 * Date: 11/7/17
 * Time: 1:12 AM
 */
@Repository
interface UserRepository : ReactiveMongoRepository<User, ObjectId> {
    fun findByEmail(email: String): Mono<User>
}