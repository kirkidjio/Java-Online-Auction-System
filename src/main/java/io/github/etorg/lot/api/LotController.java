package io.github.etorg.lot.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.etorg.lot.internal.service.LotService;
import io.github.etorg.lot.internal.service.dto.CreateLotDto;
import io.github.etorg.lot.internal.service.dto.MakeBidDto;



@RestController
@RequestMapping("/lots")
public class LotController {
    
	@Autowired
	LotService lotService;
	
	@PostMapping("/create/{userId}")
	public String createLot(@RequestBody CreateLotDto form, @PathVariable String userId) {
		lotService.createLot(UUID.fromString(userId), form.currency(), form.timeout(), form.description(), form.minBid());
		return "Lot created :)";
		
	}
	
	@PostMapping("/makebid/{userId}")
	public String makeBid(@RequestBody MakeBidDto form, @PathVariable String userId) {
		lotService.makeBid(UUID.fromString(userId), form.lotId(), form.currency(), form.value());
		return "Bid made :)";
	}
	
	
}
