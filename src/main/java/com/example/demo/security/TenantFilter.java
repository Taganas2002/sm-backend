package com.example.demo.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(20) // run after JWT filter
public class TenantFilter implements Filter {
  @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    try {
      var http = (HttpServletRequest) req;
      Object claim = http.getAttribute("jwt.schoolId"); // set in AuthTokenFilter
      if (claim instanceof Number n) TenantContext.setSchoolId(n.longValue());
      chain.doFilter(req, res);
    } finally {
      TenantContext.clear();
    }
  }
}
