package io.github.etorg.unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;


import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.domain.exceptions.LotTimeOutException;



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
        LotAggregate lot = lotBuilder.build();
        
        assertEquals(lot.getCurrency(), "pln", "Currency initialized");
        assertEquals(lot.getTimeOut(),LocalDateTime.of(2026, 5,1, 10, 30), "TimeOut initialized");
        assertEquals(lot.getMinBid(), 100, "Min bid initialized");
        assertEquals(lot.getState(), "OPEN", "Start state is OPEN");
    }
    
    @Test 
    public void test_lot_1_creating_lot_with_timeout_earlier_then_current_time_FAIL(){
        
        assertThrows(LotTimeOutException.class, () -> {lotBuilder.setTimeOut(LocalDateTime.of(2025, 5,1, 10, 30)).build();}, "Earlier then current time");
        assertThrows(LotTimeOutException.class, () -> {lotBuilder.setTimeOut(LocalDateTime.of(2027, 5,1, 10, 30)).build();}, "Later then current time plus 6 month");
    }
    
    @Test
    public void test_lot_2_making_bid_with_open_state(){
        LotAggregate lot = lotBuilder.setMinBid(100).build();  // start state is OPEN
        BidVO bid = new BidVO("buyer", "pln", 150);
        
        lot.makeBid(bid);
        
        assertTrue(lot.getBids().contains(bid), "Bid is accepted");
        
        
        
    }
    
    
}
