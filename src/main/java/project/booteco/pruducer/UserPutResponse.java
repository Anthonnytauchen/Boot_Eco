package project.booteco.pruducer;

import jakarta.validation.constraints.NotNull;
import project.booteco.domain.StateConversation;

import java.util.UUID;

public record UserPutResponse(
        @NotNull(message = "O ID do utilizador é obrigatório para atualização")
        UUID id,
        String emailGoogle,
        String urlGraphic,
        StateConversation stateConversation,
        String objectiveTextFree
) {

}
