package project.booteco.pruducer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPostRequest(
        @NotBlank(message = "O telefone do WhatsApp é obrigatório")
        @Size(max = 20, message = "O telefone não pode exceder 20 caracteres")
        String telefoneWhatsapp
) {
}
