package io.github.etorg.lot.api.events;
import java.math.BigDecimal;
import java.util.UUID;

public record BidMakedEvent(UUID lotId, UUID userId, BigDecimal value, String currency) implements Event {

}
