package io.github.etorg.lot.internal.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateLotDto(String currency,LocalDateTime timeout, String description ,BigDecimal minBid, String title) {

}
