package project.booteco.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import project.booteco.config.GeminiConfigurationProperties;


@Service
@Slf4j

public class AiService {
    private final RestClient restClient;
    private final GeminiConfigurationProperties geminiProperties;
    private final ObjectMapper objectMapper;

    public AiService(GeminiConfigurationProperties geminiProperties) {
        this.geminiProperties = geminiProperties;
        this.objectMapper = new ObjectMapper();
        // Agora você acessa os valores através dos métodos do record
        this.restClient = RestClient.builder()
                .baseUrl(geminiProperties.url())
                .defaultHeader("x-goog-api-key", geminiProperties.key())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String extractTransactionFromJson(String userInput) {
        log.info("A preparar para enviar texto para a IA: {}", userInput);
        try {
            String prompt = buildPrompt(userInput);
            Map<String,Object> requestBody = buildRequestBody(prompt);

            String jsonResponse = callGeminiApi(requestBody);

            return parseGeminiResponse(jsonResponse);
        } catch (Exception e) {
            log.error("Erro ao chamar a API da IA: {}", e.getMessage());
            throw new RuntimeException("Falha ao comunicar com a IA. Tente novamente em alguns segundos.", e);
        }
    }
    private String buildPrompt(String texto) {
        return """
                Você é um assistente financeiro. Leia a seguinte frase e extraia os dados financeiros.
                
                REGRA IMPORTANTE: Se a frase NÃO contiver informações claras sobre um gasto ou ganho de dinheiro, responda APENAS com a palavra: ERRO.
                
                Caso seja uma transação financeira válida, devolva APENAS um JSON válido, sem blocos de código markdown (sem ```json), com as chaves:
                - "valor": número decimal (ex: 50.50)
                - "tipo": exatamente a palavra "RECEITA" ou "DESPESA"
                - "categoria": escolha uma entre "ALIMENTACAO", "SAUDE", "TRANSPORTE", "MORADIA", "LAZER", "EDUCACAO", ou "OUTROS"
                
                Frase do utilizador: "%s"
                """.formatted(texto);
    }
    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );
    }
    private String callGeminiApi(Map<String, Object> requestBody) {
        return restClient.post()
                .body(requestBody)
                .retrieve()
                .body(String.class);

    }
    private String parseGeminiResponse(String jsonResponse) throws Exception {
        JsonNode rootNode= objectMapper.readTree(jsonResponse);
        String ClenTextAI= rootNode.path("candidates").get(0).
                path("content").path("parts").get(0).path("text").asText();
        return ClenTextAI.replace("```json", "").replace("```", "").trim();
    }

}
