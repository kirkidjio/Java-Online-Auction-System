package io.github.etorg.lot.api.events;
import java.util.UUID;

public record LotClosedEvent(UUID lotId, UUID winnerId, String reason) implements Event {
   
}
