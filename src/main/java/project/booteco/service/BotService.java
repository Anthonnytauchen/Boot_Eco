package project.booteco.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.booteco.domain.StateConversation;
import project.booteco.pruducer.UserGetResponse;
import project.booteco.pruducer.UserPostRequest;
import project.booteco.pruducer.UserPutResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotService {
    private final TransactionService transactionService;
    private final UserService userService;

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
        userService.updateUser(new UserPutResponse(user.id(), user.emailGoogle(), StateConversation.AGUARDANDO_OBJETIVO_TEXTO,message));
        return "Obrigado seu objetivo foi salvo! Agora qual o sua renda mensal?";
    }
    private String dealingFree(UserGetResponse user, String message){
        return "";
    }
}
