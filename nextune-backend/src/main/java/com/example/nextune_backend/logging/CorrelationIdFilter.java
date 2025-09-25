package com.example.nextune_backend.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class CorrelationIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            if (req instanceof HttpServletRequest r) {
                MDC.put("method", r.getMethod());
                MDC.put("path", r.getRequestURI());
            }
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
}
