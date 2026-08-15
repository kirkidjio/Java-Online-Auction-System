package io.etorg.microservice.notifications.service.lots.email

import io.etorg.microservice.notifications.service.lots.email.events.BidMadeEmailForOwnerEvent
import io.etorg.microservice.notifications.service.lots.email.events.BidMadeEmailForUsersEvent
import io.etorg.microservice.notifications.service.lots.email.events.ClosedLotEmailForOwnerEvent
import io.etorg.microservice.notifications.service.lots.email.events.ClosedLotEmailForUsersEvent
import io.etorg.microservice.notifications.service.lots.email.events.ClosedLotEmailForWinnerEvent
import io.etorg.microservice.notifications.service.lots.email.events.DrawLotEmailForOwnerEvent
import io.etorg.microservice.notifications.service.lots.email.events.DrawLotEmailForUsersEvent
import io.etorg.microservice.notifications.service.lots.email.events.DrawLotEmailForWinnerEvent
import io.etorg.microservice.notifications.service.lots.events.BidMakedEvent
import io.etorg.microservice.notifications.service.lots.events.LotClosedEvent
import io.etorg.microservice.notifications.service.lots.events.LotDrawedEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class LotListener(val rabbitTemplate: RabbitTemplate) {

    // Handles the lot closed event: publishes dedicated email events for the owner, the winner and each other bidder.
    @RabbitListener(queues = ["lot.closed.notifications"])
    fun sendEmailsAboutClosedLot(event: LotClosedEvent) {
        rabbitTemplate.convertAndSend(
            "notifications.lot-closed-email-for-owner.notifications",
            ClosedLotEmailForOwnerEvent(event.lotId, event.winnerId, event.reason, event.bids, event.ownerId, event.title)
        )
        rabbitTemplate.convertAndSend(
            "notifications.lot-closed-email-for-winner.notifications",
            ClosedLotEmailForWinnerEvent(event.lotId, event.winnerId, event.reason, event.bids, event.ownerId, event.title)
        )
        // Publishes a separate email event for every bidder except the winner; users with several bids receive a single event.
        val otherBiddersIds = event.bids.map { it.buyerId }.distinct().filter { it != event.winnerId }
        for (bidderId in otherBiddersIds) {
            rabbitTemplate.convertAndSend(
                "notifications.lot-closed-email-for-users.notifications",
                ClosedLotEmailForUsersEvent(bidderId, event.lotId, event.winnerId, event.reason, event.bids, event.ownerId, event.title)
            )
        }
    }

    // Handles the lot drawed event: publishes dedicated email events for the owner and each bidder.
    // A draw has no winner, so the leading bidder (the one with the highest bid) is notified as the "winner" if any bid exists.
    @RabbitListener(queues = ["lot.drawed.notifications"])
    fun sendEmailsAboutDrawLot(event: LotDrawedEvent) {
        val winnerId = event.bids.maxByOrNull { it.value }?.buyerId

        rabbitTemplate.convertAndSend(
            "notifications.lot-draw-email-for-owner.notifications",
            DrawLotEmailForOwnerEvent(event.lotId, winnerId, event.reason, event.bids, event.ownerId, event.title)
        )
        if (winnerId != null) {
            rabbitTemplate.convertAndSend(
                "notifications.lot-draw-email-for-winner.notifications",
                DrawLotEmailForWinnerEvent(event.lotId, winnerId, event.reason, event.bids, event.ownerId, event.title)
            )
        }
        // Publishes a separate email event for every bidder except the leading bidder; users with several bids receive a single event.
        val otherBiddersIds = event.bids.map { it.buyerId }.distinct().filter { it != winnerId }
        for (bidderId in otherBiddersIds) {
            rabbitTemplate.convertAndSend(
                "notifications.lot-draw-email-for-users.notifications",
                DrawLotEmailForUsersEvent(bidderId, event.lotId, winnerId, event.reason, event.bids, event.ownerId, event.title)
            )
        }
    }

    // Handles the bid made event: publishes dedicated email events for the owner and each other bidder.
    @RabbitListener(queues = ["lot.bid.notifications"])
    fun sendEmailsAboutMadeBid(event: BidMakedEvent) {
        rabbitTemplate.convertAndSend(
            "notifications.bid-made-email-for-owner.notifications",
            BidMadeEmailForOwnerEvent(event.lotId, event.ownerId, event.title, event.bids)
        )
        // Publishes a separate email event for every bidder except the one who placed the latest bid.
        val currentBidderId = event.bids.last().buyerId
        val otherBiddersIds = event.bids.map { it.buyerId }.distinct().filter { it != currentBidderId }
        for (bidderId in otherBiddersIds) {
            rabbitTemplate.convertAndSend(
                "notifications.bid-made-email-for-users.notifications",
                BidMadeEmailForUsersEvent(bidderId, event.lotId, event.ownerId, event.title, event.bids)
            )
        }
    }
}
