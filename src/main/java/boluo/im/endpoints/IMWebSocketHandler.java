package boluo.im.endpoints;

import boluo.im.client.AccountBroker;
import boluo.im.client.Broker;
import boluo.im.client.Device;
import boluo.im.client.repository.AccountBrokerRepository;
import boluo.im.client.repository.RedissonAccountBrokerRepository;
import boluo.im.common.Constants;
import boluo.im.config.IMConfig;
import boluo.im.message.Message;
import boluo.im.message.MessageService;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class IMWebSocketHandler extends TextWebSocketHandler {

    private final ApplicationContext applicationContext;
    private final int port;
    private final AccountBrokerRepository abRepository;
    private final WebSocketSessionRepository wsRepository;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    private final String ip;

    public IMWebSocketHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.port = applicationContext.getBean(RSocketProperties.class).getServer().getPort();
        this.abRepository = applicationContext.getBean(AccountBrokerRepository.class);
        this.wsRepository = applicationContext.getBean(WebSocketSessionRepository.class);
        this.objectMapper = applicationContext.getBean(ObjectMapper.class);
        this.messageService = applicationContext.getBean(MessageService.class);
        String localIp = applicationContext.getBean(IMConfig.class).getLocalIp();
        if(StrUtil.isBlank(localIp)) {
            localIp = NetUtil.getLocalhostStr();
            if(localIp == null) {
                throw new IllegalArgumentException("无法获得本地ip地址，请配置boluo.im.localIp");
            }
        }
        this.ip = localIp;
    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try{
            wsRepository.save(session);
            //构建broker
            AccountBroker ab = (AccountBroker) session.getAttributes().get(Constants.ACCOUNT_BROKER_ATTR);
            Device device = (Device) session.getAttributes().get(Constants.DEVICE_ATTR);
            //broker
            Broker broker = new Broker(ip, port, session.getId());
            broker.setDevice(device);
            //ab
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
        }catch (Exception e) {
            handleTransportError(session, e);
            throw e;
        }
        //路由消息
        try {
            messageService.route(obj, message).subscribe();
        }catch (Throwable e) {
            //确保出错后不会关闭链接
            log.error("sessionId:{} route fail", session.getId(), e);
        }
    }

    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        //心跳成功
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("sessionId:{} close:{}", session.getId(), closeStatus);
        AccountBroker ab = (AccountBroker) session.getAttributes().get(Constants.ACCOUNT_BROKER_ATTR);
        if(abRepository instanceof RedissonAccountBrokerRepository rabRepository) {
            if(rabRepository.isRunning()) {
                rabRepository.remove(ab);
            }
        }else {
            abRepository.remove(ab);
        }
        wsRepository.delete(session);
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(applicationContext instanceof Lifecycle lf && lf.isRunning()) {
            log.error("sessionId:{} exception:{}", session.getId(), exception.getMessage(), exception);
        }else {
            //ignore 容器关闭时的错误
            log.warn("sessionId:{} exception:{}", session.getId(), exception.getMessage());
        }
    }

}
