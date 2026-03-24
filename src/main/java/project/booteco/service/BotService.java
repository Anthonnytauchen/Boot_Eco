package project.booteco.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.booteco.domain.CategoryTransation;
import project.booteco.domain.StateConversation;
import project.booteco.domain.TypeTransation;
import project.booteco.pruducer.*;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotService {
    private final TransactionService transactionService;
    private final UserService userService;
    private final AiService aiService;
    private  final ObjectMapper objectMapper= new ObjectMapper();

    public String processMessage(String phone, String message) {
        var user = getOrCreateUser(phone);
        switch (user.stateConversation()){
            case INICIO:
                return dealingBeginning(user,message);
            case AGUARDANDO_EMAIL:
                return dealingEmail(user,message);
            case AGUARDANDO_OBJETIVO_TEXTO:
                return dealingObjectiveText(user,message);
            case LIVRE:
                return dealingFree(user,message);
            default:
                return "Desculpe, não entendi o seu momento atual.";

        }
    }
    private UserGetResponse getOrCreateUser(String phone){
        try{
        return userService.findByPhone(phone);
        }catch (ResponseStatusException e){
            log.info("User not found, creating new user with phone: {}",phone);
            return userService.createdUser(new UserPostRequest(phone));
        }
    }
    private String dealingBeginning(UserGetResponse user, String message){
        userService.updateUser(new UserPutResponse(user.id(),null,StateConversation.AGUARDANDO_EMAIL,null));
        return "Olá! Bem-vindo ao BootEco. Para começarmos, qual é o seu e-mail do Google? " +
                "Obs: Irei mandar relatorios graficos toda vez que voce pedir o relario de gastos";
    }
    private String dealingEmail(UserGetResponse user, String message){
        userService.updateUser(new UserPutResponse(user.id(),message,StateConversation.AGUARDANDO_OBJETIVO_TEXTO,null));
        return "Obrigado seu email foi salvo! Agora qual o seu objetivo? (Investimento, Economia, etc)";
    }
    private String dealingObjectiveText(UserGetResponse user, String message){
        userService.updateUser(new UserPutResponse(user.id(), user.emailGoogle(), StateConversation.LIVRE, message));
        return "Obrigado, o seu objetivo foi salvo! 🎉\n" +
                "O seu cadastro está concluído. A partir de agora, pode mandar as suas transações livremente.\n\n" +
                "Para começarmos, digite como se estivesse a falar com um amigo qual é a sua renda mensal (Ex: 'Recebi 5000 do meu salário hoje').";
    }
    private String dealingFree(UserGetResponse user, String message){
        try {
            // 1. Pega no JSON da IA
            String jsonDaIA = aiService.extractTransactionFromJson(message);

            // 2. Lê a "Árvore" usando o mapper reaproveitado
            JsonNode transacaoNode = objectMapper.readTree(jsonDaIA);

            // 3. Extrai os valores usando classes limpas
            BigDecimal valor = BigDecimal.valueOf(transacaoNode.get("valor").asDouble());
            TypeTransation tipo = TypeTransation.valueOf(transacaoNode.get("tipo").asText());
            CategoryTransation categoria = CategoryTransation.valueOf(transacaoNode.get("categoria").asText());

            // 4. Monta o DTO
            TransactionPostRequest request = new TransactionPostRequest(
                    user.id(),
                    valor,
                    tipo,
                    categoria,
                    message
            );

            // 5. Guarda na Base de Dados
            TransactionGetResponse transacaoSalva = transactionService.createTransaction(request);

            // 6. Devolve o recibo
            return "✅ Registado com sucesso!\n" +
                    "Tipo: " + transacaoSalva.type() + "\n" +
                    "Valor: R$ " + transacaoSalva.value() + "\n" +
                    "Categoria: " + transacaoSalva.categoryTransaction();

        } catch (Exception e) {
            log.error("Erro ao processar transação livre: ", e);
            return "Ops! O meu cérebro confundiu-se com os valores. Pode tentar escrever de forma mais direta? (Ex: 'Gastei 50 no Ifood').";
        }
    }
}
