package com.apigateway.apigateway.config;

import com.apigateway.apigateway.model.AuthenticationFailure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ERROR_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_ERROR_FILTER_ORDER;

@Component
@Slf4j
public class SendErrorZuulCustomFilter extends SendErrorFilter {

    private final ObjectMapper objectMapper;

    public SendErrorZuulCustomFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String filterType() {
        log.info("Entering SendErrorZuulCustomFilter.filterType");
        return ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        log.info("Entering SendErrorZuulCustomFilter.filterOrder");
        return SEND_ERROR_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        log.info("Entering SendErrorZuulCustomFilter.shouldFilter");
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getThrowable() != null
                && !ctx.getBoolean("sendErrorFilter.ran", false);
    }

    /**
     * This function returning the error response if authentication API is throwing any error message
     *
     * @return
     */
    @Override
    public Object run() {
        log.info("Entering SendErrorZuulCustomFilter.run");
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        log.info("Response = {} ");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            response.getOutputStream().println(objectMapper.writeValueAsString((AuthenticationFailure.builder().message("Authentication Failure").errorCode(401)).build()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
