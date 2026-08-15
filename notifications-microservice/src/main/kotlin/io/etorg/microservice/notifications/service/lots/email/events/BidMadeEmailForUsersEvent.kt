package io.etorg.microservice.notifications.service.lots.email.events

import io.etorg.microservice.notifications.service.lots.events.Bid
import java.util.UUID

data class BidMadeEmailForUsersEvent(val recipientId: UUID, val lotId: UUID, val ownerId:UUID , val title:String, val bids: MutableList<Bid>)