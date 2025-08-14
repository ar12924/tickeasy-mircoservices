package microservices.member.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Component
public class JwtAuthFilter extends org.springframework.web.filter.OncePerRequestFilter {

    private byte[] resolveSecret() {
        String env = System.getenv("JWT_SECRET");
        if (env == null || env.isBlank()) {
            return "dev-secret-change-me".getBytes(StandardCharsets.UTF_8);
        }
        // 支援純文字或 base64 編碼
        try {
            return Base64.getDecoder().decode(env);
        } catch (IllegalArgumentException e) {
            return env.getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(resolveSecret()).build().parseClaimsJws(token).getBody();
                String username = claims.getSubject();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ignore) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}


