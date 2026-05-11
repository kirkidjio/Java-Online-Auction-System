package io.github.etorg.lot.internal.domain.events;
import java.util.UUID;

public record LotClosedEvent(UUID lotId, UUID winnerId, String reason) implements Event {
   
}
