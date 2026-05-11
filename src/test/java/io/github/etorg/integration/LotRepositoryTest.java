package io.github.etorg.integration;

import static org.junit.jupiter.api.Assertions.*;

import io.github.etorg.unit.LotBuilder;
import org.junit.jupiter.api.*;

import io.github.etorg.lot.internal.domain.*;
import io.github.etorg.lot.internal.infrastructure.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.UUID;

@SpringBootTest
class LotRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LotBuilder builder;
    
    @Test
    void test() {
    	UUID lot_id = UUID.randomUUID();
    	List<BidVO> bids = Arrays.asList(
    			new BidVO(UUID.randomUUID() ,UUID.fromString("6e6b87ca-9763-4337-b149-c511e90d2174"), "pln", 300),
    			new BidVO(UUID.randomUUID() ,UUID.fromString("6e6b87ca-9763-4337-b149-c511e90d2174"), "pln", 400)
    			);
    	
    	builder = new LotBuilder().setOwnerId(UUID.fromString("bd7ed1cc-4056-475f-a47a-7cf15d8ca08b")).setId(lot_id);
    	
    	LotAggregate lot = builder.setDescription("furry suit").setMinBid(750).setBids(bids).build();

        LotJdbcRepository rep =
                new LotJdbcRepository(jdbcTemplate);

        
        rep.save(lot);  
        
        Optional<LotAggregate> result =
                rep.findById(lot_id);

        System.out.println(result.get());
        assertEquals(result.get().getCurrency(), "pln");
    }
}
