package com.biacode.biacentric.demeter.model

import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by Arthur Asatryan.
 * Date: 11/7/17
 * Time: 1:11 AM
 */
@Document
data class User(val email: String, val password: String)