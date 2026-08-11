package io.github.etorg.lot.internal.infrastructure.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.github.etorg.lot.internal.infrastructure.repositories.enums.LotAttributeSort;
import io.github.etorg.lot.internal.infrastructure.repositories.enums.Order;
import io.github.etorg.lot.internal.service.dto.CategoryDto;
import io.github.etorg.lot.internal.service.dto.LotCardDto;
import io.github.etorg.lot.internal.service.dto.LotDto;

public interface ILotQueryRepository {
	// cursorId is the id of the last card of the previous page; together with the cursor
	// value it forms the keyset needed to paginate without skipping or duplicating rows.
	List<LotCardDto> getSortedCards(LotAttributeSort intAtribute, Order order, BigDecimal cursor, UUID cursorId);
	List<LotCardDto> getSortedCards(LotAttributeSort timeAtribute, Order order, LocalDateTime cursor, UUID cursorId);
	List<LotCardDto> getSortedCards(LotAttributeSort Atribute, Order order);
	List<CategoryDto> getCategories();
	
	
	public LotDto getLot(UUID id);
	
}
