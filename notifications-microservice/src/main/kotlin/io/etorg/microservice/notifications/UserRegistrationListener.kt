package io.etorg.microservice.notifications

import io.etorg.microservice.notifications.events.UserRegisteredEvent
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component

class UserRegistrationListener (val rep: IEmailSubscribersRepository) {

    @RabbitListener(queues = ["users.notifications"])
    fun registerUser(event: UserRegisteredEvent) {
        rep.save(EmailSubscribersEntity(null, event.userId, event.email, event.username))
        println("MESSAGE ABOUT USER REGISTRATION HANDLED")
    }

}