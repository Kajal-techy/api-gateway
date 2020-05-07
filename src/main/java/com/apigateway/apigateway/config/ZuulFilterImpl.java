package com.apigateway.apigateway.config;

import com.apigateway.apigateway.dao.AuthServiceProxy;
import com.apigateway.apigateway.model.AuthenticatedResponse;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Slf4j
@Component
public class ZuulFilterImpl extends ZuulFilter {

    @Value("${hostname.userservice}")
    private String hostname;

    @Value("${path.getUserByUserName}")
    private String path;

    @Value("${spring.data.authService}")
    private String authService;

    @Value("${spring.url.authServiceUserValidationPath}")
    private String authServiceUserValidationPath;

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
        return !RequestContext.getCurrentContext().getRequest().getRequestURI().equals("/v1/authenticate");
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

