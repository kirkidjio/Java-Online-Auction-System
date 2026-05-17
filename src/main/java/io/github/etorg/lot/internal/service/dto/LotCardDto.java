package io.github.etorg.lot.internal.service.dto;

import java.time.LocalDateTime;

public record LotCardDto(String title, int min_bid, String currency, LocalDateTime timeout) {

}
