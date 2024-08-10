package com.meenachinmay.api_gateway.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    var username: String = "",

    var password: String = "",

    @Column(unique = true)
    var email: String = ""
) {
    // No-arg constructor
    constructor() : this(0, "", "", "")
}