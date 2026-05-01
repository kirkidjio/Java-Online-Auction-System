package io.github.etorg.lot.internal.domain.events;


public record LotClosedEvent(String lotId, String winnerId, String reason) implements Event {
   
}
