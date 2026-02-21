package project.booteco.pruducer;

import project.booteco.domain.StateConversation;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserGetResponse(
        UUID id,
        String phoneWhatsapp,
        String emailGoogle,
        String urlGraphic ,
        StateConversation stateConversation,
        String objetivoTextoLivre,
        LocalDateTime dateTime) {
}
