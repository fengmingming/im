package boluo.im.message;

import boluo.im.client.Account;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Mono<Void> route(Message message, final TextMessage textMessage) {
        //发给自己
        Account self = new Account(message.getTenantId(), message.getFrom());
        Mono<Void> selfMono = routeUser(self, textMessage).log(this.getClass().getName());
        //发给其他人
        if(message.isGroup()) {//组消息
            Mono<Void> groupMono = accountRepository.findByGroupId(message.getTenantId(), message.getGroupId())
                    .flatMapIterable(Function.identity())
                    .filter(it -> !it.getAccount().equals(message.getFrom()))
                    .flatMap(account -> routeUser(account, textMessage))
                    .log(this.getClass().getName()).then();
            return selfMono.then(groupMono);
        }else {//单个消息
            Account account = new Account(message.getTenantId(), message.getTo());
            Mono<Void> otherMono = routeUser(account, textMessage).log(this.getClass().getName());
            return selfMono.then(otherMono);
        }
    }

    protected Mono<Void> routeUser(Account account, final TextMessage textMessage) {
        return abRepository.find(account).flatMap(ab -> {
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
            return Flux.fromStream(remoteBrokers.stream()).flatMap(broker -> {
                Address address = new Address(broker.getIp(), broker.getPort());
                return rsocketFactory.getRemoteRouter(address).route(new RemoteMessage(broker.getSessionId(), textMessage.getPayload()))
                        .doOnError(e -> log.error("remote rote fail address:{} message:{}", address, textMessage.getPayload(), e))
                        .onErrorComplete();
            }).then();
        });
    }

}
