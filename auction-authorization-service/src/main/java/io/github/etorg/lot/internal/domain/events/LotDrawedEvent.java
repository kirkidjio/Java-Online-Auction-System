package io.github.etorg.lot.internal.domain.events;

import io.github.etorg.lot.internal.domain.BidVO;

import java.util.List;
import java.util.UUID;

public record LotDrawedEvent(UUID lotId, String reason, List<BidVO> bids, UUID ownerId, String title)  implements Event {
    
}
