package com.JavaEcommerce.Ecommerce.exception;


import com.JavaEcommerce.Ecommerce.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class GlobalException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethdExcetionHandler(MethodArgumentNotValidException e){
        Map<String,String> errResponse=new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->{
            String fieldName=(( FieldError)err).getField();
            String messgae = err.getDefaultMessage();
            errResponse.put(fieldName,messgae);
                });
        return new ResponseEntity<Map<String, String>>(errResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFound(ResourceNotFoundException e){
        String message=e.getMessage();
        APIResponse apiResponse=new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<APIResponse> myResourceNotFound(ApiException e){
        String message=e.getMessage();
        APIResponse apiResponse=new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
}
