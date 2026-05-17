package io.github.etorg.lot.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.RequestBody;

import io.github.etorg.lot.internal.service.LotService;
import io.github.etorg.lot.internal.service.dto.CreateLotDto;
import io.github.etorg.lot.internal.service.dto.LotCardDto;
import io.github.etorg.lot.internal.service.dto.LotDto;
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
	
	@GetMapping("cards/")
	public List<LotCardDto> getCards(@RequestParam LocalDateTime time) {
		return lotService.getCards(time);
	}
	
	@GetMapping("item/{id}")
	public LotDto getLot(@PathVariable String id) {
		return lotService.getLot(UUID.fromString(id));
	}
	
}
