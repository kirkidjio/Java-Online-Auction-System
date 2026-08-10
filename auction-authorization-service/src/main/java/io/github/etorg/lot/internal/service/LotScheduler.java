package io.github.etorg.lot.internal.service;

import java.util.List;
import java.util.UUID;

import io.github.etorg.lot.internal.domain.events.Event;
import io.github.etorg.lot.internal.domain.events.LotClosedEvent;
import io.github.etorg.lot.internal.domain.exceptions.DomainLotException;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.infrastructure.repositories.ILotRepository;

@Component
public class LotScheduler {
	
	@Autowired
	ILotRepository rep;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	DirectExchange direct;
	
	@Scheduled(fixedRate=10000)
	void changeLotStateAfterTimeout() {
		List<LotAggregate> lots = rep.findWithTimeout();
		for (LotAggregate lot: lots) try { lot.changeStateAfterTimeout();} catch (DomainLotException e) {continue;} // todo
		rep.save(lots);
		for (LotAggregate lot: lots)
			for (Event event: lot.getUpdates())
				if (event instanceof LotClosedEvent) rabbitTemplate.convertAndSend(direct.getName(), "routing.lot.closed", event);
				else rabbitTemplate.convertAndSend(direct.getName(), "routing.lot.drawed", event);
	}
	
}
