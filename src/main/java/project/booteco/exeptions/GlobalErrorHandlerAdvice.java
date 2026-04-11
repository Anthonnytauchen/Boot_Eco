package project.booteco.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandlerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<DefautErrorMesage> handleUserNotFounde(UserNotFoundException e){
      var error = new  DefautErrorMesage(HttpStatus.NOT_FOUND.value(),e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(AiProcessingException.class)
    public ResponseEntity<DefautErrorMesage> handleAiProcessing(AiProcessingException e){

        var error = new DefautErrorMesage(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
