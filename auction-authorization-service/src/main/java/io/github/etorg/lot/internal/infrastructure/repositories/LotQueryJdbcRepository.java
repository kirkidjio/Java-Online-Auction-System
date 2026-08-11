package io.github.etorg.lot.internal.infrastructure.repositories;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import io.github.etorg.lot.internal.infrastructure.repositories.enums.*;
import io.github.etorg.lot.internal.service.dto.BidDto;
import io.github.etorg.lot.internal.service.dto.CategoryDto;
import io.github.etorg.lot.internal.service.dto.LotCardDto;
import io.github.etorg.lot.internal.service.dto.LotDto;


@Repository
public class LotQueryJdbcRepository implements ILotQueryRepository {
	private JdbcTemplate jdbcTemplate;
	
	public LotQueryJdbcRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public List<LotCardDto> getSortedCards(LotAttributeSort timeAttribute, Order order, LocalDateTime cursor, UUID cursorId){
		// Inclusive operator keeps rows whose value equals the cursor value, so cards
		// sharing the same sort value are no longer skipped between pages.
		Map<Order, String> inclusiveSign = Map.of(Order.DESC, "<=", Order.ASC, ">=");
		// Strict operator separates rows strictly before/after the cursor value.
		Map<Order, String> strictSign = Map.of(Order.DESC, "<", Order.ASC, ">");
		Set<LotAttributeSort> acceptableColumns = Set.of(LotAttributeSort.CREATED_AT, LotAttributeSort.TIMEOUT);
		
		if (!acceptableColumns.contains(timeAttribute)) throw new RuntimeException("bad value for timeatribute");
		
		List<LotCardDto> cards = jdbcTemplate.query("""
			
				select id, created_at, title, min_bid, currency, timeout from lots.lots 
				where %s %s ? and (%s %s ? or id %s ?)
				order by %s %s, id %s limit 10
			
				
				""".formatted(
				timeAttribute.name(), inclusiveSign.get(order),
				timeAttribute.name(), strictSign.get(order), strictSign.get(order),
				timeAttribute.name(), order.name(), order.name()),
				this::mappingCard, Timestamp.valueOf(cursor), Timestamp.valueOf(cursor), cursorId);
		return cards;
	}
	
	
	public List<LotCardDto> getSortedCards(LotAttributeSort intAttribute, Order order, BigDecimal cursor, UUID cursorId){
		// The id tie-breaker condition prevents duplicates when more than one page
		// worth of cards shares the same sort value: only rows with a smaller id
		// (for DESC) are taken from that value group.
		Map<Order, String> inclusiveSign = Map.of(Order.DESC, "<=", Order.ASC, ">=");
		Map<Order, String> strictSign = Map.of(Order.DESC, "<", Order.ASC, ">");
		Set<LotAttributeSort> acceptableColumns = Set.of(LotAttributeSort.MIN_BID);
		
		if (!acceptableColumns.contains(intAttribute)) throw new RuntimeException("bad value for decimal atribute");
		
		List<LotCardDto> cards = jdbcTemplate.query("""
			
				select id, created_at, title, min_bid, currency, timeout from lots.lots 
				where %s %s ? and (%s %s ? or id %s ?)
				order by %s %s, id %s limit 10
			
				
				""".formatted(
				intAttribute.name(), inclusiveSign.get(order),
				intAttribute.name(), strictSign.get(order), strictSign.get(order),
				intAttribute.name(), order.name(), order.name()),
				this::mappingCard, cursor, cursor, cursorId);
		return cards;
	}
	
	
	public List<LotCardDto> getSortedCards(LotAttributeSort attribute, Order order){
		// The id is added to the order by clause so that the very first page uses the
		// same deterministic ordering as all subsequent cursor-based pages.
		Set<LotAttributeSort> acceptableColumns = Set.of(LotAttributeSort.MIN_BID, LotAttributeSort.TIMEOUT, LotAttributeSort.CREATED_AT);
		
		if (!acceptableColumns.contains(attribute)) throw new RuntimeException("bad value for decimal atribute");
		
		List<LotCardDto> cards = jdbcTemplate.query("""
			
				select id, created_at, title, min_bid, currency, timeout from lots.lots 
				
				order by %s %s, id %s limit 10
			
				
				""".formatted(attribute.name(), order.name(), order.name()), this::mappingCard);
		return cards;
	}
	
	
	
	
	
	
	
	
	public LotDto getLot(UUID id) {
		List<BidDto> bids = getBids(id);
		
		List<LotDto.Builder> lot = jdbcTemplate.query("""
			
				select * from lots.lots l
				
				where l.id = ?
				
				
				""", this::mappingLot, id);
		
		return lot.get(0).bids(bids).build();
	}


	public List<CategoryDto> getCategories() {
		/*
		List<CategoryDto> categories = jdbcTemplate.query("""
			
				select * from categories
				
				
				""", this::mappingCategory);
		
		return categories;

		 */
		throw new RuntimeException("Not implemented");
	}

	
	
	private List<BidDto> getBids(UUID lotId) {
		List<BidDto> bids = jdbcTemplate.query("""
				
				select * from lots.bids b
				join users u on u.id = b.buyer_id
				where b.lot_id = ?
			
				
				""", this::mappingBid, lotId);
		
		return bids;
	}
		
	
	
	
	
	private LotCardDto mappingCard(ResultSet row, int rowNum) throws SQLException {
		return new LotCardDto(
				row.getString("title"),
				row.getBigDecimal("min_bid"),
				row.getString("currency"),
				row.getTimestamp("timeout").toLocalDateTime(),
				row.getTimestamp("created_at").toLocalDateTime(),
				row.getString("id"));
		
	}
	
	private LotDto.Builder mappingLot(ResultSet row, int rowNum) throws SQLException {
	    return LotDto.builder()
	            .id(UUID.fromString(row.getString("id")))
	            .ownerId(UUID.fromString(row.getString("owner_id")))
	            .timeout(row.getTimestamp("timeout").toLocalDateTime())
	            .description(row.getString("description"))
	            .created_at(row.getTimestamp("created_at").toLocalDateTime())
	            .min_bid(row.getBigDecimal("min_bid"))
	            .currency(row.getString("currency"))
	            .status(row.getString("status"))
	            .title(row.getString("title"))
	            .category(row.getString("name"));
	}
	
	private BidDto mappingBid(ResultSet row, int rowNum) throws SQLException {
	    return new BidDto(
	            UUID.fromString(row.getString("id")),
	            row.getString("username"),
	            row.getString("currency"),
	            row.getBigDecimal("value")
	    );
	}
	
	private CategoryDto mappingCategory(ResultSet row, int rowNum) throws SQLException {
	    return new CategoryDto(
	            UUID.fromString(row.getString("id")),
	            row.getString("category")
	    );
	}
	
	
}
