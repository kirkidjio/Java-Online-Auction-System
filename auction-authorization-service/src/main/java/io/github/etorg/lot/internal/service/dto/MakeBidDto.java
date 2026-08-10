package io.github.etorg.lot.internal.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MakeBidDto(UUID lotId, String currency, BigDecimal value) {
	
}
