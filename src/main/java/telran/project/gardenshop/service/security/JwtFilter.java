package telran.project.gardenshop.service.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader(AUTH_HEADER);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorization.substring(BEARER_PREFIX.length());

        try {
            String username = jwtService.extractUserName(jwt);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtService.isValidToken(jwt)) {

                UserDetails user = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT expired");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
            return;
        }

        filterChain.doFilter(request, response);
    }
}


//package telran.project.gardenshop.service.security;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//import org.springframework.web.filter.OncePerRequestFilter;
//import java.io.IOException;
//
//@Service
//public class JwtFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String authorization = request.getHeader("Authorization");
//        if (authorization == null || !authorization.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String jwt = authorization.substring("Bearer ".length());
//
//        String username = null;
//        try {
//            username = jwtService.extractUserName(jwt);
//        } catch (ExpiredJwtException expiredJwtException) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, expiredJwtException.getMessage());
//            filterChain.doFilter(request, response);
//        }
//
//        if (username != null && jwtService.isValidToken(jwt)) {
//            UserDetails user = userDetailsService.loadUserByUsername(username);
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
