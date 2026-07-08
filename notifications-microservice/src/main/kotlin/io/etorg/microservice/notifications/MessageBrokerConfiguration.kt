package io.etorg.microservice.notifications

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
    fun exchange(): DirectExchange {
        return DirectExchange("lot.direct")
    }



    @Bean
    fun usersQueue(): Queue {
        return QueueBuilder.durable("users.notifications").quorum().build()
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
    fun bindingUsersQueueToLotExchange(usersQueue: Queue, exchange: DirectExchange): Binding {
        return BindingBuilder.bind(usersQueue).to(exchange).with("routing.users.notifications")
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
