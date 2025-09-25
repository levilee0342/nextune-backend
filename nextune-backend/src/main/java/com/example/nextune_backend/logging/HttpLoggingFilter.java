package com.example.nextune_backend.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(2)
public class HttpLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper((HttpServletRequest) req);
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper((HttpServletResponse) res);

        try {
            chain.doFilter(request, response);
        } finally {
            String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            log.debug("HTTP {} {} body={}", request.getMethod(), request.getRequestURI(),
                    body.isBlank() ? "<empty>" : body);

            String respBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            log.debug("HTTP resp {} {} status={} body={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(),
                    respBody.isBlank() ? "<empty>" : respBody);

            response.copyBodyToResponse(); // quan trọng: ghi trả lại body cho client
        }
    }
}
