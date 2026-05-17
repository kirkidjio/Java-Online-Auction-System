package io.github.etorg.lot.internal.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.github.etorg.lot.internal.service.dto.LotCardDto;
import io.github.etorg.lot.internal.service.dto.LotDto;

public interface ILotQueryRepository {
	List<LotCardDto> getLastCreatedCards(LocalDateTime time);
	public LotDto getLot(UUID id);
	
}
