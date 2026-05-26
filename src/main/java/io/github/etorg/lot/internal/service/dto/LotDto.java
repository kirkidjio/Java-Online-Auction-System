package io.github.etorg.lot.internal.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder(builderClassName = "Builder")
public record LotDto(UUID id, UUID ownerId, LocalDateTime timeout, String description, LocalDateTime created_at, BigDecimal min_bid, String currency, String status, String title, List<BidDto> bids, String category) {

}
