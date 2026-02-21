package project.booteco.pruducer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import project.booteco.domain.CategoryTransation;

import java.math.BigDecimal;

public record TransationPutResponse(
        @NotBlank(message = "O código curto da transação é obrigatório para atualização")
        String codigoCurto,

        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valor,

        CategoryTransation categoria,

        String subcategoria
) {
}
