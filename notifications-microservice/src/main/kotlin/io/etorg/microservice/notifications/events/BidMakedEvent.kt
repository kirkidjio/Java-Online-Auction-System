package io.etorg.microservice.notifications.events

import java.util.UUID

data class BidMakedEvent(val lotId: UUID, val ownerId:UUID , val title:String, val bids: MutableList<Bid>)

