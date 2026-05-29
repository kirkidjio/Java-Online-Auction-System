package io.github.etorg.lot.internal.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import io.github.etorg.lot.internal.domain.BidVO;
import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.infrastructure.repositories.ILotQueryRepository;
import io.github.etorg.lot.internal.infrastructure.repositories.ILotRepository;
import io.github.etorg.lot.internal.infrastructure.repositories.enums.LotAttributeSort;
import io.github.etorg.lot.internal.infrastructure.repositories.enums.Order;
import io.github.etorg.lot.internal.service.dto.CategoryDto;
import io.github.etorg.lot.internal.service.dto.CreateLotDto;
import io.github.etorg.lot.internal.service.dto.LotCardDto;
import io.github.etorg.lot.internal.service.dto.LotCardQueryDto;
import io.github.etorg.lot.internal.service.dto.LotCardsWithCursorDto;
import io.github.etorg.lot.internal.service.dto.LotDto;
import io.github.etorg.lot.internal.service.dto.MakeBidDto;

@Service
public class LotService {
	
	ILotRepository rep;
	ILotQueryRepository repQ;
	
	public LotService(ILotRepository rep, ILotQueryRepository repQ) {
		this.rep = rep;
		this.repQ = repQ;
	}
    
	public void createLot(UUID userId, CreateLotDto dto) {
		LotAggregate lot = new LotAggregate(UUID.randomUUID() ,userId, dto.currency(), dto.timeout(), dto.description() ,dto.minBid(), dto.title());
		rep.save(lot);
	}
	
	public void makeBid(UUID userId, MakeBidDto dto) {
		LotAggregate lot = rep.findById(dto.lotId()).orElseThrow();
		BidVO bid = new BidVO(UUID.randomUUID(), userId, dto.currency(),dto.value());
		lot.makeBid(bid);
		rep.save(lot);
	}
	
	public void closeByOwner(UUID userId, UUID lotId) {
		LotAggregate lot = rep.findById(lotId).orElseThrow();
		lot.closeByOwner(userId);
		rep.save(lot);
	}
	
	public void drawByOwner(UUID userId, UUID lotId) {
		LotAggregate lot = rep.findById(lotId).orElseThrow();;
		lot.drawByOwner(userId);
		rep.save(lot);
	}
	
	public LotCardsWithCursorDto getCards(LotCardQueryDto dto) {
		if ("TIMEOUT".equals(dto.attribute()) && dto.cursor() != null) {
			List<LotCardDto> cards = repQ.getSortedCards(LotAttributeSort.valueOf(dto.attribute()),Order.valueOf(dto.order()), LocalDateTime.parse(dto.cursor()));
			return new LotCardsWithCursorDto(cards, cards.getLast().timeout().toString()); 
			}
		else if ("CREATED_AT".equals(dto.attribute()) && dto.cursor() != null) {
			List<LotCardDto> cards = repQ.getSortedCards(LotAttributeSort.valueOf(dto.attribute()),Order.valueOf(dto.order()), LocalDateTime.parse(dto.cursor()));
			return new LotCardsWithCursorDto(cards, cards.getLast().created_at().toString()); 
			}
		else if ("MIN_BID".equals(dto.attribute()) && dto.cursor() != null) {
			List<LotCardDto> cards = repQ.getSortedCards(LotAttributeSort.valueOf(dto.attribute()),Order.valueOf(dto.order()), new BigDecimal(dto.cursor()));
			return new LotCardsWithCursorDto(cards, cards.getLast().min_bid().toString()); 
			}
		else if (dto.cursor() == null) {
			List<LotCardDto> cards = repQ.getSortedCards(LotAttributeSort.valueOf(dto.attribute()),Order.valueOf(dto.order()));
			return new LotCardsWithCursorDto(cards, cards.getLast().min_bid().toString()); 
		}
		
		else throw new RuntimeException("Attribute %s not sortable".formatted(dto.attribute()));
	}
	
	public LotDto getLot(UUID id) {
		return repQ.getLot(id);
	}
	
	public void deleteLot(UUID id) {
		rep.delete(id);
	}
	
	public List<CategoryDto> getCategories(){
		return repQ.getCategories();
		
	}

	
	
	
}
