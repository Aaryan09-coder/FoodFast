package com.jplProject.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.management.BadAttributeValueExpException;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Retrieve the JWT token from the Authorization header of the request.
        // JwtConstant.JWT_HEADER should be the header key, usually "Authorization".
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);
        //"Bearer token"
        // Check if the token exists and starts with "Bearer "
        if(jwt!=null){
            // Remove the "Bearer " prefix to get the actual JWT token.
            jwt = jwt.substring(7);

            try{
                // Generate a secret key from the stored SECRET_KEY constant.
                // This key will be used to verify the JWT token's signature.
                SecretKey key= Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
                // Parse the JWT token using the secret key to verify and retrieve its claims (payload).
                // Set signing key for token validation
                // Parse the token and get the claims
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

                // Extract the email and authorities (roles) from the claims.
                // Email is used as the identifier, and authorities define user roles or permissions.
                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));
                //ROLE_CUSTOMER, ROLE_ADMIN
                System.out.println("authorities -------- "+authorities);

                // Convert the comma-separated roles string into a list of GrantedAuthority objects.
                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                // Create an Authentication object containing the user email and authorities.
                // UsernamePasswordAuthenticationToken can represent the authentication state of a user.
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
            catch(Exception e){
                // If any exception occurs (e.g., token is invalid or expired),
                // throw a BadCredentialsException to indicate the token is not valid.
                throw new BadCredentialsException("invalid token......");
            }
        }
        // Continue with the remaining filters in the chain (if token is valid or not provided).
        filterChain.doFilter(request,response);
    }
}
