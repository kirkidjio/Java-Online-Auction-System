package io.github.etorg.lot.internal.domain;

import java.util.UUID;

import io.github.etorg.lot.internal.domain.exceptions.DomainBidVOException;

public record BidVO(UUID id ,UUID buyerId, String currency, int value) implements Comparable<BidVO> {
    
    public BidVO(UUID id ,UUID buyerId, String currency, int value){
    	this.id = id;
        this.buyerId = buyerId;
        if (value <= 0) throw new DomainBidVOException();
        this.value = value;
        this.currency = currency;
    }
    
    @Override
    public int compareTo(BidVO bid){
        return Integer.compare(this.value, bid.value());
    }
}
