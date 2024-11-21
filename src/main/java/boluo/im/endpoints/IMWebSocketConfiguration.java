package boluo.im.endpoints;

import boluo.im.config.IMConfig;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class IMWebSocketConfiguration implements WebSocketConfigurer, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        IMConfig imConfig = applicationContext.getBean(IMConfig.class);
        String originPatterns = imConfig.getOriginPatterns();
        if(StrUtil.isBlank(originPatterns)) {
            originPatterns = "*";
        }
        registry.addHandler(new IMWebSocketHandler(applicationContext), "/chat")
                .setAllowedOriginPatterns(originPatterns.split(","))
                .addInterceptors(new AuthenticationHandshakeInterceptor(applicationContext));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
