package project.booteco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; // Adicione este import!
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.booteco.service.BotService;
import project.booteco.service.WhatsAppService;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class BotController {

    private final BotService botService;
    private final WhatsAppService whatsAppService;
    private final ObjectMapper objectMapper = new ObjectMapper();// Adicionamos o ObjectMapper aqui

    @Value("${webhook.secret.token:token_de_teste_123}")
    private String secretToken;

    @PostMapping
    public ResponseEntity<String> receptionistPost(
            @RequestParam(value = "token", required = false) String tokenAcesso,
            @RequestBody String rawPayload) { // <-- Recebemos como String bruta!

        // 1. Barreira de Segurança
        if (tokenAcesso == null || !tokenAcesso.equals(secretToken)) {
            log.warn("Tentativa de acesso bloqueada!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso Negado");
        }

        try {
            // Imprime o JSON inteiro no console
            log.info("JSON Bruto Recebido: {}", rawPayload);

            // 2. Converte a String manualmente para JsonNode
            JsonNode payload = objectMapper.readTree(rawPayload);

            // Garante que é um evento de Mensagem e não outra notificação
            if (!"Message".equals(payload.path("Type").asText(""))) {
                return ResponseEntity.ok("Ignorado: Não é uma mensagem de chat");
            }

            // Atalho para o nó principal de dados
            JsonNode contentNode = payload.path("Payload").path("Content");

            // 3. Evitar loop infinito (Na sua plataforma, "Source" diz quem enviou)
            // Se não for "Contact" (ou seja, foi o próprio sistema/bot), ignora.
            String source = contentNode.path("LastMessage").path("Source").asText("");
            if (!"Contact".equalsIgnoreCase(source)) {
                return ResponseEntity.ok("Ignorado: Mensagem do próprio bot");
            }

            // 4. Extração do Telefone e Chat ID
            String phone = contentNode.path("Contact").path("PhoneNumber").asText("");
            if (phone.startsWith("+")) {
                phone = phone.substring(1);
            }

            // 🔥 NOVO: Pegamos o ID da conversa que a Umbler gerou!
            String chatId = contentNode.path("Id").asText("");

            // 5. Extração do Texto
            String message = contentNode.path("LastMessage").path("Content").asText("");

            // 6. Processar e Enviar a Resposta
            if (message != null && !message.isBlank()) {
                log.info("Processando mensagem de {}: {}", phone, message);

                String respostaDoBot = botService.processMessage(phone, message);

                // 🔥 NOVO: Passamos o chatId em vez do phone para a Umbler
                whatsAppService.sendMessage(chatId, respostaDoBot);
            }

            return ResponseEntity.ok("Processado com sucesso");
    } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    }
