package io.github.etorg.lot.internal.infrastructure.repositories.projections;

import java.math.BigDecimal;
import java.util.UUID;

public record BidProjection(UUID id, UUID buyer_id, String currency, BigDecimal value, UUID lot_id) {

}
