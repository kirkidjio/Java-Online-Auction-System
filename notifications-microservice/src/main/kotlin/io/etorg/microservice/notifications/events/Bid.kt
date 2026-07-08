package io.etorg.microservice.notifications.events

import java.math.BigDecimal
import java.util.UUID

data class Bid(val id: UUID ,val buyerId: UUID, val currency: String, val value: BigDecimal)