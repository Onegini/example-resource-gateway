package com.onegini.examples.resourcegateway.web;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.onegini.examples.resourcegateway.model.ApplicationDetails;
import com.onegini.examples.resourcegateway.model.TokenIntrospectionResult;
import com.onegini.examples.resourcegateway.service.AccessTokenExtractor;
import com.onegini.examples.resourcegateway.service.DeviceApiRequestService;
import com.onegini.examples.resourcegateway.service.ScopeValidationService;
import com.onegini.examples.resourcegateway.service.TokenTypeValidationService;
import com.onegini.examples.resourcegateway.service.tokenintrospection.TokenIntrospectionService;

@RestController
@RequestMapping(value = "/resources")
public class ResourcesController {

  @Resource
  private TokenIntrospectionService tokenIntrospectionService;
  @Resource
  private ScopeValidationService scopeValidationService;
  @Resource
  private DeviceApiRequestService deviceApiRequestService;
  @Resource
  private AccessTokenExtractor accessTokenExtractor;
  @Resource
  private TokenTypeValidationService tokenTypeValidationService;

  private static final String DECORATION = "✨";

  @RequestMapping(value = "/devices", method = RequestMethod.GET)
  public ResponseEntity<?> getDevices(@RequestHeader(AUTHORIZATION) final String authorizationHeader) {

    final String accessToken = accessTokenExtractor.extractFromHeader(authorizationHeader);
    final TokenIntrospectionResult tokenIntrospectionResult = tokenIntrospectionService.introspectAccessToken(accessToken);

    scopeValidationService.validateReadScopeGranted(tokenIntrospectionResult.getScope());
    tokenTypeValidationService.validateNoImplicitAuthenticationToken(tokenIntrospectionResult.getAmr());

    return deviceApiRequestService.getDevices(tokenIntrospectionResult.getSub());
  }

  @RequestMapping(value = "/application-details", method = RequestMethod.GET)
  public ResponseEntity<?> getApplicationDetails(@RequestHeader(AUTHORIZATION) final String authorizationHeader) {

    final String accessToken = accessTokenExtractor.extractFromHeader(authorizationHeader);
    final TokenIntrospectionResult tokenIntrospectionResult = tokenIntrospectionService.introspectAccessToken(accessToken);

    scopeValidationService.validateApplicationDetailsScopeGranted(tokenIntrospectionResult.getScope());
    tokenTypeValidationService.validateNoImplicitAuthenticationToken(tokenIntrospectionResult.getAmr());

    final ApplicationDetails applicationDetails = ApplicationDetails.fromIssuer(tokenIntrospectionResult.getIss());

    return new ResponseEntity<Object>(applicationDetails, OK);
  }

  @RequestMapping(value = "/user-id-decorated", method = RequestMethod.GET)
  public ResponseEntity<?> getFoo(@RequestHeader(AUTHORIZATION) final String authorizationHeader) {
    final String accessToken = accessTokenExtractor.extractFromHeader(authorizationHeader);
    final TokenIntrospectionResult tokenIntrospectionResult = tokenIntrospectionService.introspectAccessToken(accessToken);

    tokenTypeValidationService.validateImplicitAuthenticationToken(tokenIntrospectionResult.getAmr());

    final String userId = tokenIntrospectionResult.getSub();
    final String responseText = DECORATION + " " + userId + " " + DECORATION;

    return new ResponseEntity<>(responseText, OK);
  }
}
