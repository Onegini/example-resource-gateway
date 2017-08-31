package com.onegini.examples.resourcegateway.util;

import static com.onegini.examples.resourcegateway.ResourceGatewayConstants.BEARER_PREFIX;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.ResponseEntity;

import com.onegini.examples.resourcegateway.model.ErrorResponse;

public class ErrorResponseBuilder {

  public static ResponseEntity<ErrorResponse> buildBadRequestResponse() {
    final String error = "invalid_request";
    final String errorDescription = "The request is missing a required parameter";

    final ErrorResponse errorResponse = new ErrorResponse(error, errorDescription);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  public static ResponseEntity<ErrorResponse> buildInvalidScopeResponse() {
    final String error = "insufficient_scope";
    final String errorDescription = "The request requires higher privileges than provided by the access token.";

    final ErrorResponse errorResponse = new ErrorResponse(error, errorDescription);
    return ResponseEntity.status(FORBIDDEN).body(errorResponse);
  }

  public static ResponseEntity<ErrorResponse> buildInvalidAccessTokenResponse() {
    final String error = "invalid_token";
    final String errorDescription = "The access token provided is expired, revoked, malformed, or invalid for other reasons.";

    final ErrorResponse errorResponse = new ErrorResponse(error, errorDescription);
    final String authenticateHeaderValue = BEARER_PREFIX + "error=\"" + error + "\", error_description=\"" + errorDescription + "\"";
    return ResponseEntity.status(UNAUTHORIZED).header(WWW_AUTHENTICATE, authenticateHeaderValue).body(errorResponse);
  }
}
