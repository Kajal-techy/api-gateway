package com.apigateway.apigateway.config;

import com.apigateway.apigateway.dao.AuthServiceProxy;
import com.apigateway.apigateway.model.AuthenticatedResponse;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class ZuulFilterImpl extends ZuulFilter {

    private final AuthServiceProxy authServiceProxy;

    public ZuulFilterImpl(AuthServiceProxy authServiceProxy) {
        this.authServiceProxy = authServiceProxy;
    }

    @Override
    public String filterType() {
        log.info("Entering ZuulFilterImpl.filterType");
        return "pre";
    }

    @Override
    public int filterOrder() {
        log.info("Entering ZuulFilterImpl.filterOrder");
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        log.info("Entering ZuulFilterImpl.shouldFilter");
        String RequestURI = RequestContext.getCurrentContext().getRequest().getRequestURI();
        log.info("shouldFilter = {} ", (!RequestURI.equals("/v1/authenticate")) && (!RequestURI.equals("/v1/user")));
        return (!RequestURI.equals("/v1/authenticate") && !RequestURI.equals("/v1/user"));
    }

    @Override
    public Object run() {
        log.info("Entering ZuulFilterImpl.run");
        AuthenticatedResponse authenticatedResponse = null;
        String authorizationHeader = RequestContext.getCurrentContext().getRequest().getHeader("Authorization");
        try {
            authenticatedResponse = authServiceProxy.tokenValidation(authorizationHeader);
            RequestContext context = RequestContext.getCurrentContext();
            if (authenticatedResponse.getLoggedInUserId() != null && !authenticatedResponse.getLoggedInUserId().isBlank()) {
                context.addZuulRequestHeader("loggedInUserId", authenticatedResponse.getLoggedInUserId());
                return authenticatedResponse;
            }
        } catch (Exception ex) {
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return authenticatedResponse;
    }
}

