package project.booteco.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.booteco.domain.CategoryTransaction;
import project.booteco.domain.StateConversation;
import project.booteco.domain.TypeTransaction;
import project.booteco.exeptions.UserNotFoundException;
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
        }catch (UserNotFoundException e){
            log.info("User not found, creating new user with phone: {}",phone);
            return userService.createdUser(new UserPostRequest(phone));
        }
    }
    private String dealingBeginning(UserGetResponse user, String message){
        userService.updateUser(new UserPutResponse(user.id(),null,StateConversation.AGUARDANDO_EMAIL,null));
        return "Olá! Bem-vindo ao BootEco. Para começarmos, qual é o seu e-mail do Google? " +
                "Obs: Irei mandar monthlyReports graficos toda vez que voce pedir o relario de gastos";
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

    private String processAITransaction(UserGetResponse user, String message) throws Exception{
        String jsonIa= aiService.extractTransactionFromJson(message);
        log.info("JSON recebido da IA: {}", jsonIa);
        JsonNode transacaoNode = objectMapper.readTree(jsonIa);
        TransactionPostRequest request = buildTransactionRequest(user, transacaoNode, message);
        TransactionGetResponse transacaoSalva = transactionService.createTransaction(request);
        return getGenereteSucess(transacaoSalva);
    }

    private TransactionPostRequest buildTransactionRequest(UserGetResponse user, JsonNode transacaoNode, String originalMessage) {
        double valorDouble = transacaoNode.get("valor").asDouble();
        String tipoTexto = transacaoNode.get("tipo").asText().toUpperCase().trim();
        String categoriaTexto = transacaoNode.get("categoria").asText().toUpperCase().trim();


        categoriaTexto = removeAccents(categoriaTexto);

        BigDecimal value = BigDecimal.valueOf(valorDouble);
        TypeTransaction type = TypeTransaction.valueOf(tipoTexto);
        CategoryTransaction categoryTransaction = CategoryTransaction.valueOf(categoriaTexto);

        String subcategoria = originalMessage.length() > 50 ? originalMessage.substring(0, 47) + "..." : originalMessage;

        return new TransactionPostRequest(user.id(), value, type, categoryTransaction, subcategoria);
    }
    private String monthlyReportResponse(UserGetResponse user){
       MonthlyReportResponse monthlyReport = transactionService.generateMonthlySummary(user.id());
        
       StringBuilder sb = new StringBuilder();
        sb.append("📊 *O seu Relatório Mensal* 📊\n\n");

        // Totais
        sb.append("📈 *Receitas:* R$ ").append(monthlyReport.totalRevenue()).append("\n");
        sb.append("📉 *Despesas:* R$ ").append(monthlyReport.totalExpense()).append("\n");


        sb.append("💰 *Saldo:* R$ ").append(monthlyReport.totalSaved()).append("\n\n");

        // 3. Iterar sobre o Mapa de Gastos por Categoria
        if (monthlyReport.gastosPorCategoria() != null && !monthlyReport.gastosPorCategoria().isEmpty()) {
            sb.append("📋 *Gastos por Categoria:*\n");


            monthlyReport.gastosPorCategoria().forEach((categoria, valor) -> {

                sb.append(" 🔸 ").append(categoria.name()).append(": R$ ").append(valor).append("\n");
            });
        } else {
            sb.append("Ainda não tem gastos categorizados registados neste mês.\n");
        }

        return sb.toString();

    }

    private String dealingFree(UserGetResponse user, String message){
        try {
            message = removeAccents(message);
            String msUpper = message.toUpperCase().trim();
            if (message.equals("1")){
                return monthlyReportResponse(user);
            } else if (message.equals("2") || msUpper.equals("CANCELAR ")) {
                return cancelTransaction(user, message);
            } else if (message.equals("3") || msUpper.equals("FECHAR")) {
                return closeMonth(user,message);
            }
            return processAITransaction(user, message);

        }catch (Exception e){
            log.error("Erro ao processar mensagem livre: ", e);
            return getFallbackMenuMessage();
        }
    }
    private String closeMonth(UserGetResponse user, String message) {
        String msUpper = message.toUpperCase().trim();

        // 1. O usuário clicou na opção 3 do menu
        if (msUpper.equals("3")) {
            return "⚠️ *Atenção!* Você tem certeza que deseja fechar o seu mês?\n\n" +
                    "Isso irá gerar o seu relatório final e *apagar todos os seus registros* até o dia de hoje.\n\n" +
                    "Para confirmar, digite a palavra: *FECHAR*";
        }

        // 2. O usuário confirmou com a palavra FECHAR
        if (msUpper.equals("FECHAR")) {
            try {
                // A. Gera o relatório FINAL do mês com os dados ainda no banco
                String relatorioFinal = monthlyReportResponse(user);

                // B. Agora sim, apaga/arquiva os dados
                transactionService.closeMonth(user.id());

                // C. Retorna a mensagem de sucesso junto com o resumão do mês que passou
                return "✅ *Mês fechado com sucesso!* Seus registros foram zerados para o próximo mês.\n\n" +
                        "Aqui está o seu resumo final:\n\n" +
                        relatorioFinal;

            } catch (Exception e) {
                log.error("Erro ao fechar o mês para o usuário {}: ", user.id(), e);
                return "❌ Ops! Ocorreu um erro e não foi possível fechar o seu mês. Tente novamente mais tarde.";
            }
        }

        // 3. Se cair aqui, é porque a mensagem não era nem "3" nem "FECHAR"
        return getFallbackMenuMessage();
    }
    private String cancelTransaction(UserGetResponse user, String message){
        String msUpper= message.toUpperCase().trim();
        if(msUpper.equals("2") ){
            return "🗑️ *Apagar uma Transação*\n\n" +
                    "Para apagar, digite a palavra *CANCELAR* seguida do código gerado no recibo.\n\n" +
                    "💡 *Exemplo:* CANCELAR AB123";
        }
        String shortCode = message.substring(9).trim();
        try{
            transactionService.cancelTransactionByShortCode(user.id(),shortCode);
            return "✅ Transação *" + shortCode + "* apagada com sucesso!";
        }catch (Exception e){
            log.error("Erro ao cancelar transação {}: ", shortCode, e);
            return "❌ Ops! Não foi possível apagar.\n\n" +
                    "Verifique se o código *" + shortCode + "* está correto ou se a transação já foi excluída.";
        }
    }
    private String removeAccents(String text) {
        return text.replace("Ç", "C")
                .replace("Ã", "A")
                .replace("Õ", "O")
                .replace("É", "E")
                .replace("Í", "I");
    }
    private String getFallbackMenuMessage() {
        return "🤔 Ops, não entendi muito bem. O que você deseja fazer?\n\n" +
                "1️⃣ - Ver Relatório Mensal\n" +
                "2️⃣ - Apagar uma transação\n" +
                "3️⃣ - Fechar o mês\n\n" +
                "💡 *Dica:* Você também pode simplesmente digitar o seu gasto ou receita.\n" +
                "Ex: _'Recebi 5000 de salário'_ ou _'Gastei 50 no iFood'_";
    }

    private String getGenereteSucess(TransactionGetResponse transacaoSalva) {
        return "✅ *Transação registrada com sucesso!*\n\n" +
                "🏷️ *Tipo:* " + transacaoSalva.type() + "\n" +
                "💲 *Valor:* R$ " + transacaoSalva.value() + "\n" +
                "📂 *Categoria:* " + transacaoSalva.categoryTransaction() + "\n" +
                "🗑️ *Código p/ cancelamento:* " + transacaoSalva.shortCode() + "\n" +
                "--------------------------\n" +
                "O que deseja fazer agora?\n\n" +
                "1️⃣ - Ver Relatório Mensal\n" +
                "2️⃣ - Apagar uma transação\n" +
                "3️⃣ - Fechar o mês\n\n" +
                "💡 Ou simplesmente digite o seu próximo gasto/receita.";
    }
}
