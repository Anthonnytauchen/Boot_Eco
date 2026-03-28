package project.booteco.pruducer;



import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import project.booteco.domain.CategoryTransation;
import project.booteco.domain.TypeTransation;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionPostRequest(
        @NotNull(message = "O ID do usuário é obrigatório")
        UUID userId,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal value,
        @Enumerated(EnumType.STRING)
        @NotNull(message = "O tipo da transação não pode ser nulo")
        TypeTransation type,

        @NotNull(message = "A categoria não pode ser nula")
        CategoryTransation categoryTransaction,

        String subcategory
)
{}
