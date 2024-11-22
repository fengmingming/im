package boluo.im.rsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

@Configuration
public class RSocketConfiguration {

    @Bean
    public RSocketStrategies rSocketStrategies(ObjectMapper objectMapper) {
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2JsonEncoder(objectMapper)))
                .decoders(decoders -> decoders.add(new Jackson2JsonDecoder(objectMapper)))
                .build();
    }

    @Bean
    public RSocketRequester.Builder rSocketRequesterBuilder(ObjectMapper objectMapper) {
        return RSocketRequester.builder().rsocketStrategies(rSocketStrategies(objectMapper));
    }

}
