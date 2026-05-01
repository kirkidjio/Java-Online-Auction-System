package io.github.etorg.lot.internal.domain.events;


public record LotDrawedEvent(String lotId, String reason) implements Event {

}
