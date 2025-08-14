package microservices.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
// keep gateway auth minimal; rely on oauth2ResourceServer().jwt() in GatewaySecurityConfig
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class NotifyMemberGuardFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String path = req.getURI().getPath();
        if (!path.startsWith("/api/notify/")) return chain.filter(exchange);

        // 簡易 RBAC：需有已驗證 JWT，並可選擇性比對 memberId
        return exchange.getPrincipal()
                .flatMap(p -> chain.filter(exchange))
                .switchIfEmpty(Mono.defer(() -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }));
    }
}


