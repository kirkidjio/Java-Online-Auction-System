package io.etorg.microservice.notifications

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID


@Entity
@Table(name = "email_subscribers", schema = "notifications")
class EmailSubscribersEntity (

    @Id
    @GeneratedValue(GenerationType.UUID)
    var id:UUID? = null,

    @Column(name="user_id", unique = true, nullable = false)
    var userId: UUID,

    @Column(name="email", unique = true, nullable = false)
    var email: String,

    @Column(name="username", unique = true, nullable = false)
    var username: String,

    @Column(name="isSubscribed", nullable = false)
    var isSubscribed: Boolean = true

)