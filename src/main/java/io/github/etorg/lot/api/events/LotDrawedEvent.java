package io.github.etorg.lot.api.events;

import java.util.UUID;

public record LotDrawedEvent(UUID lotId, String reason) implements Event {
    
}
