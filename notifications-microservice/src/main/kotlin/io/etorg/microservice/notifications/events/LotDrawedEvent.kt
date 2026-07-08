package io.etorg.microservice.notifications.events

import java.util.*

data class LotDrawedEvent(val lotId: UUID, val reason: String, val bids: MutableList<Bid>, val title: String, val ownerId: UUID)