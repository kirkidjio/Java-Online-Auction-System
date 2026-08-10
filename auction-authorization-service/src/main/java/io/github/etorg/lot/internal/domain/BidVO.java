package io.github.etorg.lot.internal.domain;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.etorg.lot.internal.domain.exceptions.DomainBidVOException;

public record BidVO(UUID id ,UUID buyerId, String currency, BigDecimal value) implements Comparable<BidVO> {
    
    public BidVO(UUID id ,UUID buyerId, String currency, BigDecimal value){
    	BigDecimal zero = BigDecimal.valueOf(0);
    	
    	this.id = id;
        this.buyerId = buyerId;
        if (value.compareTo(zero) == -1 || value.compareTo(zero) == 0) throw new DomainBidVOException();
        this.value = value;
        this.currency = currency;
    }
    
    @Override
    public int compareTo(BidVO bid){
        return value.compareTo(bid.value());
    }
}
