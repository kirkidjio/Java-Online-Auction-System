package io.etorg.microservice.notifications.service.users.email

import io.etorg.microservice.notifications.events.UserConfirmRegistrationEvent
import io.etorg.microservice.notifications.events.UserRegisteredEvent
import io.etorg.microservice.notifications.infrastructure.IEmailSubscribersRepository
import io.etorg.microservice.notifications.models.EmailSubscribersEntity
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class UserAuthListener(val emailSubscribersRepository: IEmailSubscribersRepository,
                       val mailSender: JavaMailSender) {

    @RabbitListener(queues = ["users.confirm-registration.notifications"])
    private fun sendEmailWithToken(event: UserConfirmRegistrationEvent){
        val mailMessage: SimpleMailMessage = SimpleMailMessage()
        mailMessage.setTo(event.email)
        mailMessage.subject = "Registration confirm"
        mailMessage.text = "To complete registration on the Etorg platform, follow the link: http://localhost:8080/api/users/authentication/confirm-registration/${event.token}"

        mailSender.send(mailMessage)
    }

    @RabbitListener(queues = ["users.registered.notifications"])
    fun registerUser(event: UserRegisteredEvent) {
        emailSubscribersRepository.save(EmailSubscribersEntity(null, event.userId, event.email, event.username))
    }


}