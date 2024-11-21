package boluo.im.message;

import boluo.im.endpoints.WebSocketSessionRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Controller
public class MessageResponder {

    @Resource
    private WebSocketSessionRepository wsRepository;

    @MessageMapping("chat.message")
    public Mono<Void> route(@Payload RemoteMessage message) {
        if(log.isDebugEnabled()) {
            log.debug("MessageResponder route sessionId:{} message:{}", message.getSessionId(), message.getMessage());
        }
        Optional<WebSocketSession> opt = wsRepository.find(message.getSessionId());
        if(opt.isPresent()) {
            WebSocketSession session = opt.get();
            if(session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message.getMessage()));
                } catch (IOException e) {
                    log.error("MessageResponder route sendMessage fail sessionId:{} message:{}", message.getSessionId(), message.getMessage(), e);
                }
            }
        }
        return Mono.empty();
    }


}
