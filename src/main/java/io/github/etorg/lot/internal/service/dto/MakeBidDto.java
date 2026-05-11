package io.github.etorg.lot.internal.service.dto;

import java.util.UUID;

public record MakeBidDto(UUID lotId, String currency, int value) {
	
}
