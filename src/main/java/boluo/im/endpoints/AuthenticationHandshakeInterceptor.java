package boluo.im.endpoints;

import boluo.im.client.Account;
import boluo.im.common.URLUtil;
import boluo.im.config.IMConfig;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;

public class AuthenticationHandshakeInterceptor implements HandshakeInterceptor {

    private final IMConfig imConfig;
    private final WebClient webClient;

    public AuthenticationHandshakeInterceptor(ApplicationContext applicationContext) {
        this.imConfig = applicationContext.getBean(IMConfig.class);
        this.webClient = applicationContext.getBean(WebClient.class);
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //身份验证
        URI uri = request.getURI();
        UrlQuery query = UrlQuery.of(uri.getQuery(), Charset.defaultCharset());
        Account account = new Account((String) query.get("tenantId"), (String) query.get("account"));
        attributes.put("tenantId", account.getTenantId());
        attributes.put("account", account.getAccount());
        if(StrUtil.isNotBlank(imConfig.getAuthUrl())) {
            String url = URLUtil.appendQuery(imConfig.getAuthUrl(), uri.getQuery());
            return Boolean.TRUE.equals(webClient.get().uri(url).exchangeToMono(res -> {
                if (res.statusCode().is2xxSuccessful()) {
                    return Mono.just(true);
                }
                return Mono.just(false);
            }).block(Duration.ofSeconds(imConfig.getAuthTimeout())));
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}

}
