package project.booteco.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.booteco.service.BotService;
import project.booteco.service.WhatsAppService; // Vamos criar este serviço no Passo 2

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class BotController {

    private final BotService botService;
    private final WhatsAppService whatsAppService;

    @Value("${webhook.secret.token:token_de_teste_123}")
    private String secretToken;

    @PostMapping
    public ResponseEntity<String> receptionistPost(
            @RequestHeader(value = "X-Bot-Token", required = false) String tokenAcesso,
            @RequestBody JsonNode payload) {

        // 1. Barreira de Segurança
        if (tokenAcesso == null || !tokenAcesso.equals(secretToken)) {
            log.warn("Tentativa de acesso bloqueada!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso Negado");
        }

        try {
            // 2. Evitar loop infinito (O bot não pode processar mensagens que ele mesmo enviou)
            boolean fromMe = payload.path("data").path("key").path("fromMe").asBoolean(false);
            if (fromMe) {
                return ResponseEntity.ok("Ignorado: Mensagem do próprio bot");
            }

            // 3. Extração do Telefone (Padrão Evolution API: 555199999999@s.whatsapp.net)
            String phone = payload.path("data").path("key").path("remoteJid").asText();
            if (phone != null && phone.contains("@")) {
                phone = phone.split("@")[0]; // Pega só os números
            }

            // 4. Extração do Texto (Pode vir em campos diferentes dependendo se tem link/emoji)
            JsonNode messageNode = payload.path("data").path("message");
            String message = messageNode.has("conversation")
                    ? messageNode.get("conversation").asText()
                    : messageNode.path("extendedTextMessage").path("text").asText();

            // 5. Processar e Enviar a Resposta
            if (message != null && !message.isBlank()) {
                log.info("Processando mensagem de {}: {}", phone, message);

                // Gera a resposta com a sua inteligência de negócio
                String respostaDoBot = botService.processMessage(phone, message);

                // Faz o POST devolvendo a resposta para o WhatsApp
                whatsAppService.sendMessage(phone, respostaDoBot);
            }

            // O Webhook só precisa receber um OK 200 rápido para saber que deu certo
            return ResponseEntity.ok("Processado com sucesso");

        } catch (Exception e) {
            log.error("Erro ao ler JSON da API de WhatsApp", e);
            return ResponseEntity.badRequest().body("Erro interno");
        }
    }
}
