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
    private final String instanceName;

    public WhatsAppService(
            @Value("${whatsapp.api.url:http://localhost:8080}") String apiUrl,
            @Value("${whatsapp.api.apikey:global_apikey}") String apiKey,
            @Value("${whatsapp.api.instance:minha_instancia}") String instanceName) {

        this.instanceName = instanceName;
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("apikey", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public void sendMessage(String phone, String text) {
        // Padrão de envio da Evolution API
        String endpoint = "/message/sendText/" + instanceName;

        Map<String, Object> body = Map.of(
                "number", phone,
                "options", Map.of(
                        "delay", 1200, // Dá um delay para parecer humano digitando
                        "presence", "composing"
                ),
                "textMessage", Map.of(
                        "text", text
                )
        );

        try {
            restClient.post()
                    .uri(endpoint)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Mensagem enviada com sucesso para {}", phone);
        } catch (Exception e) {
            log.error("Falha ao enviar mensagem para {}: {}", phone, e.getMessage());
        }
    }
}
