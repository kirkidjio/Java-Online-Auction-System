package io.github.etorg.lot.internal.domain.events;
import java.util.UUID;

public record BidMakedEvent(UUID lotId, UUID userId, int value) implements Event {

}
