package io.github.etorg.lot.internal.infrastructure.dto;

import java.time.LocalDateTime;



public record LotDTO(
	String id,
	String ownerId,
	LocalDateTime timeout,
	LocalDateTime createdAt,
	int minBid,
	String currency,
	String state)
{}
