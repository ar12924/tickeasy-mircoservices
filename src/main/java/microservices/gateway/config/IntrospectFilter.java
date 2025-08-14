package microservices.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class IntrospectFilter extends AbstractGatewayFilterFactory<Object> {

    private final WebClient webClient;

    @Value("${gateway.auth.introspect-url:http://localhost:18082/auth/introspect}")
    private String introspectUrl;

    public IntrospectFilter(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (auth == null || !auth.startsWith("Bearer ")) {
                return Mono.error(new ResponseStatusException(UNAUTHORIZED, "Missing Bearer token"));
            }
            var form = new LinkedMultiValueMap<String, String>();
            form.add("token", auth.substring("Bearer ".length()));
            return webClient.post()
                    .uri(introspectUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(IntrospectResponse.class)
                    .flatMap(body -> {
                        if (body == null || !Boolean.TRUE.equals(body.active)) {
                            return Mono.error(new ResponseStatusException(UNAUTHORIZED, "Invalid token"));
                        }
                        return chain.filter(exchange);
                    });
        };
    }

    public static class IntrospectResponse {
        public Boolean active;
        public String sub;
        public String role;
        public Long exp;
        public Long iat;
        public String iss;
    }
}




