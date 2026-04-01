package project.booteco.pruducer;

import project.booteco.domain.CategoryTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionGetResponse(
        UUID id,
        String shortCode,
        BigDecimal value,
        String type,
        CategoryTransaction categoryTransaction,
        String subcategory,
        LocalDateTime date,
        String status
) {
}
