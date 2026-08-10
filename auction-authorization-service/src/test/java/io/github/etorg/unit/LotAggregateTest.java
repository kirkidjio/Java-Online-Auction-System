package io.github.etorg.unit;

import io.github.etorg.lot.internal.domain.events.LotClosedEvent;
import io.github.etorg.lot.internal.domain.events.LotDrawedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


import io.github.etorg.lot.internal.domain.LotAggregate;
import io.github.etorg.lot.internal.domain.StatusEnum;
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
    	UUID lotId = UUID.randomUUID();
    	UUID ownerId = UUID.randomUUID();
    	
        LocalDateTime timeout = LocalDateTime.now().plusMonths(5);
        LotAggregate lot = new LotAggregate(lotId, ownerId, "pln", timeout, "bebra" ,BigDecimal.valueOf(100), "test");
        
        assertEquals(lot.getCurrency(), "pln", "Currency initialized");
        assertEquals(lot.getTimeOut(),timeout, "TimeOut initialized");
        assertEquals(lot.getMinBid(), BigDecimal.valueOf(100), "Min bid initialized");
        assertEquals(lot.getState(), StatusEnum.OPEN, "Start state is OPEN");
    }
    
    @Test 
    public void test_lot_1_creating_lot_with_timeout_earlier_then_current_time_FAIL(){
    	UUID lotId = UUID.randomUUID();
    	UUID ownerId = UUID.randomUUID();
    	
        LocalDateTime timeoutLate = LocalDateTime.now().plusMonths(7);
        LocalDateTime timeoutEarly = LocalDateTime.now().minusDays(1);
        
        
        assertThrows(DomainLotException.class, () -> {
        	new LotAggregate(lotId, ownerId, "pln", timeoutLate, "bebra" ,BigDecimal.valueOf(100), "test");},
        		"Setted timeout earlier then current time");
        assertThrows(DomainLotException.class, () -> {
        	new LotAggregate(lotId, ownerId, "pln", timeoutEarly, "bebra" ,BigDecimal.valueOf(100), "test");},
        		"Setted timeout later then current time plus 6 month");
    }
    
    @Test
    public void test_lot_2_making_bid_with_open_state(){
        LotAggregate lot = lotBuilder.setMinBid(BigDecimal.valueOf(100)).setCurrency("pln").build();  // start state is OPEN
        BidVO bid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(150));
        
        lot.makeBid(bid);
        
        assertTrue(lot.getBids().contains(bid), "Bid is accepted");
        
    }
    
    @Test
    public void test_lot_2_making_bid_when_timeout_FAIL(){
        LotAggregate lot = lotBuilder.setMinBid(BigDecimal.valueOf(100)).setCurrency("pln").setTimeOut(LocalDateTime.now().minusDays(1)).build();  // start state is OPEN
        BidVO bid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(150));
        
        assertThrows(DomainLotException.class, () -> lot.makeBid(bid));
        
       
        
    }
    
    @Test
    public void test_lot_2_making_bid_with_not_open_state_FAIL(){
        LotAggregate lot = lotBuilder.setMinBid(BigDecimal.valueOf(100)).setState(StatusEnum.CLOSED).build();  
        BidVO bid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(150));
        
        
        assertThrows(DomainLotException.class, () -> {lot.makeBid(bid);});
    }
    
    @Test 
    public void test_lot_2_making_bid_with_currency_another_then_lot_FAIL(){
        LotAggregate lot = lotBuilder.setMinBid(BigDecimal.valueOf(100)).setCurrency("usd").build();  
        BidVO bid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(150));
        
        
        assertThrows(DomainLotException.class, () -> {lot.makeBid(bid);});
    }
    
    @Test
    public void test_lot_2_making_bid_when_bid_less_then_minimal_bid_FAIL(){
        LotAggregate lot = lotBuilder.setMinBid(BigDecimal.valueOf(200)).setCurrency("pln").build();  
        BidVO bid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(150));
        
        
        assertThrows(DomainLotException.class, () -> {lot.makeBid(bid);});
    }
    
    @Test
    public void test_lot_2_making_bid_when_bid_less_then_maximum_bid_FAIL(){
        BidVO maxBid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(300));
        ArrayList<BidVO> bids = new ArrayList<>();
        bids.add(maxBid);
        LotAggregate lot = lotBuilder
                .setMinBid(BigDecimal.valueOf(200))
                .setCurrency("pln")
                .setBids(bids)
                .build();
        
        BidVO bid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(250));
        
        
        assertThrows(DomainLotException.class, () -> {lot.makeBid(bid);});
    }
    
    
    @Test
    public void test_lot_3_closing_lot_by_owner(){
        BidVO maxBid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(300));
        ArrayList<BidVO> bids = new ArrayList<>();
        bids.add(maxBid);
        LotAggregate lot = lotBuilder
                .setTimeOut(LocalDateTime.now().plusDays(5))
                .setBids(bids)
                .build();
        
        lot.closeByOwner(lot.getOwnerId());
        
        assertEquals(lot.getState(), StatusEnum.CLOSED);
        assertTrue(lot.getUpdates().contains(new LotClosedEvent(lot.getId(), maxBid.buyerId() ,"OWNER", lot.getBids(), lot.getOwnerId(), lot.getTitle())));
    }
    
    @Test
    public void test_lot_3_closing_lot_by_NOT_owner_FAIL(){
        BidVO maxBid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(300));
        ArrayList<BidVO> bids = new ArrayList<>();
        bids.add(maxBid);
        LotAggregate lot = lotBuilder
                .setTimeOut(LocalDateTime.now().plusDays(5))
                .setBids(bids)
                .build();
        
        
        
        assertThrows(DomainLotException.class, () -> {lot.closeByOwner(UUID.randomUUID());});
    }
    
    @Test
    public void test_lot_4_drawing_lot_by_owner(){
        BidVO maxBid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(300));
        ArrayList<BidVO> bids = new ArrayList<>();
        bids.add(maxBid);
        LotAggregate lot = lotBuilder
                .setTimeOut(LocalDateTime.now().plusDays(5))
                .setBids(bids)
                .build();
        
        lot.drawByOwner(lot.getOwnerId());
        
        assertEquals(lot.getState(), StatusEnum.DRAW);
        assertTrue(lot.getUpdates().contains(new LotDrawedEvent(lot.getId(), "OWNER", lot.getBids(), lot.getOwnerId(), lot.getTitle())));
    }
    
    @Test
    public void test_lot_4_drawing_lot_by_NOT_owner_FAIL(){
        BidVO maxBid = new BidVO(UUID.randomUUID() ,UUID.randomUUID(), "pln", BigDecimal.valueOf(300));
        ArrayList<BidVO> bids = new ArrayList<>();
        bids.add(maxBid);
        LotAggregate lot = lotBuilder
                .setTimeOut(LocalDateTime.now().plusDays(5))
                .setBids(bids)
                .build();
        
        
        
        assertThrows(DomainLotException.class, () -> {lot.drawByOwner(UUID.randomUUID());});
    }
    
    
}
