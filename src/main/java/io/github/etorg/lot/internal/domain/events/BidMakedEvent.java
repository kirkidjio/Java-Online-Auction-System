package io.github.etorg.lot.internal.domain.events;
import io.github.etorg.lot.internal.domain.BidVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BidMakedEvent(UUID lotId, UUID ownerId, String title, List<BidVO> bids) implements Event {

}
