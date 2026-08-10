package io.github.etorg.lot.internal.service.dto;

import java.util.List;

public record LotCardsWithCursorDto(List<LotCardDto> lotcards, String cursor) {
	
}
