package in.onkarjamdade.removebg.security;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ClerkJwtAuthFilter extends OncePerRequestFilter {

    @Value("${clerk.issuer}")
    private String clerkIssuer;

    private final ClerkJwksProvider jwksProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if(request.getRequestURI().contains("/api/webhooks/**")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // 1. Get the unverified token headers to read the Key ID ('kid')
                String[] chunks = token.split("\\.");
                String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode headerNode = mapper.readTree(headerJson);
                String kid = headerNode.get("kid").asText();

                // 2. Fetch the corresponding Public Key from your fixed JwksProvider
                java.security.PublicKey publicKey = jwksProvider.getPublicKey(kid);

                // 3. Verify the signature against Clerk's parameters
                io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parserBuilder()
                        .setSigningKey(publicKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // 4. Check Issuer matching parameters
                if (claims.getIssuer().equals(clerkIssuer)) {
                    org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication =
                            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                    claims.getSubject(), null, java.util.Collections.emptyList());

                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (Exception e) {
                // CRITICAL FIX: This forces the hidden error to finally print out in red on your IntelliJ terminal console
                System.err.println("=== CLERK JWT AUTHENTICATION CRASHED ===");
                e.printStackTrace();

                // Send a clear JSON message back to React explaining that authentication failed
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"JWT Verification Failed: " + e.getMessage() + "\"}");
                return; // Halt filter chain execution immediately
            }
        }

        filterChain.doFilter(request, response);
    }
}