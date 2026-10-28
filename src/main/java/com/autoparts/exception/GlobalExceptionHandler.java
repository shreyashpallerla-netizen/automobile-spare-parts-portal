package com.autoparts.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        log.error("Resource not found: {}", ex.getMessage());
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", 404);
        return "error/error";
    }

    @ExceptionHandler(StockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleStockException(StockException ex, Model model) {
        log.error("Stock error: {}", ex.getMessage());
        model.addAttribute("error", "Stock Error");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", 400);
        return "error/error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.error("Validation error: {}", ex.getMessage());
        model.addAttribute("error", "Validation Error");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", 400);
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", "An unexpected error occurred. Please try again.");
        model.addAttribute("status", 500);
        return "error/error";
    }
}
