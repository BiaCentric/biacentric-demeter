package com.biacode.biacentric.demeter.repository.exception

/**
 * Created by Arthur Asatryan.
 * Date: 11/7/17
 * Time: 10:59 AM
 */
data class UserNotFoundException(
        override val message: String,
        override val cause: Throwable
) : RuntimeException()