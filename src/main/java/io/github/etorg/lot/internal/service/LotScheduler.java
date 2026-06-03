package io.github.etorg.lot.internal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.infrastructure.repositories.ILotRepository;

@Component
public class LotScheduler {
	
	@Autowired
	ILotRepository rep;
	
	@Scheduled(fixedRate=10000)
	void changeLotStateAfterTimeout() {
		List<LotAggregate> lots = rep.findWithTimeout();
		for (LotAggregate lot: lots) lot.changeStateAfterTimeout();
		rep.save(lots);
	}
	
}
