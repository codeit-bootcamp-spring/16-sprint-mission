package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request, Model model) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
        model.addAttribute("errorResponse", response);
        return "error/404";
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleIllegalState(IllegalStateException e, HttpServletRequest request, Model model) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.CONFLICT, e.getMessage(), request.getRequestURI());
        model.addAttribute("errorResponse", response);
        return "error/409";
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNoSuchElement(NoSuchElementException e, HttpServletRequest request, Model model) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
        model.addAttribute("errorResponse", response);
        return "error/404";
    }
}
