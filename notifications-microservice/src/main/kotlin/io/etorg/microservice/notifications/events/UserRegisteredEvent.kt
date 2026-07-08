package io.etorg.microservice.notifications.events

import java.util.UUID

@JvmRecord
data class UserRegisteredEvent(val userId: UUID, val username: String, val email: String)
