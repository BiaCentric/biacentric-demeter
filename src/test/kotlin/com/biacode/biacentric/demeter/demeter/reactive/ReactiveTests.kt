package com.biacode.biacentric.demeter.demeter.reactive

import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Test
import reactor.core.Exceptions
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import reactor.test.StepVerifier.create
import java.io.IOException
import java.time.Duration

/**
 * Created by Arthur Asatryan.
 * Date: 11/6/17
 * Time: 4:43 PM
 */
class ReactiveTests {

    @Test
    fun empty() {
        val flux = Flux.empty<String>()
        create(flux)
                .verifyComplete()
    }

    @Test
    fun fromValues() {
        val flux = Flux.just("foo", "bar")
        create(flux)
                .expectNext("foo", "bar")
                .verifyComplete()
    }

    @Test
    fun fromList() {
        val flux = Flux.just(listOf("foo", "bar"))
        create(flux)
                .expectNext(listOf("foo", "bar"))
                .verifyComplete()
    }

    @Test
    fun fromError() {
        val flux = Flux.error<IllegalStateException>(IllegalStateException())
        create(flux)
                .expectError(IllegalStateException::class.java)
                .verify()
    }

    @Test
    fun countEach100ms() {
        val flux = Flux.interval(Duration.ofMillis(100)).take(3)
        create(flux)
    }

    @Test
    fun noSignal() {
        val mono = Mono.never<Void>()
        create(mono)
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1))
                .thenCancel()
                .verify()
    }

    @Test
    fun monoWithValue() {
        Mono.just("foo").let {
            create(it)
                    .expectNext("foo")
                    .verifyComplete()
        }
    }

    @Test
    fun monoWithError() {
        Mono.error<IllegalStateException>(IllegalStateException()).let {
            create(it)
                    .expectError(IllegalStateException::class.java)
                    .verify()
        }
    }

    @Test
    fun `expect 3 elements then error`(): Unit = listOf("foo", "bar").toFlux()
            .concatWith("baz".toMono())
            .concatWith(Mono.error(IllegalStateException()))
            .let {
                create(it)
                        .expectNext("foo", "bar", "baz")
                        .expectError(IllegalStateException::class.java)
                        .verify()
            }

    //region User
    @Test
    fun `expect elements with expectNextMatches and consumeNextWith`(): Unit = listOf(
            User("Arthur"),
            User("Gago"),
            User("Vagho")
    ).toFlux().let {
        create(it)
                .expectNextMatches { it.username == "Arthur" }
                .consumeNextWith { Assertions.assertThat(it.username).isEqualTo("Gago") }
                .consumeNextWith { Assert.assertEquals(it.username, "Vagho") }
                .verifyComplete()
    }

    data class User(val username: String, val email: String? = null, val password: String? = null)
    //endregion

    @Test
    fun `with long running task`() {
        Flux.interval(Duration.ofSeconds(1)).take(10).let {
            create(it)
                    .expectNextCount(10)
                    .verifyComplete()
        }
    }

    @Test
    fun `count with virtual time, note this one is cool`(): Unit = {
        Flux.interval(Duration.ofSeconds(1)).take(3600)
    }.let {
        StepVerifier.withVirtualTime(it)
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(3600)
                .verifyComplete()
    }

    @Test
    fun `capitalize one - transform mono`() {
        "arthur".toMono().map { it.toUpperCase() }.let {
            create(it).expectNext("ARTHUR")
        }
    }

    @Test
    fun `async capitalize`(): Unit = "arthur".toMono()
            .flatMap { it.toUpperCase().toMono() }.let {
        create(it)
                .expectNext("ARTHUR")
                .verifyComplete()
    }

    @Test
    fun `merge two fluxes`() {
        val firstFlux = listOf("foo", "bar").toFlux()
        val secondFlux = listOf("baz", "taz").toFlux()
        Flux.merge(firstFlux, secondFlux).let {
            create(it)
                    .expectNext("foo", "bar", "baz", "taz")
                    .verifyComplete()
        }
        firstFlux.concatWith(secondFlux).let {
            create(it)
                    .expectNext("foo", "bar", "baz", "taz")
                    .verifyComplete()
        }
    }

    @Test
    fun `multiple monos to flux`() {
        val mono1 = "foo".toMono()
        val mono2 = "bar".toMono()
        Flux.concat(mono1, mono2).log().let {
            create(it)
                    .expectNext("foo", "bar")
                    .verifyComplete()
        }
    }

    @Test
    fun `request all by one`() {
        listOf("foo", "bar", "baz", "taz").toFlux().log().let {
            create(it, 1)
                    .expectNext("foo")
                    .thenRequest(2)
                    .expectNext("bar", "baz")
                    .thenCancel()
        }
    }

    @Test
    fun `do on each`() {
        listOf("foo", "bar", "baz").toFlux()
                .doOnSubscribe { println("Starring...") }
                .doOnNext { println(it) }
                .doOnComplete { println("It completed...") }
                .let {
                    create(it)
                            .expectNext("foo", "bar", "baz")
                            .verifyComplete()
                }
    }

    @Test
    fun `restore on error and put other mono`() {
        Mono.empty<String>().switchIfEmpty("arthur".toMono()).let {
            create(it)
                    .expectNext("arthur")
                    .verifyComplete()
        }
    }

    @Test
    fun `deal with checked exceptions`() {
        Mono.error<RuntimeException>(Exceptions.propagate(IOException())).let {
            create(it)
                    .expectError(IOException::class.java)
                    .verify()
        }
    }

    @Test
    fun `zipping flux`() {
        val mono1 = "foo".toMono()
        val mono2 = "bar".toMono()
        val mono3 = "baz".toMono()
        Flux.zip(mono1, mono2, mono3)
                .map { it.t1.plus(it.t2).plus(it.t3) }
                .let {
                    create(it)
                            .expectNext("foobarbaz")
                            .verifyComplete()
                }
    }

    @Test
    fun `get the first completed mono`() {
        val mono1 = "arthur".toMono()
        val mono2 = "vagho".toMono()
        Mono.first(mono1, mono2).let {
            create(it).expectNextCount(1).verifyComplete()
        }
    }

    @Test
    fun `then operator - when you do not care when the flux is completed`() {
        listOf("foo", "bar", "baz").toFlux().then().let {
            create(it).verifyComplete()
        }
    }

    @Test
    fun `reactive streams are null aware - null handling`() {
        Mono.justOrEmpty<String>(null).let {
            create(it).verifyComplete()
        }
    }

    @Test
    fun `deal with empty mono`() {
        Mono.empty<String>().switchIfEmpty("foo".toMono()).let {
            create(it).expectNext("foo").verifyComplete()
        }
    }

    @Test
    fun `blocking and crying`() {
        val block = "foo".toMono().block()
        Assert.assertEquals("foo", block)
    }

    @Test
    fun `dealing with blocking calls - for example calling jdbc`() {
        Flux.defer({ Flux.fromIterable(listOf("foo", "bar", "baz")).subscribeOn(Schedulers.elastic()) })
    }

    @Test
    fun `run on specified scheduler`() {
        "arthur".toMono().publishOn(Schedulers.parallel()).doOnNext { println(it) }.then().let {
            create(it).verifyComplete()
        }
    }
}