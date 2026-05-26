package io.github.etorg;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.etorg.lot.internal.service.LotService;
import io.github.etorg.users.service.AuthenticationService;
import io.github.etorg.users.service.dto.RegisterUserDto;

@Controller("/")
public class UiController {
	
	@Autowired
	LotService lotService;
	
	@Autowired
	AuthenticationService authService;

	@GetMapping("/auth")
	public String getAuthPage () {
		return "auth";
	}

	
	
	@GetMapping("lots/{id}")
	public String getLotInfo(Model model, @PathVariable String id) {
		model.addAttribute("lot", lotService.getLot(UUID.fromString(id)));
		return "lot";
	} 
	
	
}
