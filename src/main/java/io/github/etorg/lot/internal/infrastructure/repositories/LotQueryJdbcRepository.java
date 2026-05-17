package io.github.etorg.lot.internal.infrastructure.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import io.github.etorg.lot.internal.service.dto.LotCardDto;
import io.github.etorg.lot.internal.service.dto.LotDto;

@Repository
public class LotQueryJdbcRepository implements ILotQueryRepository {
	private JdbcTemplate jdbcTemplate;
	
	public LotQueryJdbcRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<LotCardDto> getLastCreatedCards(LocalDateTime time){
		List<LotCardDto> cards = jdbcTemplate.query("""
			
				select created_at, title, min_bid, currency, timeout from lots 
				where created_at < ?
				order by created_at desc limit 10
			
				
				""", this::mappingCard, Timestamp.valueOf(time));
		return cards;
		
	}
	
	public LotDto getLot(UUID id) {
		List<LotDto> lot = jdbcTemplate.query("""
			
				select * from lots where id = ?
			
				
				""", this::mappingLot, id);
		
		return lot.get(0);
	}
	
	private LotCardDto mappingCard(ResultSet row, int rowNum) throws SQLException {
		return new LotCardDto(row.getString("title"), row.getInt("min_bid"), row.getString("currency"), row.getTimestamp("timeout").toLocalDateTime());
		
	}
	
	private LotDto mappingLot(ResultSet row, int rowNum) throws SQLException {
		return new LotDto(
				UUID.fromString(row.getString("id")),
				UUID.fromString(row.getString("owner_id")),
				row.getTimestamp("timeout").toLocalDateTime(),
				row.getString("description"),
				row.getTimestamp("created_at").toLocalDateTime(),
				row.getInt("min_bid"),
				row.getString("currency"),
				row.getString("status"), 
				row.getString("title"));
		
	}
	
	
}
