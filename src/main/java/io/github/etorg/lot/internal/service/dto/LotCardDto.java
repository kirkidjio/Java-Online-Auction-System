package io.github.etorg.lot.internal.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LotCardDto(String title, BigDecimal min_bid, String currency, LocalDateTime timeout, LocalDateTime created_at) {

}
