package project.booteco.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandlerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<DefaultErrorMessage> handleUserNotFounde(UserNotFoundException e){
      var error = new DefaultErrorMessage(HttpStatus.NOT_FOUND.value(),e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(AiProcessingException.class)
    public ResponseEntity<DefaultErrorMessage> handleAiProcessing(AiProcessingException e){

        var error = new DefaultErrorMessage(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<DefaultErrorMessage> handleTransactionNotFound(TransactionNotFoundException e){
        var error = new DefaultErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultErrorMessage> handleValidationErrors(org.springframework.web.bind.MethodArgumentNotValidException e){
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        var error = new DefaultErrorMessage(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultErrorMessage> handleGenericException(Exception e){
        var error = new DefaultErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro interno inesperado.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
