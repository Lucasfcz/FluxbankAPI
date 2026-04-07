package io.github.Lucasfcz.fluxbank.exception;

import io.github.Lucasfcz.fluxbank.dto.response.ErrorResponseDTO;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maps missing account ids to HTTP 404.
    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleIdNotFound(IdNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Maps business validation errors to HTTP 400.
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientBalance(InsufficientBalanceException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Maps invalid monetary amounts to HTTP 400.
    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidAmount(InvalidAmountException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Maps transfers to the same account to HTTP 400.
    @ExceptionHandler(SameAccountException.class)
    public ResponseEntity<ErrorResponseDTO> handleSameAccount(SameAccountException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Maps duplicate resource violations to HTTP 409.
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceConflict(ResourceConflictException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // If is inactive account, maps to HTTP 403 to indicate the operation cannot proceed due to the account state.
    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountInactive(AccountInactiveException ex) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // Maps null owner to HTTP 400 when account owner is not provided.
    @ExceptionHandler(NullOwnerException.class)
    public ResponseEntity<ErrorResponseDTO> handleNullOwner(NullOwnerException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockException.class})
    public ResponseEntity<ErrorResponseDTO> handleOptimisticLocking(Exception ex) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(), "Concurrent update detected. Please retry the operation."
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}