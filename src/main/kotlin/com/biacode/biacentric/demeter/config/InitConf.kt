package com.biacode.biacentric.demeter.config

import com.biacode.biacentric.demeter.model.User
import com.biacode.biacentric.demeter.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient
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

    @Bean
    fun webClient(): WebClient {
        return WebClient
                .builder()
                .clientConnector(ReactorClientHttpConnector())
                .filter({ request, next ->
                    next.exchange(
                            ClientRequest.from(request)
                                    .header("User-Agent", "Spring")
                                    .build()
                    )
                })
                .build()
    }

}