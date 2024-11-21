package boluo.im.message;

import boluo.im.client.Account;
import boluo.im.client.AccountBroker;
import boluo.im.client.Broker;
import boluo.im.client.repository.AccountBrokerRepository;
import boluo.im.client.repository.AccountRepository;
import boluo.im.endpoints.WebSocketSessionRepository;
import boluo.im.rsocket.Address;
import boluo.im.rsocket.RSocketFactory;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Setter
@Service
@Slf4j
public class MessageService {

    @Resource
    private AccountBrokerRepository abRepository;
    @Resource
    private WebSocketSessionRepository wsRepository;
    @Resource
    private RSocketFactory rsocketFactory;
    @Resource
    private AccountRepository accountRepository;

    public void route(Message message, final TextMessage textMessage) {
        if(message.isGroup()) {//组消息
            accountRepository.findByGroupId(message.getTenantId(), message.getGroupId())
                    .flatMapIterable(Function.identity())
                    .doOnNext(account -> routeUser(account, textMessage))
                    .subscribe();
        }else {//单个消息
            Account account = new Account(message.getTenantId(), message.getTo());
            routeUser(account, textMessage);
        }
    }

    protected void routeUser(Account account, final TextMessage textMessage) {
        AccountBroker ab = abRepository.find(account);
        if(ab == null) return;
        List<Broker> remoteBrokers = new ArrayList<>();
        List<WebSocketSession> localWss = new ArrayList<>();
        for(Broker broker : ab.getBrokers()) {
            Optional<WebSocketSession> opt = wsRepository.find(broker.getSessionId());
            if(opt.isPresent()) {
                localWss.add(opt.get());
            }else {
                remoteBrokers.add(broker);
            }
        }
        //处理本地
        localWss.forEach(session -> {
            try{
                if(session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }catch (Throwable e) {
                log.error("rote fail id:{} message:{}", session.getId(), textMessage.getPayload(), e);
            }
        });
        //处理远程
        remoteBrokers.forEach(broker -> {
            Address address = new Address(broker.getIp(), broker.getPort());
            try{
                rsocketFactory.getRemoteRouter(address).route(new RemoteMessage(broker.getSessionId(), textMessage.getPayload())).subscribe();
            }catch (Throwable e) {
                log.error("remote rote fail address:{} message:{}", address, textMessage.getPayload(), e);
            }
        });
    }

}
