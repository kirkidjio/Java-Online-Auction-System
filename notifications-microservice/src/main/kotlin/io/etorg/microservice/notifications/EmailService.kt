package io.etorg.microservice.notifications

import io.etorg.microservice.notifications.infrastructure.IEmailSubscribersRepository
import org.springframework.stereotype.Service

@Service
class EmailService (val rep: IEmailSubscribersRepository) {




}