package boluo.im.endpoints;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketSession Repository
 * */
@Component
public class WebSocketSessionRepository implements DisposableBean {

    private final ConcurrentHashMap<String, WebSocketSession> cache = new ConcurrentHashMap<>(2048);

    public Optional<WebSocketSession> find(String sessionId) {
        return Optional.ofNullable(cache.get(sessionId));
    }

    public void save(WebSocketSession session) {
        cache.put(session.getId(), session);
    }

    public void delete(WebSocketSession session) {
        cache.remove(session.getId());
    }

    @Override
    public void destroy() throws Exception {

    }
}
