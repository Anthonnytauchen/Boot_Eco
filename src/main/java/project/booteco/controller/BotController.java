package project.booteco.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.booteco.pruducer.WebhookMessageRequest;
import project.booteco.service.BotService;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class BotController {
    private final BotService botService;
    @PostMapping
public ResponseEntity<String> receptionistPost (@RequestBody @Valid WebhookMessageRequest request){
        log.info("Mensagem recebida: {}",request);
        var responseBot = botService.processMessage(request.phone(),request.message());
        return ResponseEntity.ok(responseBot);
    }
}
