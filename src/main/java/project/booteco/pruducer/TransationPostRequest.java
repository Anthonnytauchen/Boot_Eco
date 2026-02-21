package project.booteco.pruducer;



import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import project.booteco.domain.CategoryTransation;
import project.booteco.domain.TypeTransation;

import java.math.BigDecimal;
import java.util.UUID;

public record TransationPostRequest(
        @NotNull(message = "O ID do usuário é obrigatório")
        UUID usuarioId,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "O tipo da transação não pode ser nulo")
        TypeTransation typeTransacao,

        @NotNull(message = "A categoria não pode ser nula")
        CategoryTransation categoryTransation,

        String subcategoria
)
{}
