package project.booteco.pruducer;

import project.booteco.domain.CategoryTransation;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlyReportResponse(
        BigDecimal totalRevenue,
        BigDecimal totalExpense,
        BigDecimal totalSaved,
        Map<CategoryTransation,BigDecimal> gastosPorCategoria
) { }
