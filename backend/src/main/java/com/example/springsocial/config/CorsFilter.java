package com.example.springsocial.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class CorsFilter implements Filter {
	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // Set CORS headers
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, Connection, User-Agent, authorization, sw-useragent, sw-version");
        // Add additional CORS parameters
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Expose-Headers", "custom-header1, custom-header2");
        res.setHeader("Access-Control-Max-Age", "3600");

        // Prevent file inclusion
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && (pathInfo.contains("../") || pathInfo.contains("/../") || pathInfo.contains("..%2f") || pathInfo.contains("%2f.."))) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Prevent SQL injection
        String[] sqlKeywords = {"select", "insert", "update", "delete", "drop", "truncate"};
        String parameterValue;
        for (String sqlKeyword : sqlKeywords) {
            parameterValue = req.getParameter(sqlKeyword);
            if (parameterValue != null && parameterValue.toLowerCase().contains(sqlKeyword)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        // Prevent XSS
        String userAgent = req.getHeader("User-Agent");
        if (userAgent != null && (userAgent.contains("<script>") || userAgent.contains("alert("))) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Just REPLY OK if request method is OPTIONS for CORS (pre-flight)
        if (req.getMethod().equals("OPTIONS")) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Pass request down the chain
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialization code, if needed
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }

}
