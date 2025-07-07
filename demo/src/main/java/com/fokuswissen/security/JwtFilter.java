package com.fokuswissen.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fokuswissen.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter
{
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    //Benötigt das Repository um den User zu laden und JwtUtil um den Token zu validieren
    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository)
    {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain)
        throws ServletException, IOException
        {
            if("OPTIONS".equalsIgnoreCase(request.getMethod())) // CORS-Preflight-Requests durchlassen
            {
                filterChain.doFilter(request, response);
                return;
            }
            String path = request.getRequestURI();
            if(path.startsWith("/auth")) //auth soll vom Filter freigestellt sein
            {
                filterChain.doFilter(request, response);
                return;
            }
            
            String authHeader = request.getHeader("Authorization");
            if(authHeader != null && authHeader.startsWith("Bearer "))
            {
                String token = authHeader.substring(7);
                if(jwtUtil.validateToken(token))
                {
                    String username = jwtUtil.extractUsername(token);
                    userRepository.findByUsername(username).ifPresent(user ->
                    {
                        var authentication = new UsernamePasswordAuthenticationToken(
                            user, null, List.of()
                        );
                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
                }
            }
            filterChain.doFilter(request, response);
        }
}
