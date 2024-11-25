package boluo.im.client.repository;

import boluo.im.client.Account;
import boluo.im.client.AccountBroker;
import boluo.im.client.Broker;
import boluo.im.common.Constants;
import boluo.im.endpoints.WebSocketSessionRepository;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSetReactive;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.socket.CloseStatus;
import reactor.core.publisher.Mono;

@Slf4j
public class RedissonAccountBrokerRepository implements AccountBrokerRepository, DisposableBean, InitializingBean {

    @Setter
    @Resource
    private RedissonClient redissonClient;
    @Setter
    @Resource
    private WebSocketSessionRepository wsRepository;
    @Getter
    private volatile boolean running;


    @Override
    public void save(AccountBroker ab) {
        Account account = ab.getAccount();
        RSetReactive<Broker> brokers = redissonClient.reactive().getSet(account.toRedisKey());
        brokers.addAll(ab.getBrokers()).subscribe();
    }

    @Override
    public Mono<AccountBroker> find(Account account) {
        RSetReactive<Broker> brokers = redissonClient.reactive().getSet(account.toRedisKey());
        return brokers.readAll().map(its -> {
            AccountBroker ab = new AccountBroker(account);
            ab.getBrokers().addAll(its);
            return ab;
        });
    }

    @Override
    public void remove(AccountBroker ab) {
        Account account = ab.getAccount();
        RSetReactive<Broker> brokers = redissonClient.reactive().getSet(account.toRedisKey());
        brokers.removeAll(ab.getBrokers()).subscribe();
    }

    private void removeSync(AccountBroker ab) {
        Account account = ab.getAccount();
        RSetReactive<Broker> brokers = redissonClient.reactive().getSet(account.toRedisKey());
        brokers.removeAll(ab.getBrokers()).block();
    }

    @Override
    public void destroy() throws Exception {
        running = false;
        wsRepository.findAll().forEach(session -> {
            try{
                removeSync((AccountBroker) session.getAttributes().get(Constants.ACCOUNT_BROKER_ATTR));
                session.close(CloseStatus.NORMAL);
            }catch (Throwable e) {
                //ignore error
                log.warn("close WebSocketSession fail when application closed", e);
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        running = true;
    }
}
