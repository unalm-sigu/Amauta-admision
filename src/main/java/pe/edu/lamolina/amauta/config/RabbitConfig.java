package pe.edu.lamolina.amauta.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.lamolina.amauta.controller.mensajeria.queue.ChatUnalmQueue;
import pe.edu.lamolina.model.constantines.BienestarConstantine;

@Configuration
public class RabbitConfig {

    @Bean
    Queue queue() {
        return new Queue(BienestarConstantine.QUEUE_CHAT_AMAUTA, true);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("mensaje-chat-unalm");
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(BienestarConstantine.QUEUE_CHAT_AMAUTA);
    }

    @Bean()
    MessageListenerAdapter listenerAdapter(ChatUnalmQueue receiver) {
        MessageListenerAdapter listener = new MessageListenerAdapter(receiver);
        listener.addQueueOrTagToMethodName(BienestarConstantine.QUEUE_CHAT_AMAUTA, "handleMessage");
        return listener;
    }


    @Bean
    SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.setQueueNames(BienestarConstantine.QUEUE_CHAT_AMAUTA);
        container.setMessageListener(listenerAdapter);

        return container;
    }

}
