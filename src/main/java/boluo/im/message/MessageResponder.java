package boluo.im.message;

import boluo.im.endpoints.WebSocketSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.Setter;
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
@Setter
public class MessageResponder {

    @Resource
    private WebSocketSessionRepository wsRepository;
    @Resource
    private MessageService messageService;
    @Resource
    private ObjectMapper objectMapper;

    @MessageMapping("chat.message.route")
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
                }catch (IOException e) {
                    log.error("MessageResponder route sendMessage fail sessionId:{} message:{}", message.getSessionId(), message.getMessage(), e);
                }
            }
        }
        return Mono.empty();
    }

    @MessageMapping("chat.message.send")
    public Mono<Void> send(@Payload Message message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            return Mono.error(e);
        }
        if(log.isDebugEnabled()) {
            log.debug("chat.message.send {}", json);
        }
        return messageService.route(message, new TextMessage(json));
    }

}
