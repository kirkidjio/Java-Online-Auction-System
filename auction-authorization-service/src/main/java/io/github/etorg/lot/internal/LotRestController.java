package io.github.etorg.lot.internal;

import java.security.Principal;
import java.util.UUID;

import io.github.etorg.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.RequestBody;

import io.github.etorg.lot.internal.service.LotService;
import io.github.etorg.lot.internal.service.dto.CreateLotDto;
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
	@PostMapping("/create/")
	public int createLot(@RequestBody CreateLotDto form, Principal principal) {
		lotService.createLot(UUID.fromString(principal.getName()), form);
		return 201;
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/makebid/")
	public void makeBid(@RequestBody MakeBidDto form, Principal principal) {
		lotService.makeBid(UUID.fromString(principal.getName()), form);
	}

	
	@GetMapping("cards/")
	public LotCardsWithCursorDto getCardsByCursor(@ModelAttribute LotCardQueryDto form) {
		return lotService.getCards(form);
	}
	
	@GetMapping("item/{id}")
	public LotDto getLot(@PathVariable String id) {
		return lotService.getLot(UUID.fromString(id));
	}


/*	@GetMapping("/categories")
	public List<CategoryDto> getCategories() {
		return lotService.getCategories();
	}
	*/

	/**
	 * Security configuration of the lots module.
	 *
	 * The chain serves only /api/lots/** paths; everything inside requires authentication.
	 * When the module is extracted into a microservice, this class moves along with it.
	 * Authorization is performed only at the endpoint (URL) level.
	 */
	@Configuration
	public static class LotsSecurityConfig {

		@Autowired
		JwtFilter jwtFilter;

		/**
		 * Chain of the lots module: any request to /api/lots/** is allowed
		 * only for authenticated users.
		 */
		@Bean
		@Order(2)
		SecurityFilterChain lotsSecurityFilterChain(HttpSecurity http) throws Exception{
			return http
					.securityMatcher("/api/lots/**")
					.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.csrf(csrf -> csrf.disable())
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
					.build();
		}
	}
}
