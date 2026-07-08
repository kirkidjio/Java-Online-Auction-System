package io.github.etorg.lot.internal.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.github.etorg.lot.internal.domain.events.BidMakedEvent;
import io.github.etorg.lot.internal.domain.events.Event;
import io.github.etorg.lot.internal.domain.events.LotClosedEvent;
import io.github.etorg.lot.internal.domain.events.LotDrawedEvent;
import io.github.etorg.lot.internal.domain.exceptions.DomainLotException;
import lombok.Builder;


import java.util.*;

public class LotAggregate {
    private UUID id;
    private String title;
    private UUID ownerId;
    private LocalDateTime timeout;
    private BigDecimal minBid;
    private String currency;
    private StatusEnum state;
    private String description;
    private List<BidVO> bids;
    private ArrayList<Event> updates = new ArrayList<>();
    
    
    
    public LotAggregate(UUID id, UUID ownerId, String currency,LocalDateTime timeout, String description ,BigDecimal minBid, String title){
        this.id = id;
        this.timeout = timeout;
        this.minBid = minBid;
        this.currency = currency;
        this.state = StatusEnum.OPEN;
        this.ownerId = ownerId;
        this.bids = new ArrayList<>();
        this.description = description;
        this.title = title;
        
        if (timeout.isBefore(LocalDateTime.now()) || timeout.isAfter(LocalDateTime.now().plusMonths(6))){
            throw new DomainLotException("TimeOut cant be earlier then now");
        }
        
    }
    
    @Builder
    public LotAggregate(UUID id, UUID ownerId, String currency,LocalDateTime timeout, BigDecimal minBid, StatusEnum state, List<BidVO> bids, String description, String title){
        this.id = id;
        this.timeout = timeout;
        this.minBid = bids.isEmpty() ? minBid : Collections.max(bids).value();
        this.currency = currency;
        
        this.ownerId = ownerId;
        this.bids = bids;
        
        this.state = state;
        this.description = description;
        this.title = title;
        
    }
    
    private boolean isTimeout() {
    	if (timeout.isBefore(LocalDateTime.now())){
    		return true;
    	}
    	return false;
    }
    
    private void close(String reason){
        if (!state.equals(StatusEnum.OPEN)) throw new DomainLotException("Lot cant be closed when lot status is not OPEN");
        if (bids.isEmpty()) throw new DomainLotException("Lot cant be closed when lot havent bids");
        
        state = StatusEnum.CLOSED;
        updates.add(new LotClosedEvent(id, Collections.max(bids).buyerId(), reason, bids, ownerId, title));
        
        
    }
    
    private void draw(String reason){
        if (!state.equals(StatusEnum.OPEN)) throw new DomainLotException("Lot cant be drawed when lot status is not OPEN");
        
        state = StatusEnum.DRAW;
        updates.add(new LotDrawedEvent(id, reason, bids, ownerId, title));
        
    }
    
    public void drawByOwner(UUID user){
    	if (isTimeout()) throw new DomainLotException("Lot cant be drawed by user when timeout");
        if (!ownerId.equals(user)) throw new DomainLotException("Permission denied for drawing lot");
        draw("OWNER");
    }
    
    public void closeByOwner(UUID user){
    	if (isTimeout()) throw new DomainLotException("Lot cant be close by user when timeout");
        if (!ownerId.equals(user)) throw new DomainLotException("Permission denied for closing lot");
        close("OWNER");
        
    }
    
    public void changeStateAfterTimeout() {
    	if (isTimeout()) {
    		if (bids.isEmpty()) draw("TIMEOUT");
    		else close("TIMEOUT");
    	}
    }
    
    public void makeBid(BidVO bid) {
    	if (isTimeout()) throw new DomainLotException("Bid cant be made when timeout");
        if (!state.equals(StatusEnum.OPEN)) throw new DomainLotException("Bid cant be made when lot status is not OPEN");
        if (!bid.currency().equals(currency)) throw new DomainLotException("Bid currency must be the same as lot currency");
    	if (bid.value().compareTo(minBid) == -1) throw new DomainLotException("Bid cant be less then minimal allowed");
        
        
        
        bids.add(bid);
        minBid = bid.value().multiply(BigDecimal.valueOf(1.05)); 
        updates.add(new BidMakedEvent(id, ownerId, title, bids));
        
        
    	
    }
    
    // GETTERS
    public UUID getId() {return id;}
    public LocalDateTime getTimeOut() {return timeout;}
    public BigDecimal getMinBid() {return minBid;}
    public String getCurrency() {return currency;}
    public StatusEnum getState() {return state;}
    public UUID getOwnerId(){return ownerId; }
    public ArrayList<BidVO> getBids() {return new ArrayList<>(bids);}
    public String getDescription() {return description;}
    public ArrayList<Event> getUpdates() {return new ArrayList<>(updates);}
    public String getTitle() {return title;}
}
