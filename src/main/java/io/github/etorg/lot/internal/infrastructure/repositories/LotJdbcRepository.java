package io.github.etorg.lot.internal.infrastructure.repositories;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.etorg.lot.internal.domain.*;

import java.util.ArrayList;
import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;

@Repository
public class LotJdbcRepository implements ILotRepository {
	private JdbcTemplate jdbcTemplate;
	
	public LotJdbcRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public Optional<LotAggregate> findById(UUID id) {
		
		List<BidVO> bids = jdbcTemplate.query("select * from bids where lot_id=?", this::mappingBid, id);
		List<LotAggregate.LotAggregateBuilder> lotBuilder = jdbcTemplate.query("select * from lots  where id=?", this::mappingLot, id);
		
		
		return Optional.of(lotBuilder.get(0).bids(bids).build());
		
	}
	
	
	@Transactional
	@Override
	public void save(LotAggregate lot) {
		jdbcTemplate.update(
				"""
			    insert into lots (
			        id,
			        owner_id,
			        timeout,
			        min_bid,
			        currency,
			        status,
			        description
			    )
			    values (?, ?, ?, ?, ?, ?, ?)

			    on conflict(id)
			    do update set
			        owner_id = excluded.owner_id,
			        timeout = excluded.timeout,
			        currency = excluded.currency,
			        min_bid = excluded.min_bid,
			        status = excluded.status
			    """,
			    lot.getId(),
			    lot.getOwnerId(),
			    Timestamp.valueOf(lot.getTimeOut()),
			    lot.getMinBid(),
			    lot.getCurrency(),
			    lot.getState(),
			    lot.getDescription());
		
		List<BidVO> bids = lot.getBids();
		if(!bids.isEmpty()) {
			jdbcTemplate.batchUpdate("insert into bids (id, buyer_id, currency, value, lot_id) values (?,?,?,?,?) on conflict(id) do nothing", bids, bids.size(),
					(ps, bid) -> {
						ps.setObject(1, bid.id());
						ps.setObject(2, bid.buyerId());
						ps.setString(3, bid.currency());
						ps.setFloat(4, bid.value());
						ps.setObject(5, lot.getId());
					});
			
		}
				
	}
	
	private LotAggregate.LotAggregateBuilder mappingLot(ResultSet row, int rowNum) throws SQLException{
		return LotAggregate.builder()
				.id(UUID.fromString(row.getString("id")))
				.ownerId(UUID.fromString(row.getString("owner_id")))
				.currency(row.getString("currency"))
				.timeout(row.getTimestamp("timeout").toLocalDateTime())
				.minBid(row.getInt("min_bid"))
				.state(row.getString("status"))
				.description(row.getString("description"));
	}	
	
	private BidVO mappingBid(ResultSet row, int rowNum) throws SQLException {
		return new BidVO(UUID.fromString(row.getString("id")) ,UUID.fromString(row.getString("buyer_id")), row.getString("currency"), row.getInt("value"));
		
	}
	
	
}
