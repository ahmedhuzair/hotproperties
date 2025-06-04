package com.hotproperties.hotproperties.controllers;

import com.hotproperties.hotproperties.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, HttpServletRequest request, Model model) {

        // Servlet error attributes (if present)
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

        // Request details
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");

        // Exception details
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        // Populate model attributes for Thymeleaf
        model.addAttribute("status", statusCode != null ? statusCode : 500);
        model.addAttribute("error", errorMessage != null ? errorMessage : "Unexpected error");
        model.addAttribute("path", requestUri != null ? requestUri : path);
        model.addAttribute("query", queryString);
        model.addAttribute("method", httpMethod);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("exceptionMessage", exceptionMessage);

        return "error"; // return your Thymeleaf or JSP view
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public String handleAlreadyExists(AlreadyExistsException ex, HttpServletRequest request, Model model) {

        // Servlet error attributes (if present)
        Integer statusCode = 409;
        String errorMessage = ex.getMessage();
        String requestUri = request.getRequestURI();

        // Request details
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        // Populate model attributes for Thymeleaf
        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorMessage);
        model.addAttribute("path", requestUri != null ? requestUri : path);
        model.addAttribute("query", queryString);
        model.addAttribute("method", httpMethod);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("exceptionMessage", exceptionMessage);

        return "error"; // renders error.html
    }

    @ExceptionHandler(InvalidUserParameterException.class)
    public String handleInvalidUser(InvalidUserParameterException ex, HttpServletRequest request, Model model) {

        // Servlet error attributes (if present)
        Integer statusCode = 400;
        String errorMessage = ex.getMessage();
        String requestUri = request.getRequestURI();

        // Request details
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");

        // Exception details
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        // Populate model attributes for Thymeleaf
        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorMessage);
        model.addAttribute("path", requestUri != null ? requestUri : path);
        model.addAttribute("query", queryString);
        model.addAttribute("method", httpMethod);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("exceptionMessage", exceptionMessage);

        return "error";// renders error.html
    }

    @ExceptionHandler(InvalidPropertyParameterException.class)
    public String handleInvalidProperty(InvalidPropertyParameterException ex, HttpServletRequest request, Model model) {

        // Servlet error attributes (if present)
        Integer statusCode = 400;
        String errorMessage = ex.getMessage();
        String requestUri = request.getRequestURI();

        // Request details
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");

        // Exception details
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        // Populate model attributes for Thymeleaf
        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorMessage);
        model.addAttribute("path", requestUri != null ? requestUri : path);
        model.addAttribute("query", queryString);
        model.addAttribute("method", httpMethod);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("exceptionMessage", exceptionMessage);

        return "error";// renders error.html
    }

    @ExceptionHandler(InvalidPropertyImageParameterException.class)
    public String handleInvalidPropertyImage(InvalidPropertyImageParameterException ex, HttpServletRequest request, Model model) {

        // Servlet error attributes (if present)
        Integer statusCode = 400;
        String errorMessage = ex.getMessage();
        String requestUri = request.getRequestURI();

        // Request details
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");

        // Exception details
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        // Populate model attributes for Thymeleaf
        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorMessage);
        model.addAttribute("path", requestUri != null ? requestUri : path);
        model.addAttribute("query", queryString);
        model.addAttribute("method", httpMethod);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("exceptionMessage", exceptionMessage);

        return "error";// renders error.html
    }

    @ExceptionHandler(InvalidMessageParameterException.class)
    public String handleInvalidMessage(InvalidMessageParameterException ex, HttpServletRequest request, Model model) {

        // Servlet error attributes (if present)
        Integer statusCode = 400;
        String errorMessage = ex.getMessage();
        String requestUri = request.getRequestURI();

        // Request details
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");

        // Exception details
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        // Populate model attributes for Thymeleaf
        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorMessage);
        model.addAttribute("path", requestUri != null ? requestUri : path);
        model.addAttribute("query", queryString);
        model.addAttribute("method", httpMethod);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("exceptionMessage", exceptionMessage);

        return "error";// renders error.html
    }

    @ExceptionHandler(InvalidFavoriteParameterException.class)
    public String handleInvalidFavorite(InvalidFavoriteParameterException ex, HttpServletRequest request, Model model) {

        // Servlet error attributes (if present)
        Integer statusCode = 400;
        String errorMessage = ex.getMessage();
        String requestUri = request.getRequestURI();

        // Request details
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");

        // Exception details
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        // Populate model attributes for Thymeleaf
        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorMessage);
        model.addAttribute("path", requestUri != null ? requestUri : path);
        model.addAttribute("query", queryString);
        model.addAttribute("method", httpMethod);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("exceptionMessage", exceptionMessage);

        return "error";// renders error.html
    }


    @ExceptionHandler(Exception.class)
    public String handleAnyException(Exception ex, HttpServletRequest request, Model model) {
        // Servlet error attributes (if present)
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

        // Request details
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");

        // Exception details
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();

        // Populate model attributes for Thymeleaf
        model.addAttribute("status", statusCode != null ? statusCode : 500);
        model.addAttribute("error", errorMessage != null ? errorMessage : "Unexpected error");
        model.addAttribute("path", requestUri != null ? requestUri : path);
        model.addAttribute("query", queryString);
        model.addAttribute("method", httpMethod);
        model.addAttribute("userAgent", userAgent);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("exceptionMessage", exceptionMessage);

        return "error"; // renders error.html
    }
}

