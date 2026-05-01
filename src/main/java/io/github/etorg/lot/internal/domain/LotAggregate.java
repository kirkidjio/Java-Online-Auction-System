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
        this.state = state;
        
        
        if (timeout.isBefore(LocalDateTime.now()) && state.equals("OPEN") && !bids.isEmpty()){close(); updates.add(new LotClosedEvent(id, bids.getLast().buyerId(), "TIMEOUT"));}
        if (timeout.isBefore(LocalDateTime.now()) && state.equals("OPEN") && bids.isEmpty()) {draw(); updates.add(new LotDrawedEvent(id, "TIMEOUT"));}
        
        
    }
    
    private void close(){
        if (!state.equals("OPEN")) throw new DomainLotException("Lot cant be closed when lot status is not OPEN");
        if (bids.isEmpty()) throw new DomainLotException("Lot cant be closed when lot havent bids");
        
        state = "CLOSE";
        
        
    }
    
    private void draw(){
        if (!state.equals("OPEN")) throw new DomainLotException("Lot cant be closed when lot status is not OPEN");
        
        state = "DRAW";
        
        
    }
    
    public void drawByOwner(String user){
        if (!ownerId.equals(user)) throw new DomainLotException("Permission denied for drawing lot");
        draw();
        updates.add(new LotDrawedEvent(id, "OWNER"));
        
    }
    
    public void closeByOwner(String user){
        if (!ownerId.equals(user)) throw new DomainLotException("Permission denied for closing lot");
        close();
        updates.add(new LotClosedEvent(id, bids.getLast().buyerId(), "OWNER"));
        
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
    public String getId() {return id;}
    public LocalDateTime getTimeOut() {return timeout;}
    public int getMinBid() {return minBid;}
    public String getCurrency() {return currency;}
    public String getState() {return state;}
    public String getOwnerId(){return ownerId; }
    public ArrayList<BidVO> getBids() {return new ArrayList<>(bids);}
    public ArrayList<Event> getUpdates() {return new ArrayList<>(updates);}
}
