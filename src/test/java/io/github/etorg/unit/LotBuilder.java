package io.github.etorg.unit;

import java.time.LocalDateTime;
import io.github.etorg.lot.internal.domain.LotAggregate;


public class LotBuilder {
    private String id;
    private String ownerId;
    private LocalDateTime timeout;
    private int minBid;
    private String currency;
    private String state; 
    
    public LotBuilder(){
        this.id = "lot";
        this.timeout = LocalDateTime.of(2026, 5,1, 10, 30);
        this.minBid = 100;
        this.currency = "pln";
        this.state = "OPEN";
        this.ownerId = "owner";
    }
    
    public LotBuilder setId(String id){
        this.id = id;
        return this;
    }
    
    public LotBuilder setTimeOut(LocalDateTime timeout){
        this.timeout = timeout;
        return this;
    }
    
    public LotBuilder setMinBid(int minBid){
        this.minBid = minBid;
        return this;
    }
    
    public LotBuilder setCurrency(String currency){
        this.currency = currency;
        return this;
    }
    
    public LotBuilder setState(String state){
        this.state = state;
        return this;
    }
    
    public LotBuilder setOwnerId(String ownerId){
        this.ownerId = ownerId;
        return this;
    }
    
    public LotAggregate build(){
        return new LotAggregate(id,ownerId, currency, timeout, minBid);
    }
    
    
}
