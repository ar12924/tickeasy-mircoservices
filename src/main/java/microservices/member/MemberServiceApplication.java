package microservices.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@ComponentScan(
        basePackages = {"microservices.member", "user.member.service", "user.member.dao", "common.controller"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "user\\..*\\.controller\\..*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "common\\.config\\..*")
        }
)
@EntityScan({"microservices.member.vo"})
public class MemberServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberServiceApplication.class, args);
    }
}


