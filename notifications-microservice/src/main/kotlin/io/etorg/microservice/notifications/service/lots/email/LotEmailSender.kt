package io.etorg.microservice.notifications.service.lots.email

import io.etorg.microservice.notifications.infrastructure.IEmailSubscribersRepository
import io.etorg.microservice.notifications.service.lots.email.events.BidMadeEmailForOwnerEvent
import io.etorg.microservice.notifications.service.lots.email.events.BidMadeEmailForUsersEvent
import io.etorg.microservice.notifications.service.lots.email.events.ClosedLotEmailForOwnerEvent
import io.etorg.microservice.notifications.service.lots.email.events.ClosedLotEmailForUsersEvent
import io.etorg.microservice.notifications.service.lots.email.events.ClosedLotEmailForWinnerEvent
import io.etorg.microservice.notifications.service.lots.email.events.DrawLotEmailForOwnerEvent
import io.etorg.microservice.notifications.service.lots.email.events.DrawLotEmailForUsersEvent
import io.etorg.microservice.notifications.service.lots.email.events.DrawLotEmailForWinnerEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class LotEmailSender(val mailSender: JavaMailSender, val rep: IEmailSubscribersRepository) {

    // Sends an email to the lot owner notifying that the lot was closed and stating who won it.
    @RabbitListener(queues = ["notifications.lot-closed-email-for-owner.notifications"])
    fun informOwnerAboutClosedLot(event: ClosedLotEmailForOwnerEvent) {
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(rep.findEmailByUserId(event.ownerId))
        mailMessage.subject = "Lot ${event.title} closed"
        mailMessage.text = "Your lot ${event.title} is closed. The winner is ${rep.findUsernameByUserId(event.winnerId)}. Lot sold for ${event.bids.last().value} ${event.bids.last().currency}"

        mailSender.send(mailMessage)
    }

    // Sends a congratulations email to the winner of the closed lot.
    @RabbitListener(queues = ["notifications.lot-closed-email-for-winner.notifications"])
    fun informWinnerAboutClosedLot(event: ClosedLotEmailForWinnerEvent) {
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(rep.findEmailByUserId(event.winnerId))
        mailMessage.subject = "You are win: ${event.title}"
        mailMessage.text = "Congratulations! The lot ${event.title} is your. Lot sold for ${event.bids.last().value} ${event.bids.last().currency}"

        mailSender.send(mailMessage)
    }

    // Sends an email to the bidder this event was published for, notifying that the lot was sold to another user.
    @RabbitListener(queues = ["notifications.lot-closed-email-for-users.notifications"])
    fun informOtherUsersAboutClosedLot(event: ClosedLotEmailForUsersEvent) {
        val email = rep.findEmailByUserId(event.recipientId) ?: return
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(email)
        mailMessage.subject = "Lot closed ${event.title}"
        mailMessage.text =
            "The lot ${event.title} sold other user. Lot sold for ${event.bids.last().value} ${event.bids.last().currency}"

        mailSender.send(mailMessage)
    }

    // Sends an email to the lot owner notifying that a new bid was placed on his lot.
    @RabbitListener(queues = ["notifications.bid-made-email-for-owner.notifications"])
    fun informOwnerAboutMadeBid(event: BidMadeEmailForOwnerEvent) {
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(rep.findEmailByUserId(event.ownerId))
        mailMessage.subject = "New bid made for your lot ${event.title}"
        mailMessage.text =
            "Your lot ${event.title} with new bid. Last bid is ${event.bids.last().value} ${event.bids.last().currency}"

        mailSender.send(mailMessage)
    }

    // Sends an email to the bidder this event was published for, notifying that a new bid was placed on the lot.
    @RabbitListener(queues = ["notifications.bid-made-email-for-users.notifications"])
    fun informUsersAboutMadeBid(event: BidMadeEmailForUsersEvent) {
        val email = rep.findEmailByUserId(event.recipientId) ?: return
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(email)
        mailMessage.subject = "New bid made ${event.title}"
        mailMessage.text =
            "The lot ${event.title} with new bid. Last bid is ${event.bids.last().value} ${event.bids.last().currency}"

        mailSender.send(mailMessage)
    }

    // Sends an email to the lot owner notifying that his lot was drawed (cancelled / ended without a sale).
    @RabbitListener(queues = ["notifications.lot-draw-email-for-owner.notifications"])
    fun informOwnerAboutDrawLot(event: DrawLotEmailForOwnerEvent) {
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(rep.findEmailByUserId(event.ownerId))
        mailMessage.subject = "Your Lot Draw ${event.title}"
        mailMessage.text = "Your lot ${event.title} Draw."

        mailSender.send(mailMessage)
    }

    // Sends an email to the leading bidder (the one with the highest bid) notifying that the lot was drawed.
    @RabbitListener(queues = ["notifications.lot-draw-email-for-winner.notifications"])
    fun informWinnerAboutDrawLot(event: DrawLotEmailForWinnerEvent) {
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(rep.findEmailByUserId(event.winnerId))
        mailMessage.subject = "Lot Draw ${event.title}"
        mailMessage.text = "The lot ${event.title} Draw."

        mailSender.send(mailMessage)
    }

    // Sends an email to the bidder this event was published for, notifying that the lot was drawed.
    @RabbitListener(queues = ["notifications.lot-draw-email-for-users.notifications"])
    fun informUsersAboutDrawLot(event: DrawLotEmailForUsersEvent) {
        val email = rep.findEmailByUserId(event.recipientId) ?: return
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(email)
        mailMessage.subject = "Lot Draw ${event.title}"
        mailMessage.text = "The lot ${event.title} Draw."

        mailSender.send(mailMessage)
    }
}
