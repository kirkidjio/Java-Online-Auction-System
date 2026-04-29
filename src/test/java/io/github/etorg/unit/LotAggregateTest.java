package io.github.etorg.unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.domain.BidVO;
import io.github.etorg.lot.internal.domain.exceptions.DomainLotException;



public class LotAggregateTest {
    
    LotBuilder lotBuilder;
    
    public LotAggregateTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        lotBuilder = new LotBuilder();
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void test_lot_1_creating_lot_with_currency_and_time_and_min_bid(){
        LocalDateTime timeout = LocalDateTime.now().plusMonths(5);
        LotAggregate lot = lotBuilder
                .setCurrency("pln")
                .setTimeOut(timeout)
                .setMinBid(100)
                .setState("OPEN")
                .build();
        
        assertEquals(lot.getCurrency(), "pln", "Currency initialized");
        assertEquals(lot.getTimeOut(),timeout, "TimeOut initialized");
        assertEquals(lot.getMinBid(), 100, "Min bid initialized");
        assertEquals(lot.getState(), "OPEN", "Start state is OPEN");
    }
    
    @Test 
    public void test_lot_1_creating_lot_with_timeout_earlier_then_current_time_FAIL(){
        
        assertThrows(DomainLotException.class, () -> {lotBuilder.setTimeOut(LocalDateTime.of(2025, 5,1, 10, 30)).build();}, "Earlier then current time");
        assertThrows(DomainLotException.class, () -> {lotBuilder.setTimeOut(LocalDateTime.of(2027, 5,1, 10, 30)).build();}, "Later then current time plus 6 month");
    }
    
    @Test
    public void test_lot_2_making_bid_with_open_state(){
        LotAggregate lot = lotBuilder.setMinBid(100).setCurrency("pln").build();  // start state is OPEN
        BidVO bid = new BidVO("buyer", "pln", 150);
        
        lot.makeBid(bid);
        
        assertTrue(lot.getBids().contains(bid), "Bid is accepted");
        
    }
    
    @Test
    public void test_lot_2_making_bid_with_not_open_state_FAIL(){
        LotAggregate lot = lotBuilder.setMinBid(100).setState("CLOSE").build();  
        BidVO bid = new BidVO("buyer", "pln", 150);
        
        
        assertThrows(DomainLotException.class, () -> {lot.makeBid(bid);});
    }
    
    @Test 
    public void test_lot_2_making_bid_with_currency_another_then_lot_FAIL(){
        LotAggregate lot = lotBuilder.setMinBid(100).setCurrency("usd").build();  
        BidVO bid = new BidVO("buyer", "pln", 150);
        
        
        assertThrows(DomainLotException.class, () -> {lot.makeBid(bid);});
    }
    
    @Test
    public void test_lot_2_making_bid_when_bid_less_then_minimal_bid_FAIL(){
        LotAggregate lot = lotBuilder.setMinBid(200).setCurrency("pln").build();  
        BidVO bid = new BidVO("buyer", "pln", 150);
        
        
        assertThrows(DomainLotException.class, () -> {lot.makeBid(bid);});
    }
    
    @Test
    public void test_lot_2_making_bid_when_bid_less_then_maximum_bid_FAIL(){
        BidVO maxBid = new BidVO("user", "pln", 300);
        ArrayList<BidVO> bids = new ArrayList<>();
        bids.add(maxBid);
        LotAggregate lot = lotBuilder
                .setMinBid(200)
                .setCurrency("pln")
                .setBids(bids)
                .build();
        
        BidVO bid = new BidVO("buyer", "pln", 250);
        
        
        assertThrows(DomainLotException.class, () -> {lot.makeBid(bid);});
    }
}
