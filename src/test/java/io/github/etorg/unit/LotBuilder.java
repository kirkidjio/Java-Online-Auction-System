package io.github.etorg.unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.domain.StatusEnum;
import io.github.etorg.lot.internal.domain.BidVO;

import java.util.*;


public class LotBuilder {
    private UUID id;
    private UUID ownerId;
    private LocalDateTime timeout;
    private BigDecimal minBid;
    private String currency;
    private StatusEnum state; 
    private List<BidVO> bids;
    private String description = "123";
    private String title = "test";
    
    public LotBuilder(){
        this.id = UUID.randomUUID();
        this.timeout = LocalDateTime.of(2026, 10,1, 10, 30);
        this.minBid = BigDecimal.valueOf(100);
        this.currency = "pln";
        this.state = StatusEnum.OPEN;
        this.ownerId = UUID.randomUUID();
        this.bids = new ArrayList<>();
    }
    
    public LotBuilder setId(UUID id){
        this.id = id;
        return this;
    }
    
    public LotBuilder setTimeOut(LocalDateTime timeout){
        this.timeout = timeout;
        return this;
    }
    
    public LotBuilder setMinBid(BigDecimal minBid){
        this.minBid = minBid;
        return this;
    }
    
    public LotBuilder setCurrency(String currency){
        this.currency = currency;
        return this;
    }
    
    public LotBuilder setState(StatusEnum state){
        this.state = state;
        return this;
    }
    
    public LotBuilder setOwnerId(UUID ownerId){
        this.ownerId = ownerId;
        return this;
    }
    
    public LotBuilder setBids(List<BidVO> bids){
        this.bids = bids;
        return this;
    }
    
    public LotBuilder setDescription(String description) {
    	this.description = description;
    	return this;
    }
    
    public LotAggregate build(){
        return new LotAggregate(id,ownerId, currency, timeout, minBid, state, bids, description, title);
    }
    
    
}
