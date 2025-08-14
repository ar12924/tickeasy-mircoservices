package microservices.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class GatewaySecurityConfig {

    @Value("${app.security.permit-patterns:/,/favicon.ico,/index.html,/user/**,/common/**,/static/**,/assets/**,/api/media/**}")
    private String permitPatterns;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        String[] permitted = permitPatterns.split("\\s*,\\s*");

        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/**").authenticated()
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers(permitted).permitAll()
                .anyExchange().permitAll()
        );

        // 基礎 JWT 驗證（簡化版本）：若需比對 memberId，可在自訂過濾器中解析 token
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        // CORS
        http.cors(Customizer.withDefaults());

        return http.build();
    }
}


