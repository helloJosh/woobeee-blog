package com.woobeee.chatv2.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class ChatMessage (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val sender: String = "",

    @Column(nullable = false, columnDefinition = "text")
    val content: String = "",

    @Column(nullable = false)
    val type: String = "",

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    protected constructor() : this(sender = "", content = "", type = "")
}