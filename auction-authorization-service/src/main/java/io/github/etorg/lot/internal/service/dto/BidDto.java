package io.github.etorg.lot.internal.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BidDto(UUID id ,String username, String currency, BigDecimal value) {

}
