package boluo.im.endpoints;

import boluo.im.config.IMConfig;
import boluo.im.client.Account;
import boluo.im.client.AccountBroker;
import boluo.im.client.Broker;
import boluo.im.client.Device;
import boluo.im.client.repository.AccountBrokerRepository;
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

@Slf4j
public class IMWebSocketHandler extends TextWebSocketHandler {

    private final ApplicationContext applicationContext;

    public IMWebSocketHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try{
            //构建broker
            URI uri = session.getUri();
            UrlQuery query = UrlQuery.of(uri.getQuery(), Charset.defaultCharset());
            //account
            Account account = new Account((String) query.get("tenantId"), (String) query.get("account"));
            //broker
            String ip = applicationContext.getBean(IMConfig.class).getLocalIp();
            if(StrUtil.isBlank(ip)) {
                ip = session.getLocalAddress().getHostString();
            }
            String port = applicationContext.getEnvironment().getProperty("spring.rsocket.server.port", "-1");
            Broker broker = new Broker(ip, Integer.parseInt(port), session.getId());
            Device device = new Device();
            device.setDeviceId((String) query.get("deviceId"));
            device.setDeviceTags((String) query.get("deviceTags"));
            broker.setDevice(device);
            //ab
            AccountBroker ab = new AccountBroker(account);
            ab.getBrokers().add(broker);
            AccountBrokerRepository abRepository = applicationContext.getBean(AccountBrokerRepository.class);
            abRepository.save(ab);
            WebSocketSessionRepository wsRepository = applicationContext.getBean(WebSocketSessionRepository.class);
            wsRepository.save(session);
        }catch (Exception e) {
            handleTransportError(session, e);
            throw e;
        }
    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("id:{} message:{}", session.getId(), message.getPayload());
        Message obj = null;
        try{
            obj = applicationContext.getBean(ObjectMapper.class).readValue(message.getPayload(), Message.class);
            //TODO 保存消息

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
        applicationContext.getBean(MessageService.class).route(obj, message);
    }

    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        //心跳成功
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("sessionId:{} close", session.getId());
        WebSocketSessionRepository wsRepository = applicationContext.getBean(WebSocketSessionRepository.class);
        wsRepository.delete(session);
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("sessionId:{} exception:{}", session.getId(), exception.getMessage(), exception);
        if(session.isOpen()) {
            ObjectMapper om = applicationContext.getBean(ObjectMapper.class);
            ExceptionMessage message = new ExceptionMessage();
            message.setTenantId((String) session.getAttributes().get("tenantId"));
            message.setFrom((String) session.getAttributes().get("account"));
            message.setException(exception.getMessage());
            session.sendMessage(new TextMessage(om.writeValueAsString(message)));
        }
    }

    public void handleTransportError(WebSocketSession session, Message message, Throwable exception) throws Exception {
        log.error("id:{} message:{} exception:{}", session.getId(), message, exception.getMessage(), exception);
        if(session.isOpen()) {
            ObjectMapper om = applicationContext.getBean(ObjectMapper.class);
            ExceptionMessage exceptionMessage = new ExceptionMessage();
            MessageTool.copy(message, exceptionMessage);
            exceptionMessage.setException(exception.getMessage());
            session.sendMessage(new TextMessage(om.writeValueAsString(exceptionMessage)));
        }
    }

}
