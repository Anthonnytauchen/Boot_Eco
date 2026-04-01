package project.booteco.pruducer;

import project.booteco.domain.CategoryTransaction;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlyReportResponse(
        BigDecimal totalRevenue,
        BigDecimal totalExpense,
        BigDecimal totalSaved,
        Map<CategoryTransaction,BigDecimal> gastosPorCategoria
) { }
