package com.biacode.biacentric.demeter.config

import com.biacode.biacentric.demeter.model.User
import com.biacode.biacentric.demeter.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.toFlux

/**
 * Created by Arthur Asatryan.
 * Date: 11/7/17
 * Time: 1:55 AM
 */
@Configuration
class InitDb {

    @Bean
    fun commandLineRunner(userRepository: UserRepository): CommandLineRunner {
        return CommandLineRunner {
            listOf(
                    User("art@mailinator.com", "pass1"),
                    User("foo@mailinator.com", "pass2"),
                    User("bar@mailinator.com", "pass3")
            ).toFlux().let {
                userRepository.deleteAll().thenMany(userRepository.saveAll(it)).blockLast()
            }
        }
    }

}