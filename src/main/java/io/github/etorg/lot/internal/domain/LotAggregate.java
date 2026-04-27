package io.github.etorg.lot.internal.domain;

import java.time.LocalDateTime;
import io.github.etorg.lot.internal.domain.exceptions.LotTimeOutException;

import java.util.*;

public class LotAggregate {
    private String id;
    private String ownerId;
    private LocalDateTime timeout;
    private int minBid;
    private String currency;
    private String state; 
    
    public LotAggregate(String id, String ownerId, String currency,LocalDateTime timeout, int minBid){
        this.id = id;
        this.timeout = timeout;
        this.minBid = minBid;
        this.currency = currency;
        this.state = "OPEN";
        this.ownerId = ownerId;
        
        if (timeout.isBefore(LocalDateTime.now()) || timeout.isAfter(LocalDateTime.now().plusMonths(6))){
            throw new LotTimeOutException();
        }
        
    }
    
    // GETTERS
    public LocalDateTime getTimeOut() {return timeout;}
    public int getMinBid() {return minBid;}
    public String getCurrency() {return currency;}
    public String getState() {return state;}
    public String getOwnerId(){return ownerId; }
}
