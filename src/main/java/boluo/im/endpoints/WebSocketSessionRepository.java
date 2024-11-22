package boluo.im.endpoints;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketSession Repository
 * */
@Component
@Slf4j
public class WebSocketSessionRepository {

    private final ConcurrentHashMap<String, WebSocketSession> cache = new ConcurrentHashMap<>(2048);

    public Optional<WebSocketSession> find(String sessionId) {
        return Optional.ofNullable(cache.get(sessionId));
    }

    public Collection<WebSocketSession> findAll() {
        return new ArrayList<>(cache.values());
    }

    public void save(WebSocketSession session) {
        cache.put(session.getId(), session);
    }

    public void delete(WebSocketSession session) {
        cache.remove(session.getId());
    }

}
