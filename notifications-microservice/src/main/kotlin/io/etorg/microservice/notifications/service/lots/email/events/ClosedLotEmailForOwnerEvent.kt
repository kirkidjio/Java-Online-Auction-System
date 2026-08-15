package io.etorg.microservice.notifications.service.lots.email.events

import io.etorg.microservice.notifications.service.lots.events.Bid
import java.util.UUID

data class ClosedLotEmailForOwnerEvent(val lotId: UUID, val winnerId :UUID, val reason : String, val bids:MutableList<Bid>, val ownerId:UUID, val title: String)
