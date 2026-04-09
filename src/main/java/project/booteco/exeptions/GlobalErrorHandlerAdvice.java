package project.booteco.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandlerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserNotFoundException> defaultErrorMessage(UserNotFoundException e){
      var error = new  UserNotFoundException(HttpStatus.NOT_FOUND.value(),e.getReason());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
