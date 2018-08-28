package br.com.jgorozimbo.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import br.com.jgorozimbo.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
	
}
