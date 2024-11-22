package boluo.im.endpoints;

import boluo.im.client.Account;
import boluo.im.client.AccountBroker;
import boluo.im.client.Broker;
import boluo.im.client.Device;
import boluo.im.client.repository.AccountBrokerRepository;
import boluo.im.config.IMConfig;
import boluo.im.message.Message;
import boluo.im.message.MessageService;
import boluo.im.message.MessageTool;
import boluo.im.message.submessages.ExceptionMessage;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;

@Slf4j
public class IMWebSocketHandler extends TextWebSocketHandler {

    private final IMConfig imConfig;
    private final int port;
    private final AccountBrokerRepository abRepository;
    private final WebSocketSessionRepository wsRepository;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    public IMWebSocketHandler(ApplicationContext applicationContext) {
        this.imConfig = applicationContext.getBean(IMConfig.class);
        this.port = Integer.parseInt(Objects.requireNonNull(applicationContext.getEnvironment().getProperty("spring.rsocket.server.port"), "spring.rsocket.server.port is null"));
        this.abRepository = applicationContext.getBean(AccountBrokerRepository.class);
        this.wsRepository = applicationContext.getBean(WebSocketSessionRepository.class);
        this.objectMapper = applicationContext.getBean(ObjectMapper.class);
        this.messageService = applicationContext.getBean(MessageService.class);
    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try{
            wsRepository.save(session);
            //构建broker
            URI uri = session.getUri();
            UrlQuery query = UrlQuery.of(uri.getQuery(), Charset.defaultCharset());
            //account
            Account account = new Account((String) query.get("tenantId"), (String) query.get("account"));
            //broker
            String ip = imConfig.getLocalIp();
            if(StrUtil.isBlank(ip)) {
                ip = session.getLocalAddress().getHostString();
            }
            Broker broker = new Broker(ip, port, session.getId());
            Device device = new Device();
            device.setDeviceId((String) query.get("deviceId"));
            device.setDeviceTags((String) query.get("deviceTags"));
            broker.setDevice(device);
            //ab
            AccountBroker ab = new AccountBroker(account);
            ab.getBrokers().add(broker);
            abRepository.save(ab);
        }catch (Exception e) {
            handleTransportError(session, e);
            throw e;
        }
    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("id:{} message:{}", session.getId(), message.getPayload());
        Message obj = null;
        try{
            obj = objectMapper.readValue(message.getPayload(), Message.class);
            //将源消息返回，代表确认消息
            session.sendMessage(message);
        }catch (Exception e) {
            if(obj == null) {
                handleTransportError(session, e);
            }else {
                handleTransportError(session, obj, e);
            }
            throw e;
        }
        //路由消息
        try {
            messageService.route(obj, message);
        }catch (Throwable e) {
            //确保出错后不会关闭链接
            log.error("sessionId:{} route fail", session.getId(), e);
        }
    }

    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        //心跳成功
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("sessionId:{} close", session.getId());
        wsRepository.delete(session);

    }

    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("sessionId:{} exception:{}", session.getId(), exception.getMessage(), exception);
    }

    public void handleTransportError(WebSocketSession session, Message message, Throwable exception) throws Exception {
        log.error("id:{} message:{} exception:{}", session.getId(), message, exception.getMessage(), exception);
        if(session.isOpen()) {
            ExceptionMessage exceptionMessage = new ExceptionMessage();
            MessageTool.copy(message, exceptionMessage);
            exceptionMessage.setException(exception.getMessage());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(exceptionMessage)));
        }
    }

}
