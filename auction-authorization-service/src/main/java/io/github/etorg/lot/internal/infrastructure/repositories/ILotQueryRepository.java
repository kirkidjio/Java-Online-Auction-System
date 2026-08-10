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
	List<LotCardDto> getSortedCards(LotAttributeSort intAtribute, Order order, BigDecimal cursor);
	List<LotCardDto> getSortedCards(LotAttributeSort timeAtribute, Order order, LocalDateTime cursor);
	List<LotCardDto> getSortedCards(LotAttributeSort Atribute, Order order);
	List<CategoryDto> getCategories();
	
	
	public LotDto getLot(UUID id);
	
}
