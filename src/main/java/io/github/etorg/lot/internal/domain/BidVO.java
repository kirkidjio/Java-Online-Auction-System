package io.github.etorg.lot.internal.domain;

import io.github.etorg.lot.internal.domain.exceptions.DomainBidVOException;

public record BidVO(String buyerId, String currency, int value) implements Comparable<BidVO> {
    
    public BidVO(String buyerId, String currency, int value){
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
