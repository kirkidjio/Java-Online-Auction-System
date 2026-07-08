package io.etorg.microservice.notifications

import io.etorg.microservice.notifications.events.Bid
import io.etorg.microservice.notifications.events.BidMakedEvent
import io.etorg.microservice.notifications.events.LotClosedEvent
import io.etorg.microservice.notifications.events.LotDrawedEvent
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailSender(val mailSender: JavaMailSender, val rep: IEmailSubscribersRepository) {


    @RabbitListener(queues = ["lot.closed.notifications"])
    fun informSubscribersAboutClosedLot(event: LotClosedEvent) {
        informOwnerAboutClosedLot(event)
        informWinnerAboutClosedLot(event)
        informOtherUsersAboutClosedLot(event)
    }

    @RabbitListener(queues = ["lot.drawed.notifications"])
    fun informSubscribersAboutDrawLot(event: LotDrawedEvent) {
        informOwnerAboutDrawLot(event)
        informUsersAboutDrawLot(event)
    }

    @RabbitListener(queues = ["lot.bid.notifications"])
    fun informSubscribersAboutMadeBidLot(event: BidMakedEvent) {
        informOwnerAboutMadeBid(event)
        informUsersAboutMadeBid(event)
    }

    fun informOwnerAboutClosedLot(event: LotClosedEvent) {

        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(rep.findEmailByUserId(event.ownerId))
        mailMessage.subject = "Lot ${event.title} closed"
        mailMessage.text = "Your lot ${event.title} is closed. The winner is ${rep.findUsernameByUserId(event.winnerId)}. Lot sold for ${event.bids.last().value} ${event.bids.last().currency}"

        mailSender.send(mailMessage)
    }

    fun informWinnerAboutClosedLot(event: LotClosedEvent) {

        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(rep.findEmailByUserId(event.winnerId))
        mailMessage.subject = "You are win: ${event.title}"
        mailMessage.text = "Congratulations! The lot ${event.title} is your. Lot sold for ${event.bids.last().value} ${event.bids.last().currency}"

        mailSender.send(mailMessage)
    }

    fun informOtherUsersAboutClosedLot(event: LotClosedEvent) {
        val bidsWithoutWinnerBid = event.bids.toMutableList()
        bidsWithoutWinnerBid.removeAt(bidsWithoutWinnerBid.lastIndex)

        for (bid: Bid in bidsWithoutWinnerBid) {
            val mailMessage: SimpleMailMessage = SimpleMailMessage()
            mailMessage.setTo(rep.findEmailByUserId(bid.buyerId))
            mailMessage.subject = "Lot closed ${event.title}"
            mailMessage.text =
                "The lot ${event.title} sold other user. Lot sold for ${event.bids.last().value} ${event.bids.last().currency}"


            mailSender.send(mailMessage)
        }
    }

    fun informUsersAboutMadeBid(event: BidMakedEvent) {
        val bidsWithoutWinnerBid = event.bids.toMutableList()
        bidsWithoutWinnerBid.removeAt(bidsWithoutWinnerBid.lastIndex)

        for (bid: Bid in bidsWithoutWinnerBid) {
            val mailMessage: SimpleMailMessage = SimpleMailMessage()
            mailMessage.setTo(rep.findEmailByUserId(bid.buyerId))
            mailMessage.subject = "New bid made ${event.title}"
            mailMessage.text =
                "The lot ${event.title} with new bid. Last bid is ${event.bids.last().value} ${event.bids.last().currency}"


            mailSender.send(mailMessage)
        }
    }

    fun informOwnerAboutMadeBid(event: BidMakedEvent) {

        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(rep.findEmailByUserId(event.ownerId))
        mailMessage.subject = "New bid made for your lot ${event.title}"
        mailMessage.text =
            "Your lot ${event.title} with new bid. Last bid is ${event.bids.last().value} ${event.bids.last().currency}"


        mailSender.send(mailMessage)

    }

    fun informUsersAboutDrawLot(event: LotDrawedEvent){

        val bidsWithoutWinnerBid = event.bids.toMutableList()
        bidsWithoutWinnerBid.removeAt(bidsWithoutWinnerBid.lastIndex)

        for (bid: Bid in bidsWithoutWinnerBid) {
            val mailMessage: SimpleMailMessage = SimpleMailMessage()
            mailMessage.setTo(rep.findEmailByUserId(bid.buyerId))
            mailMessage.subject = "Lot Draw ${event.title}"
            mailMessage.text =
                "The lot ${event.title} Draw."


            mailSender.send(mailMessage)
        }

    }

    fun informOwnerAboutDrawLot(event: LotDrawedEvent){

        val bidsWithoutWinnerBid = event.bids.toMutableList()
        bidsWithoutWinnerBid.removeAt(bidsWithoutWinnerBid.lastIndex)

        for (bid: Bid in bidsWithoutWinnerBid) {
            val mailMessage: SimpleMailMessage = SimpleMailMessage()
            mailMessage.setTo(rep.findEmailByUserId(bid.buyerId))
            mailMessage.subject = "Your Lot Draw ${event.title}"
            mailMessage.text =
                "Your lot ${event.title} Draw."


            mailSender.send(mailMessage)
        }

    }


}