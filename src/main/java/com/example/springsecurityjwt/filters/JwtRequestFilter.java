package com.example.springsecurityjwt.filters;

import com.example.springsecurityjwt.services.MyUserDetailsService;
import com.example.springsecurityjwt.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authorizationHeader = httpServletRequest.getHeader("Authorization");

            String username = null;
            String jwt = null;
//        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
//            jwt = authorizationHeader.substring(7);
//            username = jwtUtil.extractUsername(jwt);
//        }
            if (authorizationHeader != null) {
                jwt = authorizationHeader;
                username = jwtUtil.extractUsername(jwt);
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails((new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }catch (Exception e){
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Token");
            return;
        }
    }
}
