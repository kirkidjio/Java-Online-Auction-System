package io.github.etorg.lot.internal.domain;

import java.time.LocalDateTime;
import io.github.etorg.lot.internal.domain.exceptions.DomainLotException;
import io.github.etorg.lot.internal.domain.events.*;
import java.util.stream.IntStream;

import java.util.*;

public class LotAggregate {
    private String id;
    private String ownerId;
    private LocalDateTime timeout;
    private int minBid;
    private String currency;
    private String state;
    private ArrayList<BidVO> bids;
    private ArrayList<Event> updates = new ArrayList<>();
    
    
    public LotAggregate(String id, String ownerId, String currency,LocalDateTime timeout, int minBid){
        this.id = id;
        this.timeout = timeout;
        this.minBid = minBid;
        this.currency = currency;
        this.state = "OPEN";
        this.ownerId = ownerId;
        this.bids = new ArrayList<>();
        
        if (timeout.isBefore(LocalDateTime.now()) || timeout.isAfter(LocalDateTime.now().plusMonths(6))){
            throw new DomainLotException("TimeOut cant be earlier then now");
        }
        
    }
    
    public LotAggregate(String id, String ownerId, String currency,LocalDateTime timeout, int minBid, String state, ArrayList<BidVO> bids){
        this.id = id;
        this.timeout = timeout;
        this.minBid = minBid;
        this.currency = currency;
        
        this.ownerId = ownerId;
        this.bids = bids;
        this.bids.sort(null);
        
        if (timeout.isBefore(LocalDateTime.now()) || timeout.isAfter(LocalDateTime.now().plusMonths(6))){
            throw new DomainLotException("TimeOut cant be earlier then now");
        }
        
        // if (timeout.isBefore(LocalDateTime.now()) && state.equals("OPEN"))
        
        this.state = state;
        
    }
    
    public void makeBid(BidVO bid) {
        if (!state.equals("OPEN")) throw new DomainLotException("Bid cant be maked when lot status is not OPEN");
        if (!bid.currency().equals(currency)) throw new DomainLotException("Bid currency must be the same as lot currency");
    	if (bids.isEmpty() && bid.value() < minBid) throw new DomainLotException("Bid cant be less then minimal allowed");
        if (!bids.isEmpty()){
            if (bids.getLast().value()*1.05 > bid.value()) throw new DomainLotException("Bid cant be less then maximum bid");
        }
        bids.add(bid);
        updates.add(new BidMakedEvent(id, bid.buyerId(), bid.value()));
        
        
    	
    }
    
    // GETTERS
    public LocalDateTime getTimeOut() {return timeout;}
    public int getMinBid() {return minBid;}
    public String getCurrency() {return currency;}
    public String getState() {return state;}
    public String getOwnerId(){return ownerId; }
    public ArrayList<BidVO> getBids() {return new ArrayList<>(bids);}
    public ArrayList<Event> getUpdates() {return new ArrayList<>(updates);}
}
