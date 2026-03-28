package project.booteco.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.booteco.domain.CategoryTransation;
import project.booteco.domain.StatusTransation;
import project.booteco.domain.Transaction;
import project.booteco.domain.TypeTransation;
import project.booteco.mapper.TransactionMapper;
import project.booteco.pruducer.MonthlyReportResponse;
import project.booteco.pruducer.TransactionGetResponse;
import project.booteco.pruducer.TransactionPostRequest;
import project.booteco.pruducer.TransactionPutResponse;
import project.booteco.repository.TransactionRepository;
import project.booteco.repository.UserRepository;
import java.util.stream.Collectors;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionMapper mapper;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionGetResponse createTransaction(TransactionPostRequest request) {
        userRepository.findById(request.userId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found."));
        var newTransaction = mapper.toEntity(request);
        newTransaction.setShortCode(generateShortCode());
        transactionRepository.save(newTransaction);
        return mapper.toResponse(newTransaction);
    }


    public void cancelTransactionByShortCode(UUID userId,String shortCode){
        var transaction = transactionRepository.findByUserIdAndShortCode(userId,shortCode).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Transaction not foud"));
        transaction.setStatus(StatusTransation.CANCELADA);
        transactionRepository.save(transaction);
    }
    public TransactionGetResponse updateTransactionByShortCode(UUID userId,String shortCode, TransactionPutResponse request){
        var transaction = transactionRepository.findByUserIdAndShortCode(userId, shortCode).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Transaction not found"));
        if(transaction.getStatus()== StatusTransation.CANCELADA){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Transaction already canceled");
        }
        mapper.updateEntity(request,transaction);
        transactionRepository.save(transaction);
        return mapper.toResponse(transaction);
    }
    private String generateShortCode(){
        return  java.util.UUID.randomUUID().toString().replace("-","").substring(0,6).toUpperCase();
    }


    private BigDecimal calculate(List<Transaction> transactions, TypeTransation type) {
       return  transactions.stream().
                filter(t -> t.getType() == type).
                map(Transaction::getValue).
                reduce(BigDecimal.ZERO, BigDecimal::add);


    }
    private Map<CategoryTransation, BigDecimal> mapCalculeteCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TypeTransation.DESPESA && t.getCategoryTransaction() != null).
                collect(java.util.stream.Collectors.groupingBy(
                        Transaction::getCategoryTransaction,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getValue, BigDecimal::add)
                ));
    }


    public MonthlyReportResponse calculateSummary(List<Transaction> transactionList) {

        BigDecimal receita = calculate(transactionList, TypeTransation.RECEITA);

        BigDecimal despesas = calculate(transactionList, TypeTransation.DESPESA);

        Map<CategoryTransation,BigDecimal> categoryCalculate = mapCalculeteCategory(transactionList);
        return new MonthlyReportResponse(receita,despesas,receita.subtract(despesas),categoryCalculate);
    }
    public MonthlyReportResponse generateMonthlySummary(UUID userID) {
        List<Transaction> transactionList = transactionRepository.findByUserIdAndStatus(userID, StatusTransation.ATIVA);

        return calculateSummary(transactionList); // Passa a lista para o motor
    }
@Transactional
    public MonthlyReportResponse closeMonth (UUID userID){
        List<Transaction> transactionList = transactionRepository.findByUserIdAndStatus(
                userID,
                StatusTransation.ATIVA
        );
        if (transactionList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No transactions found");
        }
        MonthlyReportResponse closeMonth = calculateSummary(transactionList);

        transactionList.forEach(t-> t.setStatus(StatusTransation.FECHADA));

        transactionRepository.saveAll(transactionList);
        return closeMonth;
    }

    }

