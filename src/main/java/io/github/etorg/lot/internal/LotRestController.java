package io.github.etorg.lot.internal;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.RequestBody;

import io.github.etorg.lot.internal.service.LotService;
import io.github.etorg.lot.internal.service.dto.CreateLotDto;
import io.github.etorg.lot.internal.service.dto.LotCardDto;
import io.github.etorg.lot.internal.service.dto.LotCardQueryDto;
import io.github.etorg.lot.internal.service.dto.LotCardsWithCursorDto;
import io.github.etorg.lot.internal.service.dto.LotDto;
import io.github.etorg.lot.internal.service.dto.MakeBidDto;



@RestController
@RequestMapping("/api/lots")
public class LotRestController {
    
	@Autowired
	LotService lotService;
	
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/create/")
	public int createLot(@RequestBody CreateLotDto form, Principal principal) {
		lotService.createLot(UUID.fromString(principal.getName()), form);
		return 201;
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/makebid/{userId}")
	public int makeBid(@RequestBody MakeBidDto form, @PathVariable String userId, Principal principal) {
		lotService.makeBid(UUID.fromString(principal.getName()), form);
		return 201;
	}
	
	@GetMapping("cards/")
	public LotCardsWithCursorDto getCards(
			@RequestParam String attribute,
		    @RequestParam String order,
		    @RequestParam String cursor) {
		return lotService.getCards(new LotCardQueryDto(attribute, order, cursor));
	}
	
	@GetMapping("item/{id}")
	public LotDto getLot(@PathVariable String id) {
		return lotService.getLot(UUID.fromString(id));
	}
	
	@DeleteMapping("item/delete/{id}")
	public void deleteLot(@PathVariable String id) {
		lotService.deleteLot(UUID.fromString(id));
	}
	
}
