package io.etorg.microservice.notifications.infrastructure

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessageBrokerConfiguration {

    @Bean
    fun messageConverter(): MessageConverter {
        return JacksonJsonMessageConverter()
    }

    @Bean
    fun lotExchange(): DirectExchange {
        return DirectExchange("auction.direct")
    }



    @Bean
    fun lotClosedEmailForOwnerQueue(): Queue {
        return QueueBuilder.durable("notifications.lot-closed-email-for-owner.notifications").quorum().build()
    }

    @Bean
    fun lotClosedEmailForWinnerQueue(): Queue {
        return QueueBuilder.durable("notifications.lot-closed-email-for-winner.notifications").quorum().build()
    }

    @Bean
    fun lotClosedEmailForOtherUsersQueue(): Queue {
        return QueueBuilder.durable("notifications.lot-closed-email-for-users.notifications").quorum().build()
    }

    @Bean
    fun lotDrawEmailForOwnerQueue(): Queue {
        return QueueBuilder.durable("notifications.lot-draw-email-for-owner.notifications").quorum().build()
    }

    @Bean
    fun lotDrawEmailForWinnerQueue(): Queue {
        return QueueBuilder.durable("notifications.lot-draw-email-for-winner.notifications").quorum().build()
    }

    @Bean
    fun lotDrawEmailForUsersQueue(): Queue {
        return QueueBuilder.durable("notifications.lot-draw-email-for-users.notifications").quorum().build()
    }

    @Bean
    fun bidMadeEmailForOwnerQueue(): Queue {
        return QueueBuilder.durable("notifications.bid-made-email-for-owner.notifications").quorum().build()
    }

    @Bean
    fun bidMadeEmailForUsersQueue(): Queue {
        return QueueBuilder.durable("notifications.bid-made-email-for-users.notifications").quorum().build()
    }

    @Bean
    fun usersRegisteredQueue(): Queue {
        return QueueBuilder.durable("users.registered.notifications").quorum().build()
    }

    @Bean
    fun usersConfirmRegistrationQueue(): Queue {
        return QueueBuilder.durable("users.confirm-registration.notifications").quorum().build()
    }

    @Bean
    fun lotClosedQueue(): Queue {
        return QueueBuilder.durable("lot.closed.notifications").quorum().build()
    }

    @Bean
    fun lotDrawedQueue(): Queue {
        return QueueBuilder.durable("lot.drawed.notifications").quorum().build()
    }

    @Bean
    fun lotBidQueue(): Queue {
        return QueueBuilder.durable("lot.bid.notifications").quorum().build()
    }






    @Bean
    fun bindingUsersRegisteredQueueToLotExchange(usersRegisteredQueue: Queue, exchange: DirectExchange): Binding {
        return BindingBuilder.bind(usersRegisteredQueue).to(exchange).with("routing.users.registered")
    }

    @Bean
    fun bindingUsersConfirmRegistrationQueueToLotExchange(usersConfirmRegistrationQueue: Queue, exchange: DirectExchange): Binding {
        return BindingBuilder.bind(usersConfirmRegistrationQueue).to(exchange).with("routing.users.confirm-registration")
    }

    @Bean
    fun bindingLotClosedQueueToLotExchange(lotClosedQueue: Queue, exchange: DirectExchange): Binding {
        return BindingBuilder.bind(lotClosedQueue).to(exchange).with("routing.lot.closed")
    }

    @Bean
    fun bindingLotDrawedQueueToLotExchange(lotDrawedQueue: Queue, exchange: DirectExchange): Binding {
        return BindingBuilder.bind(lotDrawedQueue).to(exchange).with("routing.lot.drawed")
    }

    @Bean
    fun bindingLotBidQueueToLotExchange(lotBidQueue: Queue, exchange: DirectExchange): Binding {
        return BindingBuilder.bind(lotBidQueue).to(exchange).with("routing.lot.bid")
    }

}