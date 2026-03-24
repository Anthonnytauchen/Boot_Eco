package project.booteco.pruducer;

import jakarta.validation.constraints.NotBlank;

public record WebhookMessageRequest(
        @NotBlank
        String phone,
        @NotBlank
        String message
) { }
