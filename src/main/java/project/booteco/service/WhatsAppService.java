package project.booteco.service;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class WhatsAppService {

    private final RestClient restClient;

    public WhatsAppService(
            @Value("${utalk.api.url:https://app-utalk.umbler.com/api}") String apiUrl,
            @Value("${utalk.api.token}") String apiToken) {

        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                // A Umbler usa "Bearer" antes do token
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public void sendMessage(String chatId, String text) {
        // Rota da Umbler U-Talk
        String endpoint = "/v1/messages/";

        // O JSON extamente como a documentação pede
        Map<String, Object> body = Map.of(
                "chatId", chatId,
                "message", text
        );

        try {
            restClient.post()
                    .uri(endpoint)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info("✅ Mensagem devolvida com sucesso para o chat {}", chatId);
        } catch (Exception e) {
            log.error("❌ Falha ao enviar mensagem para o chat {}: {}", chatId, e.getMessage());
        }
    }
}