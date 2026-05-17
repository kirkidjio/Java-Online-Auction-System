package io.github.etorg.lot.internal.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LotDto(UUID id, UUID ownerId, LocalDateTime timeout, String description, LocalDateTime created_at, int min_bid, String currency, String status, String title ) {

}
