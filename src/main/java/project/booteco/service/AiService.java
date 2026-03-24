package project.booteco.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
@Slf4j
public class AiService {
    private final String apiKey;
    private final String apiUrl;
    private final RestClient restClient;
    private  final ObjectMapper objectMapper;

    public AiService(@Value("${gemini.api.key}") String apiKey, @Value("${gemini.api.url}") String apiUrl){
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    public String extractTransactionFromJson(String userInput) {
        log.info("A preparar para enviar texto para a IA: {}", userInput);
        try {
            String prompt = buildPrompt(userInput);
            Map<String,Object> requestBody = buildRequestBody(prompt);

            String jsonResponse = callGeminiApi(requestBody);

            return parseGeminiResponse(jsonResponse);
        }catch (Exception e){
            log.error("Erro ao chamar a API da IA: {}", e.getMessage());
            return "Desculpe, o meu cérebro (IA) está fora do ar no momento.";
        }
    }
    private String buildPrompt(String texto) {
        return """
                Você é um assistente financeiro. Leia a seguinte frase e extraia os dados financeiros.
                Devolva APENAS um JSON válido, sem blocos de código markdown (sem ```json), com as chaves:
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
        return restClient.post().uri(apiUrl).
                header("x-goog-api-key", apiKey).
                header("Content-Type", "application/json").
                body(requestBody).
                retrieve().
                body(String.class);

    }
    private String parseGeminiResponse(String jsonResponse) throws Exception {
        JsonNode rootNode= objectMapper.readTree(jsonResponse);
        String ClenTextAI= rootNode.path("candidates").get(0).
                path("content").path("parts").get(0).path("text").asText();
        return ClenTextAI.replace("```json", "").replace("```", "").trim();
    }

}
