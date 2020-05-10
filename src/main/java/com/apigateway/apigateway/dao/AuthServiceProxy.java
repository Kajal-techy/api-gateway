package com.apigateway.apigateway.dao;

import com.apigateway.apigateway.model.AuthenticatedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("auth-service")
public interface AuthServiceProxy {

    /**
     * This function calling is calling the AUth Service for token validation.
     *
     * @param token
     * @return
     */
    @GetMapping("/v1/validating-token")
    AuthenticatedResponse tokenValidation(@RequestHeader("Authorization") String token);
}