package io.github.etorg.lot.internal.domain.events;

import java.util.UUID;

public record LotDrawedEvent(UUID lotId, String reason) implements Event {
    
}
