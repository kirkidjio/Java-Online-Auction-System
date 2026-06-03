package io.github.etorg.lot.internal.infrastructure.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.etorg.lot.internal.domain.*;
import io.github.etorg.lot.internal.infrastructure.repositories.projections.BidProjection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.sql.Timestamp;

@Repository
public class LotJdbcRepository implements ILotRepository {
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	public LotJdbcRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedJdbcTemplate = namedJdbcTemplate;
	}
	
	@Override
	public Optional<LotAggregate> findById(UUID id) {
		
		List<BidVO> bids = jdbcTemplate.query("select * from bids where lot_id=?", this::mappingBid, id);
		List<LotAggregate.LotAggregateBuilder> lotBuilder = jdbcTemplate.query("select * from lots  where id=?", this::mappingLot, id);
		
		
		return Optional.of(lotBuilder.get(0).bids(bids).build());
		
	}
	
	@Override
	public void delete(UUID id) {
		jdbcTemplate.update("delete from lots where id = ?", id);
		
	}
	
	@Override
	public List<LotAggregate> findWithTimeout(){
		
		Map<UUID, LotAggregate.LotAggregateBuilder> mapLots = new HashMap<>();
		
		jdbcTemplate.query("select * from lots  where timeout <= now() and status='OPEN'", (row, rownum) -> {
			mapLots.put(UUID.fromString(row.getString("id")), mappingLot(row,rownum));
			return null;
		});
		
		if (mapLots.isEmpty()) {
		    return List.of();
		}

		
		Map<UUID, List<BidVO>> mapBids = new HashMap<>();
		
		namedJdbcTemplate.query("select * from bids where lot_id in (:ids)", Map.of("ids", mapLots.keySet()) ,(rows, rownum) -> {
			List<BidVO> bids = mapBids.get(UUID.fromString(rows.getString("lot_id")));
			if (bids == null) mapBids.put(UUID.fromString(rows.getString("lot_id")), new ArrayList<>(Arrays.asList(mappingBid(rows, rownum))));
			else bids.add(mappingBid(rows, rownum));
			
			return null;
		});
		
		List<LotAggregate> result = new ArrayList<>(mapLots.size()*2);
		
		for (UUID i:mapLots.keySet()) {
			List<BidVO> bids = mapBids.get(i);
			LotAggregate lot;
			if (bids == null) lot = mapLots.get(i).bids(new ArrayList<>()).build();
			else lot = mapLots.get(i).bids(bids).build();
			result.add(lot);
		}
		return result;
		
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
			        description,
			        title
			    )
			    values (?, ?, ?, ?, ?, ?, ?, ?)

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
			    lot.getState().name(),
			    lot.getDescription(),
			    lot.getTitle());
		
		List<BidVO> bids = lot.getBids();
		if(!bids.isEmpty()) {
			jdbcTemplate.batchUpdate("insert into bids (id, buyer_id, currency, value, lot_id) values (?,?,?,?,?) on conflict(id) do nothing", bids, bids.size(),
					(ps, bid) -> {
						ps.setObject(1, bid.id());
						ps.setObject(2, bid.buyerId());
						ps.setString(3, bid.currency());
						ps.setBigDecimal(4, bid.value());
						ps.setObject(5, lot.getId());
					});
			
		}
				
	}
	
	@Transactional
	@Override
	public void save(List<LotAggregate> lots) {
		jdbcTemplate.batchUpdate(
				"""
			    insert into lots (
			        id,
			        owner_id,
			        timeout,
			        min_bid,
			        currency,
			        status,
			        description,
			        title
			    )
			    values (?, ?, ?, ?, ?, ?, ?, ?)

			    on conflict(id)
			    do update set
			        owner_id = excluded.owner_id,
			        timeout = excluded.timeout,
			        currency = excluded.currency,
			        min_bid = excluded.min_bid,
			        status = excluded.status
			    """, lots, lots.size(), (ps, lot) -> {
			    ps.setObject(1, lot.getId());
			    ps.setObject(2, lot.getOwnerId());
			    ps.setTimestamp(3,(Timestamp.valueOf(lot.getTimeOut())));
			    ps.setBigDecimal(4, lot.getMinBid());
			    ps.setString(5, lot.getCurrency());
			    ps.setString(6, lot.getState().name());
			    ps.setString(7, lot.getDescription());
			    ps.setString(8, lot.getTitle());
		});
		
		List<BidProjection> bids = new ArrayList<>();
		
		for (LotAggregate lot:lots) 
			for (BidVO bid : lot.getBids())
				bids.add(new BidProjection(bid.id(), bid.buyerId(), bid.currency(), bid.value(), lot.getId()));
			
		if(!bids.isEmpty()) {
			jdbcTemplate.batchUpdate("insert into bids (id, buyer_id, currency, value, lot_id) values (?,?,?,?,?) on conflict(id) do nothing", bids, bids.size(),
					(ps, bid) -> {
						ps.setObject(1, bid.id());
						ps.setObject(2, bid.buyer_id());
						ps.setString(3, bid.currency());
						ps.setBigDecimal(4, bid.value());
						ps.setObject(5, bid.lot_id());
					});
			
		}
				
	}
	
	private LotAggregate.LotAggregateBuilder mappingLot(ResultSet row, int rowNum) throws SQLException{
		return LotAggregate.builder()
				.id(UUID.fromString(row.getString("id")))
				.ownerId(UUID.fromString(row.getString("owner_id")))
				.currency(row.getString("currency"))
				.timeout(row.getTimestamp("timeout").toLocalDateTime())
				.minBid(row.getBigDecimal("min_bid"))
				.state(StatusEnum.valueOf(row.getString("status")))
				.description(row.getString("description"));
	}	
	
	private BidVO mappingBid(ResultSet row, int rowNum) throws SQLException {
		return new BidVO(
				UUID.fromString(row.getString("id")), 
				UUID.fromString(row.getString("buyer_id")),
				row.getString("currency"),
				row.getBigDecimal("value"));
		
	}
	
	
}
