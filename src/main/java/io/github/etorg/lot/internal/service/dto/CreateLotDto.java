package io.github.etorg.lot.internal.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateLotDto(String currency,LocalDateTime timeout, String description ,int minBid) {

}
