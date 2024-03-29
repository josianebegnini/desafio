package com.sicredi.desafio.config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
	    @Bean
	    public DirectExchange exchange() {
	        return new DirectExchange("exchange-desafio");
	    }

	    @Bean
	    public Queue queue() {
	        return new Queue("queue-resultado-votacao");
	    }

	    @Bean
	    public Binding binding(Queue queue, DirectExchange exchange) {
	        return BindingBuilder.bind(queue).to(exchange).with("routing-key");
	    }
}
