package io.github.etorg.lot.internal.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.etorg.lot.internal.domain.BidVO;
import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.infrastructure.repositories.LotJdbcRepository;

@Service
public class LotService {
	
	LotJdbcRepository rep;
	
	public LotService(LotJdbcRepository rep) {
		this.rep = rep;
	}
    
	public void createLot(UUID userId, String currency,LocalDateTime timeout, String description ,int minBid) {
		LotAggregate lot = new LotAggregate(UUID.randomUUID() ,userId, currency, timeout, description ,minBid);
		rep.save(lot);
	}
	
	public void makeBid(UUID userId, UUID lotId, String currency, int value) {
		Optional<LotAggregate> lot = rep.findById(lotId);
		BidVO bid = new BidVO(UUID.randomUUID(), userId, currency, value);
		lot.get().makeBid(bid);
		rep.save(lot.get());
	}
	
}
