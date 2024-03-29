package com.sicredi.desafio.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
	@RabbitListener(queues = "queue-resultado-votacao")
	public void receiveMessage(String message) {
		System.out.println("Message received: " + message);
		// Process the received message
	}
}
