package boluo.im.endpoints;

import boluo.im.config.IMConfig;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Setter
public class IMWebSocketConfiguration implements WebSocketConfigurer {

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private IMConfig imConfig;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String originPatterns = imConfig.getOriginPatterns();
        if(StrUtil.isBlank(originPatterns)) {
            originPatterns = "*";
        }
        registry.addHandler(new IMWebSocketHandler(applicationContext), "/chat")
                .setAllowedOriginPatterns(originPatterns.split(","))
                .addInterceptors(new AuthenticationHandshakeInterceptor(applicationContext));
    }

}
