package io.etorg.microservice.notifications.events

import java.util.UUID

data class LotClosedEvent(val lotId: UUID, val winnerId :UUID, val reason : String, val bids:MutableList<Bid>, val ownerId:UUID, val title: String)
