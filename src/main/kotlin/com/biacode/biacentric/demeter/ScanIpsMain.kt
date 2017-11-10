package com.biacode.biacentric.demeter

import java.net.InetAddress
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.CompletableFuture.supplyAsync

/**
 * Created by Arthur Asatryan.
 * Date: 11/10/17
 * Time: 1:26 PM
 */
fun main(args: Array<String>) {
    NetworkUtils.checkBySubnet("10.18.3").join()
}

object NetworkUtils {
    fun checkBySubnet(subnet: String): CompletableFuture<Void> {
        val futures = (1..254).map {
            supplyAsync {
                try {
                    val address = InetAddress.getByName("$subnet.$it")
                    if (address.isReachable(100)) {
                        println("${address.toString().substring(1)} is on the network")
                    }
                } catch (ignore: Exception) {
                }
            }
        }
        return allOf(*futures.toTypedArray())
    }
}