package microservices.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        String memberUrl = System.getenv().getOrDefault("MEMBER_URL", "http://localhost:8080");
        String notifyUrl = System.getenv().getOrDefault("NOTIFY_URL", "http://localhost:8080");
        String notifyWsUrl = System.getenv().getOrDefault("NOTIFY_WS_URL", "ws://localhost:8080");
        String mediaUrl  = System.getenv().getOrDefault("MEDIA_URL",  "http://localhost:8080");

        return builder.routes()
                .route("member_api", r -> r.path("/api/members/**", "/user/member/**", "/api/member-photos/**")
                        .uri(memberUrl))
                .route("notification_api", r -> r.path("/api/notifications/**").uri(notifyUrl))
                .route("notify_api", r -> r.path("/api/notify/**").uri(notifyUrl))
                .route("notify_ws", r -> r.path("/ws/**").uri(notifyWsUrl))
                .route("media_api", r -> r.path("/api/media/**").uri(mediaUrl))
                .build();
    }
}


