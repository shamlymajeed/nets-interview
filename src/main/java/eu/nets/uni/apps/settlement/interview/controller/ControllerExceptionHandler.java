package eu.nets.uni.apps.settlement.interview.controller;

import eu.nets.uni.apps.settlement.interview.exception.ErrorResponse;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(value = {ExchangeRateNotAvailableException.class})
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public ErrorResponse exchangeRateNotAvailableException(ExchangeRateNotAvailableException ex , WebRequest request){

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setMessage("Exchange rate for the given currency is not available.");
    return errorResponse;
  }

}