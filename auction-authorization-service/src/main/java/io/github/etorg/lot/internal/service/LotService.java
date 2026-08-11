package io.github.etorg.lot.internal.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import io.github.etorg.lot.internal.domain.events.Event;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	DirectExchange direct;
	
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
		for(Event event: lot.getUpdates()) rabbitTemplate.convertAndSend(direct.getName(), "routing.lot.bid", event);
	}
	
	public void closeByOwner(UUID userId, UUID lotId) {
		LotAggregate lot = rep.findById(lotId).orElseThrow();
		lot.closeByOwner(userId);
		rep.save(lot);
		for(Event event: lot.getUpdates()) rabbitTemplate.convertAndSend(direct.getName(), "routing.lot.closed", event);
	}
	
	public void drawByOwner(UUID userId, UUID lotId) {
		LotAggregate lot = rep.findById(lotId).orElseThrow();;
		lot.drawByOwner(userId);
		rep.save(lot);
		for(Event event: lot.getUpdates()) rabbitTemplate.convertAndSend(direct.getName(), "routing.lot.drawed", event);
	}
	
	public LotCardsWithCursorDto getCards(LotCardQueryDto dto) {
		LotAttributeSort attribute = parseAttribute(dto.attribute());
		Order order = Order.valueOf(dto.order());
		List<LotCardDto> cards;
		String nextCursor;

		if (dto.cursor() == null) {
			cards = repQ.getSortedCards(attribute, order);
			// An empty first page means there is nothing to paginate over.
			nextCursor = cards.isEmpty() ? null : buildCursor(attribute, cards.getLast());
		} else {
			cards = getCardsByCursor(attribute, order, dto.cursor());
			// An empty page means the end of the list was reached, so the incoming
			// cursor is echoed back to let the client know there is nothing more.
			nextCursor = cards.isEmpty() ? dto.cursor() : buildCursor(attribute, cards.getLast());
		}
		return new LotCardsWithCursorDto(cards, nextCursor);
	}

	// Resolves the requested sort attribute, keeping the original error contract.
	private LotAttributeSort parseAttribute(String name) {
		for (LotAttributeSort attribute : LotAttributeSort.values())
			if (attribute.name().equals(name)) return attribute;
		throw new RuntimeException("Attribute %s not sortable".formatted(name));
	}

	// The cursor carries both the sort value and the id of the last shown card.
	// Format: "<sortValue>;<cardId>". The id is the tie-breaker that prevents
	// duplicates between pages when many cards share the same sort value.
	private List<LotCardDto> getCardsByCursor(LotAttributeSort attribute, Order order, String cursor) {
		String value = cursor.substring(0, cursor.indexOf(';'));
		UUID cursorId = UUID.fromString(cursor.substring(cursor.indexOf(';') + 1));
		return switch (attribute) {
			case TIMEOUT, CREATED_AT -> repQ.getSortedCards(attribute, order, LocalDateTime.parse(value), cursorId);
			case MIN_BID -> repQ.getSortedCards(attribute, order, new BigDecimal(value), cursorId);
			default -> throw new RuntimeException("Attribute %s not sortable".formatted(attribute));
		};
	}

	// Builds the next pagination cursor from the last card of the current page.
	private String buildCursor(LotAttributeSort attribute, LotCardDto last) {
		return switch (attribute) {
			case TIMEOUT -> last.timeout() + ";" + last.id();
			case CREATED_AT -> last.created_at() + ";" + last.id();
			case MIN_BID -> last.min_bid() + ";" + last.id();
		};
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
