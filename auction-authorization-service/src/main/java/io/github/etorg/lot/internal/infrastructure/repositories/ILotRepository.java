package io.github.etorg.lot.internal.infrastructure.repositories;

import io.github.etorg.lot.internal.domain.*;


import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface ILotRepository {
	Optional<LotAggregate> findById(UUID id);
	List<LotAggregate> findWithTimeout();
	void delete(UUID id);
	void save(LotAggregate lot);
	void save(List<LotAggregate> lots);
}
