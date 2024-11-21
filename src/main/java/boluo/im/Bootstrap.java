package boluo.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@SpringBootApplication
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    @Bean
    public RSocketRequester.Builder rSocketRequesterBuilder() {
        RSocketStrategies strategies = RSocketStrategies.builder().routeMatcher(new PathPatternRouteMatcher()).build();
        return RSocketRequester.builder().rsocketStrategies(strategies);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

}
