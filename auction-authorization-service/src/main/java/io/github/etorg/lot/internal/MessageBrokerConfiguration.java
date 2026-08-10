package io.github.etorg.lot.internal;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class MessageBrokerConfiguration {

    @Bean
    MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("lot.direct");
    }

}
