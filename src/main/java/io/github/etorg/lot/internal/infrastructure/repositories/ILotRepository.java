package io.github.etorg.lot.internal.infrastructure.repositories;

import io.github.etorg.lot.internal.domain.*;


import java.util.UUID;
import java.util.Optional;

public interface ILotRepository {
	Optional<LotAggregate> findById(UUID id);
	void delete(UUID id);
	void save(LotAggregate entity);
}
