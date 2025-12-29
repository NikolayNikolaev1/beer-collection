package com.nnikolaev.beercollection.security.jwt;

import com.nnikolaev.beercollection.security.CustomUserDetails;
import com.nnikolaev.beercollection.service.implementation.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.nnikolaev.beercollection.common.Constant.Route;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTH_HEADER);
        String token = null;

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            token = authHeader.substring(7);
        }

        if (token != null && this.tokenProvider.validateToken(token)) {
            if (this.tokenProvider.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String email = this.tokenProvider.getEmailFromToken(token);
            CustomUserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // If you want to skip filter for some endpoints (e.g. /auth/login, /auth/register), you can:
        String path = request.getServletPath();
        return path.startsWith(Route.AUTH_PREFIX) && !path.equals(Route.AUTH_PREFIX + Route.LOGOUT);
    }
}
