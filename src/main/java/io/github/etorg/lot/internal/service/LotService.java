package io.github.etorg.lot.internal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.etorg.lot.internal.domain.BidVO;
import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.infrastructure.repositories.ILotQueryRepository;
import io.github.etorg.lot.internal.infrastructure.repositories.ILotRepository;

import io.github.etorg.lot.internal.service.dto.LotCardDto;
import io.github.etorg.lot.internal.service.dto.LotDto;

@Service
public class LotService {
	
	ILotRepository rep;
	ILotQueryRepository repQ;
	
	public LotService(ILotRepository rep, ILotQueryRepository repQ) {
		this.rep = rep;
		this.repQ = repQ;
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
	
	public void closeByOwner(UUID userId, UUID lotId) {
		Optional<LotAggregate> lot = rep.findById(lotId);
		lot.get().closeByOwner(userId);
		rep.save(lot.get());
	}
	
	public void drawByOwner(UUID userId, UUID lotId) {
		Optional<LotAggregate> lot = rep.findById(lotId);
		lot.get().drawByOwner(userId);
		rep.save(lot.get());
	}
	
	public List<LotCardDto> getCards(LocalDateTime time) {
		return repQ.getLastCreatedCards(time);
		
	}
	
	public LotDto getLot(UUID id) {
		return repQ.getLot(id);
	}
	
	
	
	
	
	
}
