package project.booteco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.booteco.domain.StatusTransation;
import project.booteco.mapper.TransactionMapper;
import project.booteco.pruducer.TransactionGetResponse;
import project.booteco.pruducer.TransactionPostRequest;
import project.booteco.pruducer.TransactionPutResponse;
import project.booteco.repository.TransactionRepository;
import project.booteco.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionMapper mapper;
    private final TransactionRepository transactionRepositorytion;
    private final UserRepository userRepository;

    public TransactionGetResponse createTransaction(TransactionPostRequest request) {
        userRepository.findById(request.userId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found."));
        var newTransaction = mapper.toEntity(request);
        newTransaction.setShortCode(generateShortCode());
        transactionRepositorytion.save(newTransaction);
        return mapper.toResponse(newTransaction);
    }


    public void cancelTransactionByShortCode(String shortCode){
        var transaction = transactionRepositorytion.findByShortCode(shortCode).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Transaction not foud"));
        transaction.setStatus(StatusTransation.CANCELADA);
        transactionRepositorytion.save(transaction);
    }
    public TransactionGetResponse updateTransactionByShortCode(String shortCode, TransactionPutResponse request){
        var transaction = transactionRepositorytion.findByShortCode(shortCode).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Transaction not found"));
        if(transaction.getStatus()== StatusTransation.CANCELADA){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Transaction already canceled");
        }
        mapper.updateEntity(request,transaction);
        transactionRepositorytion.save(transaction);
        return mapper.toResponse(transaction);
    }
    public String generateShortCode(){
        return  java.util.UUID.randomUUID().toString().replace("-","").substring(0,6).toUpperCase();
    }
}
