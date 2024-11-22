package boluo.im.client.repository;

import boluo.im.client.Account;
import boluo.im.client.AccountBroker;
import boluo.im.client.Broker;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.redisson.api.RSetReactive;
import org.redisson.api.RedissonClient;
import reactor.core.publisher.Mono;

@Setter
public class RedissonAccountBrokerRepository implements AccountBrokerRepository {

    @Resource
    private RedissonClient redissonClient;

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

}
