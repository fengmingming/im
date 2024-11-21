package boluo.im.message;


import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.service.RSocketExchange;
import reactor.core.publisher.Mono;

public interface RemoteRouter {

    @RSocketExchange("chat.message")
    public Mono<Void> route(@Payload RemoteMessage message);

}
